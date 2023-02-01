package org.stonexthree.domin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.stonexthree.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class FileServiceImpl implements FileService{
    private PersistenceManager persistenceManager;
    //private static String FILE_STORAGE_DIR_NAME = "pictures";
    private File saveDir;
    public FileServiceImpl(@Value("${app-config.storage.persistence.file.picture-dir}")String dir,
                           PersistenceManager persistenceManager)throws IOException{
        this.persistenceManager = persistenceManager;
        dir = dir==null?"pictures":dir;
        this.saveDir = new File(persistenceManager.getBaseDir(),dir);
        if(!saveDir.isDirectory()){
            saveDir.mkdirs();
        }
    }

    @Override
    public List< String> fileUpload(MultipartFile[] files) throws IOException {
        Map<String,String> result = new HashMap<>(1);
        for(MultipartFile file : files){
            String[] cutArr = file.getOriginalFilename().split("\\.");
            String extendStr = "."+cutArr[cutArr.length-1];
            File saveFile;
            do{
                saveFile = new File(saveDir,UUID.randomUUID()+extendStr);
            }while (saveFile.exists());

            file.transferTo(saveFile);
            result.put(file.getOriginalFilename(), saveFile.getName());
        }
        return result.values().stream().toList();
    }

    @Override
    public File getFile(String location) {
        File result = new File(saveDir,location);
        if(!result.isFile()){
            return null;
        }
        return result;
    }
}
