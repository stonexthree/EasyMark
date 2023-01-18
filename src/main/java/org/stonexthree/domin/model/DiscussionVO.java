package org.stonexthree.domin.model;
import org.stonexthree.domin.Discussion;
import org.stonexthree.domin.DiscussionTopic;

/**
 * 文档回复的展示对象
 */
public class DiscussionVO {
    /**
     * 回复的文档ID
     */
    private String docId;
    /**
     * 回复的主题ID
     */
    private String topicId;
    /**
     * 回复的创建时间
     */
    private Long createTimestamp;
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
     * 回复所属的主题是否关闭
     */
    private Boolean closed;
    /**
     * 回复所属的主题的关闭类型（来源）
     */
    private String closedType;

    public DiscussionVO(DiscussionTopic topic, Discussion dto){
        this.docId = dto.getDocId();
        this.topicId = dto.getTopicId();
        this.createTimestamp = dto.getCreateTimestamp();
        this.index = dto.getIndex();
        this.detail = dto.getDetail();
        this.author = dto.getAuthor();
        this.quote = dto.getQuote();
        this.closed = topic.isClosed();
        this.closedType = topic.getClosedType().name();
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


    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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

    public Boolean isClosed() {
        return closed;
    }

    public String getClosedType() {
        return closedType;
    }
}
