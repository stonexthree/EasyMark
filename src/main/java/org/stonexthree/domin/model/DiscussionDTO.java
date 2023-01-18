package org.stonexthree.domin.model;

/**
 * 文档回复的数据对象
 */
public class DiscussionDTO {
    /**
     * 回复的文档ID
     */
    private String docId;
    /**
     * 回复的主题ID
     */
    private String topicId;
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

    public DiscussionDTO(String docId, String topicId, String detail, String author, Integer quote) {
        this.docId = docId;
        this.topicId = topicId;
        this.detail = detail;
        this.author = author;
        this.quote = quote;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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
}
