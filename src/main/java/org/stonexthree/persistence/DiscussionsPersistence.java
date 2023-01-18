package org.stonexthree.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.DiscussionTopic;

import java.io.*;
import java.util.*;


@Component
@Slf4j
public class DiscussionsPersistence {
    private static String SAVE_FILE = "discussions.data";
    private File saveFile;

    public DiscussionsPersistence(PersistenceManager persistenceManager) throws IOException {
        this.saveFile = new File(persistenceManager.getBaseDir(),SAVE_FILE);
        if(!saveFile.isFile()){
            saveFile.createNewFile();
        }
    }
    public void saveDiscussions(List<DiscussionTopic> list) throws IOException {
        if(!saveFile.exists()){
            saveFile.createNewFile();
        }
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(saveFile))){
            os.writeObject(list);
        }
    }
    public List<DiscussionTopic> loadDiscussions() throws IOException {
        if(!saveFile.isFile()){
            saveFile.createNewFile();
            return new ArrayList<>();
        }
        List<DiscussionTopic> result;
        try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(saveFile))){
            result =(List<DiscussionTopic>) is.readObject();
        }catch (Exception e){
            log.warn(e.getMessage());
            result = new ArrayList<>();
        }
        return result;
    }
}