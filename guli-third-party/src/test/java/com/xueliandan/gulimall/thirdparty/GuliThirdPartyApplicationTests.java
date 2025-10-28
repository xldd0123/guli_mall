package com.xueliandan.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//@Runwith(SpringRunner.class)
@SpringBootTest
class GuliThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Test
    void contextLoads() {
    }

    @Test
    public void updateFile() throws IOException {
        File file = new File("C:\\Users\\wszgr\\Pictures\\Views\\mountain2.png");
        PutObjectRequest putObjectRequest = new PutObjectRequest("edu-zxb-hangzhou", "2025/mmmm.png", new FileInputStream(file));

        ossClient.putObject(putObjectRequest);
    }

}
