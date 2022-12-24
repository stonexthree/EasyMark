package org.stonexthree.web;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stonexthree.domin.FileService;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {
    private FileService fileService;
    public FileController(FileService fileService){
        this.fileService = fileService;
    }


    @PostMapping("/upload")
    public CommonResponse uploadFiles(@RequestParam("file")MultipartFile[] files) throws IOException {
        return RestResponseFactory.createSuccessResponseWithData(fileService.fileUpload(files));
    }

    /**
     * 配合nginx的 http_auth_request_module 进行控制
     * nginx提供文件服务，本接口控制是否放行本次请求
     *
     * @param id
     * @param response
     */
    @GetMapping("/picture/{id}")
    public void getFile(@PathVariable("id") String id, HttpServletResponse response){
        File target = fileService.getFile(id);
        if(target == null ){
            response.setStatus(404);
        }
    }
}
