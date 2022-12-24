package org.stonexthree.domin;

import org.stonexthree.domin.model.DocDTO;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public interface LabelService {
    /**
     * 修改标签名称
     * @param oldName
     * @param newName
     * @return
     */
    boolean changeLabelName(String oldName,String newName) throws IOException;


    /**
     * 为文档绑定一批文档
     * @param labels
     * @param docId
     * @return
     */
    boolean bindLabelsToDoc(Collection<String> labels,String docId) throws IOException;

    /**
     * 移除一个绑定
     * @param labelName
     * @param docId
     * @return
     */
    boolean removeBind(String labelName,String docId) throws IOException;

    /**
     * 用一批新的标签重新绑定文档
     * @param labels
     * @param docId
     * @return
     */
    boolean rebindLabelsToDoc(Set<String> labels,String docId) throws IOException;

    /**
     * 移除文档所有的绑定记录，通常用于删除文档的后置处理
     * @param docId
     * @return
     */
    boolean removeDocBinds(String docId) throws IOException;

    /**
     * 删除某个标签
     * @param labelName
     * @return
     */
    boolean deleteLabel(String labelName) throws IOException;

    /**
     * 根据标签名称查找文档id
     */
    Set<String> getDocIdByLabel(String labelName);


    Set<String> getAllLabel();

    Set<String> getDocLabel(String docId);

    /**
     * 根据关键词模糊查询所有相关的标签
     * @param keyword
     * @return
     */
    Set<String> searchLabel(String keyword);

    /**
     * 根据标签的关键词，模糊查询所有对应的文档id;
     * @param keyword
     * @return
     */
    Set<String> searchDocByLabelKeyword(String keyword);
}
