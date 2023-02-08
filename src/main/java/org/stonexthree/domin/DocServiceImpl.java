package org.stonexthree.domin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.Document;
import org.stonexthree.persistence.DocDataPersistence;
import org.stonexthree.security.util.CryptoUtil;

import javax.print.Doc;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
public class DocServiceImpl implements DocService {
    private DocDataPersistence docDataPersistence;
    /**
     * key:账号名称，value:账号创建的文档
     */
    private Map<String, Set<Document>> usernameDocMap;
    /**
     * key:账户名称，value:该账户收藏的文档
     */
    private Map<String,Set<String>> userCollectMap;

    private TextEncryptor textEncryptor;

    public DocServiceImpl(DocDataPersistence docDataPersistence, CryptoUtil cryptoUtil) throws IOException {
        this.docDataPersistence = docDataPersistence;
        this.usernameDocMap = docDataPersistence.loadMap();
        this.userCollectMap = docDataPersistence.loadCollectMap();
        textEncryptor = cryptoUtil.getTextEncryptor();
    }

    private boolean nameUsed(String username, String docName) {
        if (usernameDocMap.containsKey(username)) {
            for (Document doc : usernameDocMap.get(username)) {
                if (doc.getDocName().equals(docName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized void addDocToMap(String userName, Document doc) {
        Set<Document> userDocSet;
        if (!usernameDocMap.containsKey(userName)) {
            userDocSet = new HashSet<>();
        } else {
            userDocSet = usernameDocMap.get(userName);
        }
        userDocSet.add(doc);
        usernameDocMap.put(userName, userDocSet);
    }

    private synchronized void deleteDocFromMap(String userName, Document target) {
        Set<Document> userDocSet = usernameDocMap.get(userName);
        try {
            userDocSet.remove(target);
        } catch (NoSuchElementException e) {
            return;
        }
    }

    @Override
    public synchronized String createDoc(String username, String docName, String content, boolean isDraft) throws IOException {
        if (nameUsed(username, docName)) {
            throw new IllegalArgumentException("文档名称已使用");
        }
        String fileName = docDataPersistence.writeDocToFile(textEncryptor.encrypt(content));
        Document doc = isDraft ?
                Document.createDraft(fileName, docName, fileName, username, Instant.now().toEpochMilli()) :
                Document.createDocument(fileName, docName, fileName, username, Instant.now().toEpochMilli());
        addDocToMap(username, doc);
        try {
            docDataPersistence.writeMap(usernameDocMap);
        } catch (IOException e) {
            log.warn(e.getMessage());
            deleteDocFromMap(username, doc);
            throw e;
        }
        return doc.getDocId();
    }

    @Override
    public synchronized boolean deleteDoc(String username, String docId) throws IOException {
        Document tempDoc = getDocById(docId);
        if (tempDoc == null || !username.equals(tempDoc.getDocAuthor())) {
            return false;
        }
        deleteDocFromMap(username, tempDoc);
        try {
            docDataPersistence.writeMap(usernameDocMap);
        } catch (IOException e) {
            addDocToMap(username, tempDoc);
            throw e;
        }
        docDataPersistence.deleteDoc(tempDoc.getDocLocation());
        return true;
    }

    @Override
    public synchronized boolean updateDoc(String username, String docId, String content) throws IOException {
        Document target = getDocById(docId);
        if (target == null || !username.equals(target.getDocAuthor())) {
            return false;
        }
        String fileName = target.getDocLocation();
        try {
            docDataPersistence.writeDocToFile(textEncryptor.encrypt(content), fileName);
            target.setUpdateTimestamp(Instant.now().toEpochMilli());
            docDataPersistence.writeMap(usernameDocMap);
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw e;
        }
        return true;

    }

    @Override
    public String getDocContent(String docId) throws IOException {
        Document document = getDocById(docId);
        if (document == null) {
            return null;
        }
        String encodeContent = docDataPersistence.readDoc(document.getDocLocation());
        return textEncryptor.decrypt(encodeContent);
    }

    @Override
    public boolean userHasDoc(String username, String docId) {
        Document document = getDocById(docId);
        if (document == null) {
            return false;
        }
        return document.getDocAuthor().equals(username);
    }

    @Override
    public Set<Document> listAllDoc() {
        Set<Document> result = new HashSet<>();
        for (Map.Entry<String, Set<Document>> entry : usernameDocMap.entrySet()) {
            //result.addAll(entry.getValue());
            result.addAll(entry.getValue().stream().filter(document -> !document.isDraft()).toList());
        }
        return result;
    }

    @Override
    public Set<Document> listDocByUserName(String username) {
        Set<Document> result = new HashSet<>();
        if (usernameDocMap.containsKey(username)) {
            result.addAll(usernameDocMap.get(username).stream().filter(document -> !document.isDraft()).toList());
        }
        return result;
    }

    @Override
    public Set<Document> searchDocByName(String keyword) {
        Set<Document> result = new HashSet<>();
        for (Set<Document> docSet : usernameDocMap.values()) {
            docSet
                    .stream()
                    .filter(doc -> (!doc.isDraft()) && doc.getDocName().contains(keyword))
                    .forEach(result::add);
        }
        return result;
    }

    @Override
    public synchronized boolean changeDocName(String username, String docId, String newDocName) {
        Document target = getDocById(docId);
        if (target == null || !username.equals(target.getDocAuthor())) {
            return false;
        }
        if (newDocName.equals(target.getDocName())) {
            return true;
        }
        if (nameUsed(username, newDocName)) {
            return false;
        }
        target.setDocName(newDocName);
        String oldDocName = target.getDocName();
        try {
            docDataPersistence.writeMap(usernameDocMap);
        } catch (IOException e) {
            log.warn(e.getMessage());
            getDocById(docId).setDocName(oldDocName);
        }
        return true;
    }

    @Override
    public boolean docExist(String docId) {
        return getDocById(docId) != null;
    }

    @Override
    public Document getDocById(String docId) {
        for (Set<Document> documentSet : usernameDocMap.values()) {
            for (Document document : documentSet) {
                if (document.getDocId().equals(docId)) {
                    return document;
                }
            }
        }
        return null;
    }

    @Override
    public Set<Document> listDocsByIds(Collection docIds) {
        Set<Document> result = new HashSet<>();
        for (Set<Document> documentSet : usernameDocMap.values()) {
            result.addAll(
                    documentSet.stream()
                            .filter(dto -> docIds.contains(dto.getDocId()))
                            .toList()
            );
        }
        return result;
    }

    @Override
    public void submitDraft(String username, String docId) throws IOException {
        Document target;
        try {
            target = usernameDocMap.get(username)
                    .stream()
                    .filter(document -> docId.equals(document.getDocId()))
                    .findFirst().get();
        }catch (RuntimeException e){
            if (e instanceof NoSuchElementException || e instanceof NullPointerException){
                throw new IllegalArgumentException("文档或用户不存在");
            }
            throw e;
        }
        target.setDraft(false);
        docDataPersistence.writeMap(usernameDocMap);
    }

    @Override
    public Set<Document> listDrafts(String username) {
        Set<Document> drafts = new HashSet<>();
        Set<Document> targets = usernameDocMap.get(username);
        if(targets==null||targets.size()==0){
            return drafts;
        }
        targets.forEach(document->{
            if(document.isDraft()){
                drafts.add(document);
            }
        });
        return drafts;
    }

    @Override
    public void collectDoc(String username, String docId) throws IOException {
        if(!docExist(docId)){
            throw new IllegalArgumentException("文档不存在");
        }
        Set<String> targetSet = this.userCollectMap.containsKey(username)?this.userCollectMap.get(username):new HashSet<>();
        targetSet.add(docId);
        this.userCollectMap.put(username,targetSet);
        docDataPersistence.writeCollectMap(this.userCollectMap);
    }

    @Override
    public void removeCollect(String username, String docId) throws IOException {
        if(!docExist(docId)){
            throw new IllegalArgumentException("文档不存在");
        }
        Set<String> targetSet = this.userCollectMap.containsKey(username)?this.userCollectMap.get(username):new HashSet<>();
        targetSet.remove(docId);
        this.userCollectMap.put(username,targetSet);
        docDataPersistence.writeCollectMap(this.userCollectMap);
    }

    @Override
    public boolean isDocCollected(String username, String docId) {
        if(this.userCollectMap.containsKey(username)&&this.userCollectMap.get(username).contains(docId)){
            return true;
        }
        return false;
    }

    @Override
    public Set<Document> listCollectedDocument(String username) {
        if(!this.userCollectMap.containsKey(username)){
            return new HashSet<>();
        }
        return listDocsByIds(this.userCollectMap.get(username));
    }

    @Override
    public Map<String, Integer> listUserCreateDocCount() {
        Map<String,Integer> result = new HashMap<>();
        usernameDocMap.forEach((key,value)->result.put(key, value.size()));
        return result;
    }

    @Override
    public Map<String, String> getIdNameMap() {
        Map<String,String> result = new HashMap<>();
        usernameDocMap.forEach((key,value)->{
            value.forEach(document -> {
                result.put(document.getDocId(),document.getDocName());
            });
        });
        return result;
    }
}
