package org.stonexthree.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.UserExtendProxy;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MyUserDataPersistence {
    private File persistenceFile;
    private static final String FILE_NAME = "users.data";
    public MyUserDataPersistence(PersistenceManager manager) {
        this.persistenceFile = new File(manager.getBaseDir(),FILE_NAME);
    }
    public Map<String,UserDetails> loadUserMap() {
        if(!persistenceFile.exists()|| persistenceFile.isDirectory()){
            try {
                persistenceFile.createNewFile();
            } catch (IOException e) {
                log.error(e.getMessage());
                return new HashMap<>();
            }
        }
        Map<String,UserDetails> result = new HashMap<>();
        try(ObjectInputStream is = new ObjectInputStream(
                new FileInputStream(persistenceFile))){
            Map<String,UserDetails> userMap = (Map<String, UserDetails>) is.readObject();
            result.putAll(userMap);
        }catch (EOFException eofException){
            return new HashMap<>();
        }
        catch (Exception exception){
            log.error(exception.getMessage());
        }
        return result;
    }

    public void saveUserMap(Map<String, UserExtendProxy> userDetails) throws  IOException{
        if(!persistenceFile.exists()|| persistenceFile.isDirectory()){
            persistenceFile.createNewFile();
        }
        try(ObjectOutputStream os = new ObjectOutputStream(
                new FileOutputStream(persistenceFile))){
            os.writeObject(userDetails);
        }
    }
}
