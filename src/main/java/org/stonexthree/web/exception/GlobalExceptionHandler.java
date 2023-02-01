package org.stonexthree.web.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.ErrorCodeUtil;
import org.stonexthree.web.utils.RestResponseFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler
    public CommonResponse exceptionHandler(Exception e) throws Exception{
        if(e instanceof IllegalArgumentException){
            return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR).setMessage(e.getMessage());
        }
        e.printStackTrace();
        return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.SERVICE_ERROR).setMessage(e.getMessage());
        //throw e;
    }
}
