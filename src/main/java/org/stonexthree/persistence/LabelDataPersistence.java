package org.stonexthree.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class LabelDataPersistence {
    private PersistenceManager manager;
    private String saveFileName = "label.data";
    private File saveFile;

    public LabelDataPersistence(PersistenceManager manager) {
        this.manager = manager;
        saveFile = new File(manager.getBaseDir(),saveFileName);
    }

    public Map<String, Set<String>> loadLabelMap() throws IOException{
        if(!saveFile.exists()){
            saveFile.createNewFile();
            return new HashMap<>(10);
        }
        Map<String,Set<String>> result;
        try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(saveFile))){
            result =(Map<String, Set<String>>) is.readObject();
        }catch (Exception e){
            log.warn(e.getMessage());
            result = new HashMap<>(10);
        }
        return result;
    }

    public void savLabelMap(Map<String, Set<String>> map) throws IOException{
        if(!saveFile.exists()){
            saveFile.createNewFile();
        }
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(saveFile))){
            os.writeObject(map);
        }
    }
}
