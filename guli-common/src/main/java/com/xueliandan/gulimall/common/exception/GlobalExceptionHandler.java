package com.xueliandan.gulimall.common.exception;

import com.xueliandan.gulimall.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zxb 2025/8/21 11:34
 */
@RestControllerAdvice(basePackages = "com.xueliandan.gulimall")
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Throwable.class)
    public R wrapException(Throwable exception) {
        log.error("程序出现异常，异常信息为: {}", exception.getMessage(), exception);
        return R.error(exception.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R methodArgumentNotValidExceptionWrapper(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMsgMap = new HashMap<>();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                String field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                errorMsgMap.put(field, defaultMessage);
            }
            return R.error("参数校验异常").put("errorMsgMap", errorMsgMap);
        } else {
            return R.error();
        }
    }


}
