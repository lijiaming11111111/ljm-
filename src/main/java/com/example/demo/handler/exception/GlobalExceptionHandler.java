package com.example.demo.handler.exception;

import com.example.demo.result.Result;
import com.example.demo.exception.BaseException;
import com.example.demo.result.Result;
import com.example.demo.vo.ValidationError;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理
 */

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
@Hidden
public class GlobalExceptionHandler {


    /**
     * 处理参数验证异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Result<List<ValidationError>>> handleValidException(MethodArgumentNotValidException e){
        List<ValidationError> validationErrors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            ValidationError v = new ValidationError();
            v.setField(error.getField());
            v.setMessage(error.getDefaultMessage());
            validationErrors.add(v);
        }
        Result<List<ValidationError>> result = Result.error("字段验证错误",validationErrors);
        return new ResponseEntity<>(result, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public ResponseEntity<Result<String>> handleBaseException(BaseException e) {
        // 用 Result 封装异常信息，code 设为 0（或你定义的失败码），msg 设为异常消息
        Result<String> result = Result.error(e.getMessage());
        // 根据业务需求设置 HTTP 状态码（如 BAD_REQUEST、INTERNAL_SERVER_ERROR 等）
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }


}
