package com.imooc.mall.exception;

import com.imooc.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理统一异常的handler
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    // 获取日志对象
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        log.error("Default Exception : ", e);
        return ApiRestResponse.error(ImoocMailExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler({ImoocMallException.class})
    @ResponseBody
    public Object handleImoocMallException(ImoocMallException e){
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException: " + e);
        return handleBindingResult(e.getBindingResult());
    }

    private ApiRestResponse handleBindingResult(BindingResult result){
        // 把异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        if(result.hasErrors()){
            List<ObjectError> errors = result.getAllErrors();
            for(ObjectError error : errors){
                list.add(error.getDefaultMessage());
            }
        }
        if(list.isEmpty()){
            return ApiRestResponse.error(ImoocMailExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(ImoocMailExceptionEnum.REQUEST_PARAM_ERROR.code, list.toString());
    }
}
