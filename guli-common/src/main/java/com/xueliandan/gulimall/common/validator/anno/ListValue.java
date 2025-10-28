package com.xueliandan.gulimall.common.validator.anno;

import com.xueliandan.gulimall.common.validator.ListValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author zxb 2025/8/22 21:06
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {ListValueValidator.class})
public @interface ListValue {

    String message() default "{com.xueliandan.gulimall.common.validator.anno.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // 注解的属性只能是基本类型、String、Class、枚举、注解以及前面的数组，因此包装类 Integer[] 是不行的
    int[] values() default {1, 0};
}
