package org.stonexthree.domin;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface FileService {
    /**
     * 文件上传，返回文件名称和存储凭证的映射
     * 文件名称取 MultipartFile.getOriginalFilename()
     * @param files 文件数组
     * @return 成功上传的文件存储凭证
     */
    List<String> fileUpload(MultipartFile[] files) throws IOException;

    /**
     * 获取文件
     * @param location 存储凭证，根据凭证获取对应文件
     * @return
     */
    File getFile(String location);
}
