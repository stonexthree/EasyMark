package org.stonexthree.web.utils;

public class RestResponseFactory {
    public static CommonResponse<String> createSuccessResponse(){
        return new CommonResponse(ErrorCodeUtil.SUCCESS,"success","");
    }
    public static CommonResponse<String> createFailedResponse(){
        return new CommonResponse(ErrorCodeUtil.FAILED,"failed","");
    }
    public static <T> CommonResponse createSuccessResponseWithData(T Data){
        return new CommonResponse(ErrorCodeUtil.SUCCESS,"success",Data);
    }
}
