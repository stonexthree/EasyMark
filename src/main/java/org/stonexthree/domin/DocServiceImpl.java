package org.stonexthree.domin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.Document;
import org.stonexthree.persistence.DocDataPersistence;
import org.stonexthree.security.util.CryptoUtil;

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
    private Map<String, Set<String>> userCollectMap;

    private TextEncryptor textEncryptor;

    /**
     * 导出任务表。key: 任务id; value: 导出文件的名称,当导出任务未完成时，为null。
     */
    private HashMap<Integer, ExportTask> exportTaskMap;
    private int taskCount;

    public class ExportTask {
        private ExportType type;
        private String username;

        private boolean errorHappened;
        private String result;

        private Long createTimestamp;

        private ExportTask(ExportType type, String username) {
            this.type = type;
            this.username = username;
            this.errorHappened = false;
            this.result = null;
            this.createTimestamp = Instant.now().toEpochMilli();
        }
    }


    public DocServiceImpl(DocDataPersistence docDataPersistence, CryptoUtil cryptoUtil) throws IOException {
        this.docDataPersistence = docDataPersistence;
        this.usernameDocMap = docDataPersistence.loadMap();
        this.userCollectMap = docDataPersistence.loadCollectMap();
        this.textEncryptor = cryptoUtil.getTextEncryptor();
        this.exportTaskMap = new HashMap<>();
        this.taskCount = 0;
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
    public Set<String> listAllDocId(){
        Set<String> result = new HashSet<>();
        usernameDocMap.forEach((key,value)->
                value.forEach(document ->
                        result.add(document.getDocId())
                )
        );
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
        } catch (RuntimeException e) {
            if (e instanceof NoSuchElementException || e instanceof NullPointerException) {
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
        if (targets == null || targets.size() == 0) {
            return drafts;
        }
        targets.forEach(document -> {
            if (document.isDraft()) {
                drafts.add(document);
            }
        });
        return drafts;
    }

    @Override
    public void collectDoc(String username, String docId) throws IOException {
        if (!docExist(docId)) {
            throw new IllegalArgumentException("文档不存在");
        }
        Set<String> targetSet = this.userCollectMap.containsKey(username) ? this.userCollectMap.get(username) : new HashSet<>();
        targetSet.add(docId);
        this.userCollectMap.put(username, targetSet);
        docDataPersistence.writeCollectMap(this.userCollectMap);
    }

    @Override
    public void removeCollect(String username, String docId) throws IOException {
        if (!docExist(docId)) {
            throw new IllegalArgumentException("文档不存在");
        }
        Set<String> targetSet = this.userCollectMap.containsKey(username) ? this.userCollectMap.get(username) : new HashSet<>();
        targetSet.remove(docId);
        this.userCollectMap.put(username, targetSet);
        docDataPersistence.writeCollectMap(this.userCollectMap);
    }

    @Override
    public boolean isDocCollected(String username, String docId) {
        if (this.userCollectMap.containsKey(username) && this.userCollectMap.get(username).contains(docId)) {
            return true;
        }
        return false;
    }

    @Override
    public Set<Document> listCollectedDocument(String username) {
        if (!this.userCollectMap.containsKey(username)) {
            return new HashSet<>();
        }
        return listDocsByIds(this.userCollectMap.get(username));
    }

    @Override
    public Map<String, Integer> listUserCreateDocCount() {
        Map<String, Integer> result = new HashMap<>();
        usernameDocMap.forEach((key, value) -> result.put(key, value.size()));
        return result;
    }

    @Override
    public Map<String, String> getIdNameMap() {
        Map<String, String> result = new HashMap<>();
        usernameDocMap.forEach((key, value) -> {
            value.forEach(document -> {
                result.put(document.getDocId(), document.getDocName());
            });
        });
        return result;
    }


    @Override
    public synchronized Integer createExportTask(ExportType type, String username) {
        Integer taskId = ++taskCount;
        ExportTask task = new ExportTask(type, username);
        exportTaskMap.put(taskId, task);
        return taskId;
    }

    @Override
    @Async
    public void runExportTask(Integer id) {
        ExportTask task = exportTaskMap.get(id);
        if (task == null) {
            return;
        }
        Map<String, String> docs = new HashMap<>();
        Set<Document> docSet;
        switch (task.type) {
            case ALL -> docSet = listAllDoc();
            case USER -> docSet = listDocByUserName(task.username);
            default -> docSet = new HashSet<>();
        }
        String filename = null;
        try {
            for (Document document : docSet) {
                docs.put(document.getDocName() + ".md", getDocContent(document.getDocId()));
            }
            filename = docDataPersistence.exportDocs(id, docs);
        } catch (IOException e) {
            e.printStackTrace();
            task.errorHappened = true;
        }
        task.result = filename;
    }

    @Override
    public boolean isTaskFinish(Integer taskId) {
        ExportTask task = exportTaskMap.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在或过期");
        }
        if (task.errorHappened) {
            throw new RuntimeException("服务内部错误，请联系管理员");
        }
        return task.result != null;
    }

    @Override
    public String getTaskResult(Integer taskId) {
        ExportTask task = exportTaskMap.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在或过期");
        }
        return task.result;
    }

    /**
     * 清理已完成的任务，同时删除相关的文件
     */
    @Override
    @Scheduled(cron = "0 0/30 * * * ? ")
    public void cleanTask() {
        Map<Integer, String> cleanMap = new HashMap<>();
        long currentTimestamp = Instant.now().toEpochMilli();
        exportTaskMap.forEach((key, value) -> {
            if (value.result != null && (currentTimestamp - value.createTimestamp > 3600000)) {
                cleanMap.put(key, value.result);
            }
        });
        for (Map.Entry<Integer, String> entry : cleanMap.entrySet()) {
            exportTaskMap.remove(entry.getKey());
            docDataPersistence.deleteExportFile(entry.getValue());
        }
    }

    @Override
    public void docHandOver(String sourceUser, String targetUser) throws IOException{
        Set<Document> sourceDocSet = usernameDocMap.get(sourceUser);
        Set<Document> targetDocSet = usernameDocMap.get(targetUser);
        boolean docSetChanged = false;
        Set<String> sourceUserCollectSet = userCollectMap.get(sourceUser);
        if(sourceDocSet!=null && targetDocSet!=null){
            targetDocSet.addAll(sourceDocSet);
            usernameDocMap.remove(sourceUser);
            docSetChanged = true;
        }
        if(sourceDocSet != null && targetDocSet == null){
            usernameDocMap.put(targetUser,sourceDocSet);
            usernameDocMap.remove(sourceUser);
            docSetChanged = true;
        }
        userCollectMap.remove(sourceUser);
        if(docSetChanged){
            usernameDocMap.get(targetUser).forEach(document -> document.setDocAuthor(targetUser));
            docDataPersistence.writeMap(usernameDocMap);
        }
        if(sourceUserCollectSet != null){
            docDataPersistence.writeCollectMap(userCollectMap);
        }
    }

    @Override
    public void docDistribute(String receiver, List<String> docList) throws IOException {
        Set<Document> adminDocs = usernameDocMap.get("admin");
        Set<Document> receiverDocs = new HashSet<>();
        Iterator iterator = adminDocs.iterator();
        while(iterator.hasNext()){
            Document document =(Document) iterator.next();
            if(docList.contains(document.getDocId())){
                document.setDocAuthor(receiver);
                iterator.remove();
                receiverDocs.add(document);
            }
        }
        if(usernameDocMap.containsKey(receiver)){
            usernameDocMap.get(receiver).addAll(receiverDocs);
        }else {
            usernameDocMap.put(receiver,receiverDocs);
        }
        try{
            docDataPersistence.writeMap(usernameDocMap);
        }catch (IOException e){
            receiverDocs.forEach(document -> document.setDocAuthor("admin"));
            adminDocs.addAll(receiverDocs);
            usernameDocMap.get(receiver).removeAll(receiverDocs);
            throw e;
        }
    }
}