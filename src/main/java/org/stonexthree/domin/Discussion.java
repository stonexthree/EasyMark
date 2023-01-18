package org.stonexthree.domin;


import org.stonexthree.domin.model.DiscussionDTO;

import java.io.Serializable;
import java.time.Instant;

/**
 * 讨论内容的对象，detail记录具体内容，author记录这条讨论的作者，quote记录这条讨论引用的上面哪条讨论的索引，没有引用的话取-1；
 */
public class Discussion implements Serializable {
    /**
     * 回复的文档ID
     */
    private String docId;
    /**
     * 回复的主题ID
     */
    private String topicId;
    /**
     * 回复的索引
     */
    private Integer index;
    /**
     * 回复的具体内容
     */
    private String detail;
    /**
     * 进行回复的账户/本条回复的作者
     */
    private String author;
    /**
     * 引用的其他回复的索引
     */
    private Integer quote;
    /**
     * 回复的创建时间
     */
    private Long createTimestamp;


    private Discussion(String docId, String topicId, int index, String detail, String author, Integer quote) {
        this.docId = docId;
        this.topicId = topicId;
        this.index = index;
        this.detail = detail;
        this.author = author;
        this.quote = quote;
        this.createTimestamp = Instant.now().toEpochMilli();
    }

    /**
     * 通过主题和数据对象构造一个回复对象
     * @param topic
     * @param dto
     * @return
     */
    public static Discussion buildDiscussion(DiscussionTopic topic,DiscussionDTO dto){
        return new Discussion(dto.getDocId(), dto.getTopicId(), topic.getDiscussionCount(), dto.getDetail(), dto.getAuthor(), dto.getQuote());
    }

    public String getDocId() {
        return docId;
    }
    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getQuote() {
        return quote;
    }

    public void setQuote(Integer quote) {
        this.quote = quote;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Long getCreateTimestamp(){
        return this.createTimestamp;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
