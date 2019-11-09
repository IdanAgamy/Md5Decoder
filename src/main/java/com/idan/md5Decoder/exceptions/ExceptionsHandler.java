package com.idan.md5Decoder.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ResponseBody
@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(ApplicationException.class)
    public ApplicationException handleApplicationException(HttpServletResponse response, ApplicationException exception) {
        response.setStatus(exception.getErrorType().getNumber());
        return exception;
    }
}
