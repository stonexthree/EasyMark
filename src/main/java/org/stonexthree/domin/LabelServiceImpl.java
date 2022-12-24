package org.stonexthree.domin;

import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.DocDTO;
import org.stonexthree.persistence.LabelDataPersistence;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author stonexthree
 */
@Component
public class LabelServiceImpl implements Serializable, LabelService {
    /*private Set<LabelDTO> lables;
    private Map<Integer, Map.Entry<String,String>> labelDocMap;*/
    /**
     * k: 标签名
     * v: 文档id的集合
     */
    private Map<String,Set<String>> labelDocMap;
    private DocService docService;
    private LabelDataPersistence persistence;

    public LabelServiceImpl(DocService docService, LabelDataPersistence persistence) throws IOException {
        this.docService = docService;
        this.persistence = persistence;
        this.labelDocMap = persistence.loadLabelMap();
    }

    @Override
    public synchronized boolean changeLabelName(String oldName, String newName) throws IOException{
        if(!labelDocMap.containsKey(oldName)||labelDocMap.containsKey(newName)){
            return false;
        }
        labelDocMap.put(newName,labelDocMap.get(oldName));
        labelDocMap.remove(oldName);
        try{
            persistence.savLabelMap(labelDocMap);
        }catch (IOException e){
            labelDocMap.put(oldName,labelDocMap.get(newName));
            labelDocMap.remove(newName);
            throw e;
        }
        return true;
    }

    @Override
    public synchronized boolean bindLabelsToDoc(Collection<String> labels, String docId) throws IOException{
        if(!docService.docExist(docId)){
            return false;
        }
        Set<String> changedKeyList = new HashSet<>();
        Set<String> addedKeyList = new HashSet<>();

        labels.stream().forEach(labelName ->{
            if(labelDocMap.containsKey(labelName)){
                labelDocMap.get(labelName).add(docId);
                changedKeyList.add(labelName);
            }else {
                Set<String> temp = new HashSet<>();
                temp.add(docId);
                labelDocMap.put(labelName,temp);
                addedKeyList.add(labelName);
            }
        });
        try {
            persistence.savLabelMap(labelDocMap);
        }catch (IOException e){
            changedKeyList.stream().forEach(changedKey -> labelDocMap.get(changedKey).remove(docId));
            addedKeyList.stream().forEach(addedKey -> labelDocMap.remove(addedKey));
            throw e;
        }

        return true;
    }

    @Override
    public synchronized boolean removeBind(String labelName, String docId) throws IOException{
        if(!labelDocMap.containsKey(labelName)){
            return false;
        }
        if(!labelDocMap.get(labelName).contains(docId)){
            return true;
        }
        labelDocMap.get(labelName).remove(docId);
        try {
            persistence.savLabelMap(labelDocMap);
        }catch (IOException e){
            labelDocMap.get(labelName).add(docId);
            throw e;
        }
        return true;
    }

    @Override
    public synchronized boolean rebindLabelsToDoc(Set<String> labels, String docId) throws IOException{
        if(!docService.docExist(docId)){
            return false;
        }
        Set<String> addedKeySet = new HashSet<>();
        Set<String> removedKeySet = new HashSet<>();
        Set<String> newKeySet = new HashSet<>();

        for(String labelName : labelDocMap.keySet()){
            if(labels.contains(labelName)){
                labelDocMap.get(labelName).add(docId);
                labels.remove(labelName);
                addedKeySet.add(labelName);
                continue;
            }
            labelDocMap.get(labelName).remove(docId);
            removedKeySet.add(labelName);
        }
        for(String labelName:labels){
            Set<String> temp = new HashSet<>();
            temp.add(docId);
            labelDocMap.put(labelName,temp);
            newKeySet.add(labelName);
        }

        try{
            persistence.savLabelMap(labelDocMap);
        }catch (IOException e){
            addedKeySet.stream().forEach(key->labelDocMap.get(key).remove(docId));
            removedKeySet.stream().forEach(key->labelDocMap.get(key).add(docId));
            newKeySet.stream().forEach(key->labelDocMap.remove(key));
            throw e;
        }
        return true;
    }


    @Override
    public synchronized boolean removeDocBinds(String docId) throws IOException{
        if(!docService.docExist(docId)){
            return false;
        }
        Set<String> removedKeySet = new HashSet<>();
        for(Map.Entry<String,Set<String>> entry:labelDocMap.entrySet()){
            if(entry.getValue().remove(docId)){
                removedKeySet.add(entry.getKey());
            }
        }
        try{
            persistence.savLabelMap(labelDocMap);
        }catch (IOException e){
            removedKeySet.stream().forEach(key -> labelDocMap.get(key).add(docId));
            throw e;
        }
        return true;
    }

    @Override
    public synchronized boolean deleteLabel(String labelName) throws IOException{
        Set<String> targetSet = labelDocMap.get(labelName);
        if(targetSet == null){
            return false;
        }
        labelDocMap.remove(labelName);
        try {
            persistence.savLabelMap(labelDocMap);
        }catch (IOException e){
            labelDocMap.put(labelName,targetSet);
            throw e;
        }
        return true;
    }

    @Override
    public Set<String> getDocIdByLabel(String labelName) {
        Set<String> result = new HashSet<>();
        result.addAll(labelDocMap.getOrDefault(labelName,new HashSet<>(0)));
        return result;
    }

    @Override
    public Set<String> getAllLabel() {
        Set<String> result = new HashSet<>();
        result.addAll(labelDocMap.keySet());
        return result;
    }

    @Override
    public Set<String> getDocLabel(String docId) {
        Set<String> result = new HashSet<>();
        for(Map.Entry<String,Set<String>> entry:labelDocMap.entrySet()){
            if(entry.getValue().contains(docId)){
                result.add(entry.getKey());
            }
        }
        return result;
    }


    @Override
    public Set<String> searchLabel(String keyword) {
        return labelDocMap.keySet().stream().filter(label -> label.contains(keyword)).collect(Collectors.toSet());
    }

    @Override
    public Set<String> searchDocByLabelKeyword(String keyword) {
        Set<String> result = new HashSet<>();
        labelDocMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().contains(keyword))
                .forEach(entry -> result.addAll(entry.getValue()));
        return result;
    }
}
