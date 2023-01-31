package org.stonexthree.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.DocHolder;

import java.io.*;
import java.util.UUID;

/**
 * @author stonexthree
 */
@Component
@Slf4j
public class DocDataPersistence {
    //private File persistenceFile;
    //private static final String HOLDER_FILE_NAME = "doc.data";
    //private static final String DOC_SAVE_DIR = "docs";
    private File docSaveDir;

    private ObjectPersistenceHandler<DocHolder> objectPersistenceHandler;

    public DocDataPersistence(@Value("${app-config.storage.persistence.file.doc.holder}") String holderFile,
                              @Value("${app-config.storage.persistence.file.doc.dir}") String docDir,
                              PersistenceManager manager) throws IOException {
        //this.persistenceFile = new File(manager.getBaseDir(), HOLDER_FILE_NAME);
        this.objectPersistenceHandler = manager.getHandler(holderFile,DocHolder::new);
        docDir = docDir == null?"docs":docDir;
        docSaveDir = new File(manager.getBaseDir(), docDir);
        if (!this.docSaveDir.exists()) {
            docSaveDir.mkdirs();
        }
    }

    public DocHolder loadHolder() throws IOException {
/*        if (!persistenceFile.exists() || persistenceFile.isDirectory()) {
            try {
                persistenceFile.createNewFile();
            } catch (IOException e) {
                log.error(e.getMessage());
                return new DocHolder();
            }
        }
        try (ObjectInputStream is = new ObjectInputStream(
                new FileInputStream(persistenceFile))) {
            return (DocHolder) is.readObject();
        } catch (EOFException eofException) {
            return new DocHolder();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new DocHolder();
        }*/
        return objectPersistenceHandler.readObject();
    }

    public void writeHolder(DocHolder holder) throws IOException {
        /*if (!persistenceFile.exists() || persistenceFile.isDirectory()) {
            persistenceFile.createNewFile();
        }
        try (ObjectOutputStream os = new ObjectOutputStream(
                new FileOutputStream(persistenceFile))) {
            os.writeObject(holder);
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw e;
        }*/
        objectPersistenceHandler.writeObject(holder);
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

