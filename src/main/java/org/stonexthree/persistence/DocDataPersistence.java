package org.stonexthree.persistence;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * @author stonexthree
 */
@Component
@Slf4j
public class DocDataPersistence {
    private File docSaveDir;

    private File exportDir;
    private ObjectPersistenceHandler<Map<String, Set<Document>>> objectPersistenceHandler;
    private ObjectPersistenceHandler<Map<String, Set<String>>> collectMapPersistenceHandler;


    public DocDataPersistence(@Value("${app-config.storage.persistence.file.doc.holder}") String holderFile,
                              @Value("${app-config.storage.persistence.file.doc.dir}") String docDir,
                              @Value("${app-config.storage.persistence.file.doc.collect}") String collectFile,
                              @Value("${app-config.storage.persistence.file.doc.export-dir}") String export,
                              PersistenceManager manager) throws IOException {
        holderFile = holderFile == null ? "doc.data" : holderFile;
        this.objectPersistenceHandler = manager.getHandler(holderFile, HashMap::new);
        collectFile = collectFile == null ? "collect.data" : collectFile;
        this.collectMapPersistenceHandler = manager.getHandler(collectFile, HashMap::new);
        docDir = docDir == null ? "docs" : docDir;
        docSaveDir = new File(manager.getBaseDir(), docDir);
        this.exportDir = new File(manager.getBaseDir(), export);
        if (!this.docSaveDir.isDirectory()) {
            docSaveDir.mkdirs();
        }
        if (!this.exportDir.isDirectory()) {
            exportDir.mkdirs();
        }

    }

    public Map<String, Set<Document>> loadMap() throws IOException {
        return objectPersistenceHandler.readObject();
    }

    public void writeMap(Map<String, Set<Document>> map) throws IOException {
        objectPersistenceHandler.writeObject(map);
    }

    public Map<String, Set<String>> loadCollectMap() throws IOException {
        return collectMapPersistenceHandler.readObject();
    }

    public void writeCollectMap(Map<String, Set<String>> map) throws IOException {
        collectMapPersistenceHandler.writeObject(map);
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

    /**
     * 导出多个文档到zip文件
     * @param taskId 任务id，会影响导出的zip文件名称
     * @param docs 要导出的文档，key: 文档的名称，对应zip中的文件名称；value: 文档内容
     * @return
     * @throws IOException
     */
    public String exportDocs(Integer taskId,Map<String,String> docs) throws IOException{
        String zipFileName =  Instant.now().toEpochMilli() +"_"+ taskId.toString()+".zip";
        ZipFile zipFile = new ZipFile(new File(this.exportDir,zipFileName));
        ZipParameters zipParameters = new ZipParameters();
        for(Map.Entry<String,String> entry: docs.entrySet()){
            InputStream inputStream = new ByteArrayInputStream(entry.getValue().getBytes(StandardCharsets.UTF_8));
            zipParameters.setFileNameInZip(entry.getKey());
            zipFile.addStream(inputStream,zipParameters);
            inputStream.close();
        }
        zipFile.close();
        return zipFileName;
    }

    public void deleteExportFile(String filename){
        File target = new File(this.exportDir,filename);
        target.delete();
    }
    public static void main(String[] args) {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setFileNameInZip("test.md");
        String content = "# Markdown\n## Hello Zip4j";
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
             ZipFile zipfile = new ZipFile("/Users/stonexthree/Documents/practise/temp/myMarkDoc/exports/test.zip");) {
            zipfile.addStream(inputStream, zipParameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

