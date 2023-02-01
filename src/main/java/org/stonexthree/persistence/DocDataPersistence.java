package org.stonexthree.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.Document;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author stonexthree
 */
@Component
@Slf4j
public class DocDataPersistence {
    private File docSaveDir;

    private ObjectPersistenceHandler<Map<String, Set<Document>>> objectPersistenceHandler;

    public DocDataPersistence(@Value("${app-config.storage.persistence.file.doc.holder}") String holderFile,
                              @Value("${app-config.storage.persistence.file.doc.dir}") String docDir,
                              PersistenceManager manager) throws IOException {
        holderFile = holderFile==null?"doc.data":holderFile;
        this.objectPersistenceHandler = manager.getHandler(holderFile, HashMap::new);
        docDir = docDir == null?"docs":docDir;
        docSaveDir = new File(manager.getBaseDir(), docDir);
        if (!this.docSaveDir.exists()) {
            docSaveDir.mkdirs();
        }
    }

    public Map<String,Set<Document>> loadMap() throws IOException {
        return objectPersistenceHandler.readObject();
    }

    public void writeMap(Map<String,Set<Document>> map) throws IOException {
        objectPersistenceHandler.writeObject(map);
    }

    public String writeDocToFile(String content, String targetFileName) throws IOException {
        File targetFile = new File(docSaveDir, targetFileName);
        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(content);
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw e;
        }
        return targetFile.getName();
    }

    public String writeDocToFile(String content) throws IOException {
        String fileName = UUID.randomUUID().toString();
        File targetFile = new File(docSaveDir, fileName);
        targetFile.createNewFile();
        try (FileWriter fileWriter = new FileWriter(targetFile)) {
            fileWriter.write(content);
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw e;
        }
        return fileName;
    }

    public String readDoc(String fileName) throws IOException {
        File targetFile = new File(docSaveDir, fileName);
        StringBuilder stringBuilder = new StringBuilder();
        String str;
        try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(targetFile))) {
            while ((str = fileBufferReader.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw e;
        }
        return stringBuilder.toString();
    }

    public void deleteDoc(String fileLocation) {
        File target = new File(docSaveDir, fileLocation);
        target.delete();
    }
}

