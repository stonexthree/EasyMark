package org.stonexthree.domin.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author stonexthree
 */
public class DocHolder implements Serializable {
    /**
     * key:账号名称，value:账号创建的文档
     */
    private Map<String, Set<DocDTO>> docDTOMap;

    public DocHolder() {
        this.docDTOMap = new HashMap<>();
    }

    public Set<DocDTO> getAllDoc() {
        Set<DocDTO> result = new HashSet<>();
        for (Map.Entry<String, Set<DocDTO>> entry : docDTOMap.entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }

    public Set<DocDTO> getDocByUserName(String username) {
        Set<DocDTO> result = new HashSet<>();
        if (docDTOMap.containsKey(username)) {
            result.addAll(docDTOMap.get(username));
        }
        return result;
    }

    public Set<DocDTO> searchByName(String keyword) {
        Set<DocDTO> result = new HashSet<>();
        for (Set<DocDTO> docSet : docDTOMap.values()) {
            docSet
                    .stream()
                    .filter(doc -> doc.getDocName().contains(keyword))
                    .forEach(result::add);
        }
        return result;
    }

    public boolean nameUsed(String username, String docName) {
        if (docDTOMap.containsKey(username)) {
            for (DocDTO doc : docDTOMap.get(username)) {
                if (doc.getDocName().equals(docName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean fileExistById(String docId) {
        return getDocById(docId) != null;
    }

    public synchronized void addDoc(String userName, DocDTO doc) {
        Set<DocDTO> userDocSet;
        if (!docDTOMap.containsKey(userName)) {
            userDocSet = new HashSet<>();
        } else {
            userDocSet = docDTOMap.get(userName);
        }
        userDocSet.add(doc);
        docDTOMap.put(userName, userDocSet);
    }

    public synchronized void DeleteDoc(String userName, DocDTO target) {
        Set<DocDTO> userDocSet = docDTOMap.get(userName);
        try {
            userDocSet.remove(target);
        } catch (NoSuchElementException e) {
            return;
        }
    }


    /**
     * @param docId
     * @return 返回文档对应的DTO，若DocHolder没有持有 对应的docId,返回null
     */
    public DocDTO getDocById(String docId) {
        for (Set<DocDTO> docDTOSet : docDTOMap.values()) {
            for (DocDTO docDTO : docDTOSet) {
                if (docDTO.getDocId().equals(docId)) {
                    return docDTO;
                }
            }
        }
        return null;
    }

    public Set<DocDTO> getDocsByIds(Collection docIds){
        Set<DocDTO> result = new HashSet<>();
        for(Set<DocDTO> docDTOSet:docDTOMap.values()){
            result.addAll(
                    docDTOSet.stream()
                            .filter(dto -> docIds.contains(dto.getDocId()))
                            .toList()
            );
        }
        return result;
    }
}
