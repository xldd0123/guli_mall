package com.xueliandan.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueliandan.gulimall.common.utils.PageUtils;
import com.xueliandan.gulimall.common.utils.Query;
import com.xueliandan.gulimall.product.dao.PmsCategoryDao;
import com.xueliandan.gulimall.product.entity.PmsAttrGroupEntity;
import com.xueliandan.gulimall.product.entity.PmsCategoryEntity;
import com.xueliandan.gulimall.product.entity.vo.Catelog2Vo;
import com.xueliandan.gulimall.product.service.PmsCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("pmsCategoryService")
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryDao, PmsCategoryEntity> implements PmsCategoryService {

    private final Logger log = LoggerFactory.getLogger(PmsCategoryServiceImpl.class);

    private StringRedisTemplate stringRedisTemplate;

    private RedissonClient redissonClient;

    @Autowired
    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsCategoryEntity> page = this.page(
                new Query<PmsCategoryEntity>().getPage(params),
                new QueryWrapper<PmsCategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<PmsCategoryEntity> listTree() {
        List<PmsCategoryEntity> allCategories = this.list();
        if (!CollectionUtils.isEmpty(allCategories)) {
            return allCategories.stream().filter(pmsCategoryEntity -> Objects.nonNull(pmsCategoryEntity.getParentCid()))
                    .filter(pmsCategoryEntity -> Objects.equals(pmsCategoryEntity.getParentCid(), 0L))
                    .peek(pmsCategoryEntity -> pmsCategoryEntity.setChildren(buildTree(pmsCategoryEntity, allCategories)))
                    .sorted(Comparator.comparing(PmsCategoryEntity::getSort, Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void enrichCategoryPath(PmsAttrGroupEntity pmsAttrGroup) {
        Long categoryId = pmsAttrGroup.getCatelogId();
        Stack<Long> stack = new Stack<>();
        doEnrichCategoryPath(categoryId, stack);
        Long[] categoryPath = new Long[stack.size()];
        int stackSize = stack.size();
        for (int i = 0; i < stackSize; i++) {
            categoryPath[i] = stack.pop();
        }
        pmsAttrGroup.setCategoryPath(categoryPath);
    }

    @Override
    public void doEnrichCategoryPath(Long categoryId, Stack<Long> stack) {
        if (Objects.isNull(categoryId) || categoryId == 0) return;
        stack.push(categoryId);
        PmsCategoryEntity category = baseMapper.selectById(categoryId);
        if (Objects.nonNull(category) && Objects.nonNull(category.getParentCid())) {
            Long parentCid = category.getParentCid();
            doEnrichCategoryPath(parentCid, stack);
        }
    }

    @Override
    public List<PmsCategoryEntity> findFirstLevelCategory() {
        return baseMapper.selectList(new QueryWrapper<PmsCategoryEntity>().eq("cat_level", 1));
    }

    // TODO 堆外内存溢出异常 OutOfDirectMemoryError
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        // 一级分类是key，然后二级分类是 List value
        // 拿到一级分类
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String catalogJson = stringValueOperations.get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            log.info("缓存未命中, 查询数据库...");
            return getCatalogJsonByRedissonLock();
        }
        log.info("缓存命中...直接返回");
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
    }


    /**
     * 现在使用 Redisson 能做到分布式锁的效果，能抗住并发了，但是，又引入一个新的问题，数据的一致性，如何保证？
     * 缓存一致性，常用的里两个场景:
     * 1.双写模式 ====> 改完数据库中的数据，同步修改缓存数据，查询出所有菜单缓存，然后修改后再放入缓存。
     * 双写数据库存在脏写问题，假设 A 线程修改为 1 写数据库，写缓存的时候慢了。B 线程来了，修改为 2 写数据库，
     * 并且 B 机器快，直接写到缓存，改为 2。此时 A 才开始写缓存，把 1 覆盖调 2，数据被脏写。
     * 那么解决脏写的一个办法就是，上锁。等待写数据库和缓存全部都完成，才释放锁。
     * 另外，如果业务允许数据脏读，允许用户看到不一致的数据，那么缓存里的数据脏了也问题不大。当缓存到期，则脏数据自动删除。
     * 双写模式，数据写到数据库的那一刻，和写到缓存，用户读到缓存的这一刻，总会有一个时间差。就看这个时间差有多少，业务上能容忍多久。
     * 读到最新数据总会有延迟，是 1ms? 十分钟? 一天? 就看业务上容忍多久，但不管怎样，最终总会一致。这就是双写的最终一致性。
     * <p>
     * 2.失效模式 ====> 改完数据库中的数据，直接将缓存中数据删除，下次再访问，从数据库中获取最新数据放入缓存
     * 同样，失效模式也会有问题。
     *
     * @return 一级分类数据
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonByRedissonLock() {

        // 只要分布式锁的名称相同，则就是同一把锁。这一点要非常注意。
        // 所以锁名不能太随意，不能都叫 lock，这样不同业务锁相同，锁的粒度太大，容易造成无谓的等待。
        // 锁的粒度要小，越小越快，但是也不能太小，太小开销大。
        // 通常建议是粒度可以小到具体某一个商品，譬如 11 号商品，那么锁名就推荐 product-11-lock
        // 那么 12 号商品的锁就推荐 product-12-lock，以此类推。
        // 那么这里是菜单分类的数据，那么锁就可以叫做 catalog-lock
        RLock lock = redissonClient.getLock("catalog-lock");
        lock.lock();
        try {
            log.info("获取分布式锁 {} 成功!", "catalog-lock");
            Map<String, List<Catelog2Vo>> catelogFromDB = doGetCatelogFromDB();
            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(catelogFromDB));
            return catelogFromDB;
        } finally {
            lock.unlock();
        }

    }


    // 按理说还有锁的自动续期，譬如我的业务逻辑很长，还没执行完，锁就过期了，那么其它线程就能拿到锁对数据进行修改，就可能出现脏写等现象
    // 如果不想做，则将锁的时间放长一点，再通过业务来控制执行完后必须将锁释放，那么也是可行的。
    public Map<String, List<Catelog2Vo>> getCatalogJsonByDistributeLock() {
        // 获取分布式锁和设置 key 过期时间必须是原子的
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)) {
            try {
                log.info("获取分布式锁成功!");
                Map<String, List<Catelog2Vo>> catelogFromDB = doGetCatelogFromDB();
                stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(catelogFromDB));
                return catelogFromDB;
            } finally {
                // 通过 uuid 判断来删除自己的锁，但是这样并非是原子的
//                if (uuid.equals(stringRedisTemplate.opsForValue().get("lock"))) {
//                    stringRedisTemplate.delete("lock");
//                }
                // 采用官方推荐的 lua 脚本方式进行删锁，保证原子性
                log.info("释放分布式锁!");
                try {
                    String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                    Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList("lock"), uuid);
                    log.info("释放分布式锁成功，返回 : {}", lock1);
                } catch (Exception e) {
                    log.error("释放分布式锁失败!", e);
                }
            }
        } else {
            // 模拟自旋，不断重试
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("retry to attempt lock...");
            return getCatalogJsonByDistributeLock();
        }
    }


    public Map<String, List<Catelog2Vo>> getCatalogJsonUsingJVMLock() {

        // spring 默认单例模式，因此 this 对象在容器中只有一份，多个线程共用一把锁，可用
        synchronized (this) {
            return doGetCatelogFromDB();
        }
    }

    private @NonNull Map<String, List<Catelog2Vo>> doGetCatelogFromDB() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isNotBlank(catalogJson)) {
            log.info("命中，其它线程查询侯放入缓存!");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        Map<String, List<Catelog2Vo>> retVal = new HashMap<>();
        log.info("查询了数据库。。。");
        // 一级分类是key，然后二级分类是 List value
        // 拿到一级分类
        List<PmsCategoryEntity> allCategories = baseMapper.selectList(null);
        List<PmsCategoryEntity> secondCateLogs = allCategories.stream()
                .filter(category -> category.getCatLevel().equals(2)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(secondCateLogs)) return new HashMap<>();


        List<PmsCategoryEntity> thirdCateLogs = allCategories.stream()
                .filter(category -> category.getCatLevel().equals(3)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdCateLogs)) return new HashMap<>();

        Map<Long, List<PmsCategoryEntity>> firstCatlogMap = secondCateLogs.stream().collect(Collectors.groupingBy(PmsCategoryEntity::getParentCid));
        Map<Long, List<PmsCategoryEntity>> secondCatlogMap = thirdCateLogs.stream().collect(Collectors.groupingBy(PmsCategoryEntity::getParentCid));
        firstCatlogMap.forEach((catId, children) -> {
            List<Catelog2Vo> catelog2Vos = children.stream().map(secondLevelCategory -> {
                Catelog2Vo catelog2Vo = new Catelog2Vo();
                catelog2Vo.setCatalog1Id(catId.toString());
                catelog2Vo.setId(secondLevelCategory.getCatId().toString());
                catelog2Vo.setName(secondLevelCategory.getName());

                List<PmsCategoryEntity> thirdCatlogs = secondCatlogMap.get(secondLevelCategory.getCatId());
                if (!CollectionUtils.isEmpty(thirdCatlogs)) {
                    List<Catelog2Vo.Category3Vo> category3Vos =
                            thirdCatlogs.stream().map(thirdLevelCategory -> {
                                Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo();
                                category3Vo.setCatalog2Id(secondLevelCategory.getCatId().toString());
                                category3Vo.setId(thirdLevelCategory.getCatId().toString());
                                category3Vo.setName(thirdLevelCategory.getName());
                                return category3Vo;
                            }).collect(Collectors.toList());
                    catelog2Vo.setCatalog3List(category3Vos);
                }
                return catelog2Vo;
            }).collect(Collectors.toList());
            retVal.put(catId.toString(), catelog2Vos);
        });
        return retVal;
    }

    /**
     * 这种方式递归构建树结构，频繁查库，效率低下!
     *
     * @param firstLevelCategories 一级分类
     */
    private void buildTreeWithoutEffective(List<PmsCategoryEntity> firstLevelCategories) {
        if (CollectionUtils.isEmpty(firstLevelCategories)) return;
        for (PmsCategoryEntity firstLevelCategory : firstLevelCategories) {
            Long catId = firstLevelCategory.getCatId();
            List<PmsCategoryEntity> children = baseMapper.selectList(new QueryWrapper<PmsCategoryEntity>().eq("parent_cid", catId));
            firstLevelCategory.setChildren(children);
            if (!CollectionUtils.isEmpty(children)) {
                buildTreeWithoutEffective(children);
            }
        }
    }

    public List<PmsCategoryEntity> buildTree(PmsCategoryEntity root, List<PmsCategoryEntity> allCategories) {
        Long catId = root.getCatId();
        List<PmsCategoryEntity> children = allCategories.stream()
                .filter(pmsCategoryEntity -> pmsCategoryEntity.getParentCid().equals(catId))
                .sorted(Comparator.comparing(PmsCategoryEntity::getSort, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
        root.setChildren(children);
        if (!CollectionUtils.isEmpty(children)) {
            // 不断的往子分类中递归添加子分类
            for (PmsCategoryEntity child : children) {
                child.setChildren(buildTree(child, allCategories));
            }
        }
        // 返回的 children 在前面的递归中都将孩子节点都设置好了
        return children;
    }

}