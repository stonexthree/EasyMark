package org.stonexthree.domin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.DocDTO;
import org.stonexthree.domin.model.DocHolder;
import org.stonexthree.persistence.DocDataPersistence;
import org.stonexthree.security.util.CryptoUtil;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
public class DocServiceImpl implements DocService{
    private DocDataPersistence docDataPersistence;
    private DocHolder docHolder;
    private TextEncryptor textEncryptor;

    public DocServiceImpl(DocDataPersistence docDataPersistence ,CryptoUtil cryptoUtil) {
        this.docDataPersistence = docDataPersistence;
        this.docHolder = docDataPersistence.loadHolder();
        textEncryptor = cryptoUtil.getTextEncryptor();
    }

    @Override
    public synchronized String createDoc(String username, String docName, String content) throws IOException{
        if(docHolder.nameUsed(username,docName)){
            throw new IllegalArgumentException("文档名称已使用");
        }
        String fileName = docDataPersistence.writeDocToFile(textEncryptor.encrypt(content));
        DocDTO doc = new DocDTO(fileName,docName,fileName,username, Instant.now().toEpochMilli());
        docHolder.addDoc(username, doc);
        try {
            docDataPersistence.writeHolder(docHolder);
        }catch (IOException e){
            log.warn(e.getMessage());
            docHolder.DeleteDoc(username,doc);
            throw e;
        }
        return doc.getDocId();
    }

    @Override
    public synchronized boolean deleteDoc(String username, String docId) throws IOException{
        DocDTO tempDoc = docHolder.getDocById(docId);
        if(tempDoc == null || !username.equals(tempDoc.getDocAuthor())){
            return false;
        }
        docHolder.DeleteDoc(username,tempDoc);
        try{
            docDataPersistence.writeHolder(docHolder);
        }catch (IOException e){
            docHolder.addDoc(username,tempDoc);
            throw e;
        }
        docDataPersistence.deleteDoc(tempDoc.getDocLocation());
        return true;
    }

    @Override
    public synchronized boolean updateDoc(String username, String docId, String content) throws IOException{
        DocDTO target = docHolder.getDocById(docId);
        if(target == null || !username.equals(target.getDocAuthor())){
            return false;
        }
        String fileName = docHolder.getDocById(docId).getDocLocation();
        //String oldFileContent = docDataPersistence.ReadDoc(fileName);
        try{
            docDataPersistence.writeDocToFile(textEncryptor.encrypt(content),fileName);
            target.setUpdateTimestamp(Instant.now().toEpochMilli());
            docDataPersistence.writeHolder(docHolder);
        }catch (IOException e){
            log.warn(e.getMessage());
            throw e;
        }

        return true;

    }

    @Override
    public String getDocContent(String docId) throws IOException{
        DocDTO docDTO = docHolder.getDocById(docId);
        if(docDTO == null){
            return null;
        }
        String encodeContent = docDataPersistence.readDoc(docDTO.getDocLocation());
        return textEncryptor.decrypt(encodeContent);
    }

    @Override
    public boolean userHasDoc(String username,String docId) {
        DocDTO docDTO = docHolder.getDocById(docId);
        if(docDTO == null){
            return false;
        }
        return docDTO.getDocAuthor().equals(username);
    }

    @Override
    public Set<DocDTO> getAllDoc() {
        return docHolder.getAllDoc();
    }

    @Override
    public Set<DocDTO> getDocByUserName(String username) {
        return docHolder.getDocByUserName(username);
    }

    @Override
    public Set<DocDTO> searchDocByName(String keyword) {
        return docHolder.searchByName(keyword);
    }

    @Override
    public synchronized boolean changeDocName(String username, String docId, String newDocName) {
        DocDTO target = docHolder.getDocById(docId);
        if(target == null || !username.equals(target.getDocAuthor())){
            return false;
        }
        if(newDocName.equals(target.getDocName())){
            return true;
        }
        if( docHolder.nameUsed(username,newDocName)){
            return false;
        }
        target.setDocName(newDocName);
        String oldDocName = target.getDocName();
        try {
            docDataPersistence.writeHolder(docHolder);
        }catch (IOException e){
            log.warn(e.getMessage());
            docHolder.getDocById(docId).setDocName(oldDocName);
        }
        return true;
    }

    @Override
    public boolean docExist(String docId){
        return docHolder.fileExistById(docId);
    }

    @Override
    public DocDTO getDocById(String docId) {
        return docHolder.getDocById(docId);
    }

    @Override
    public Set<DocDTO> getDocsByIds(Collection docIds) {
        return docHolder.getDocsByIds(docIds);
    }
}
