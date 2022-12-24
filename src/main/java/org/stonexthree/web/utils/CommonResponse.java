package org.stonexthree.web.utils;

import lombok.Data;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

@Data
public class CommonResponse<T> {
    String code;
    String message;
    T Data;

    protected CommonResponse (Integer code,String message,T Data){
        this.code = code.toString();
        this.message = message;
        this.Data = Data;
    }

    protected CommonResponse (String code,String message,T Data){
        this.code = code;
        this.message = message;
        this.Data = Data;
    }

    public CommonResponse setMessage(String message){
        this.message = message;
        return this;
    }

    public CommonResponse setCode(Integer code){
        this.code=code.toString();
        return this;
    }
    public CommonResponse setCode(String code){
        this.code=code;
        return this;
    }

}
