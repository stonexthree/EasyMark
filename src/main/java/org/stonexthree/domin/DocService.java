package org.stonexthree.domin;

import org.stonexthree.domin.model.DocDTO;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface DocService {
    Set<DocDTO> getAllDoc();
    Set<DocDTO> getDocByUserName(String username);
    Set<DocDTO> searchDocByName(String keyword);
    String createDoc(String username,String name,String content) throws IOException;
    boolean deleteDoc(String username,String docName) throws IOException;
    boolean updateDoc(String username,String name,String content) throws IOException;
    boolean changeDocName(String username,String oldDocName,String newDocName);
    String getDocContent(String docId) throws IOException;
    boolean userHasDoc(String username,String docId);
    boolean docExist(String docId);

    DocDTO getDocById(String docId);

    Set<DocDTO> getDocsByIds(Collection docIds);
}
