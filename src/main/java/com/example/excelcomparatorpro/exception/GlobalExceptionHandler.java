package com.example.excelcomparatorpro.exception;

import com.example.excelcomparatorpro.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ServiceException.class)
    public Result handGlobException(ServiceException e) {
       // e.printStackTrace();
        return Result.error().message(e.getMessage());
    }


}
