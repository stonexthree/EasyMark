package org.stonexthree.domin;

import org.stonexthree.domin.model.Document;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 文档服务
 * 作为草稿的文档和普通文档有一致的行为，仅在查询时做区分。
 */
public interface DocService {
    /**
     * 列出所有文档，不包括草稿
     * @return
     */
    Set<Document> listAllDoc();

    /**
     * 列出指定用户下的所有文档，不包括草稿
     * @param username
     * @return
     */
    Set<Document> listDocByUserName(String username);

    /**
     * 按名称搜索文档，不包括草稿
     * @param keyword
     * @return
     */
    Set<Document> searchDocByName(String keyword);

    /**
     * 创建一个文档。不是草稿。
     * @param username
     * @param name
     * @param content
     * @return
     * @throws IOException
     */
    String createDoc(String username,String name,String content,boolean isDraft) throws IOException;


    /**
     * 删除用户的一个文档
     * @param username 用户名
     * @param docId 文档id
     * @return
     * @throws IOException
     */
    boolean deleteDoc(String username,String docId) throws IOException;

    /**
     * 更新文档内容
     * @param username
     * @param docId
     * @param content
     * @return
     * @throws IOException
     */
    boolean updateDoc(String username,String docId,String content) throws IOException;

    /**
     * 更改文档名称
     * @param username
     * @param docId
     * @param newDocName
     * @return
     */
    boolean changeDocName(String username,String docId,String newDocName);

    /**
     * 获取文档内容
     * @param docId
     * @return
     * @throws IOException
     */
    String getDocContent(String docId) throws IOException;

    /**
     * 判断用户是否拥有某个文档
     * @param username
     * @param docId
     * @return
     */
    boolean userHasDoc(String username,String docId);

    /**
     * 判断文档是否操作
     * @param docId
     * @return
     */
    boolean docExist(String docId);

    /**
     * 提供文档id获取文档对象
     * @param docId
     * @return
     */
    Document getDocById(String docId);

    /**
     * 根据给定的id集合获取对应的文档
     * @param docIds
     * @return
     */
    Set<Document> listDocsByIds(Collection docIds);

    /**
     * 提交一个草稿
     * @param username 作者名称
     * @param docId 文档id
     * @return
     */
    void submitDraft(String username,String docId) throws IOException;

    /**
     * 获取用户的全部草稿
     * @param username
     * @return
     */
    Set<Document> listDrafts(String username);

    void collectDoc(String username,String docId) throws IOException;
    void removeCollect(String username,String docId) throws IOException;
    Set<Document> listCollectedDocument(String username);
    boolean isDocCollected(String username,String docId);

    /**
     * 获取各用户创建的文档数
     * @return
     */
    Map<String,Integer> listUserCreateDocCount();

    /**
     * 返回文档id和文档名称的映射
     * @return
     */
    Map<String,String> getIdNameMap();
}
