package org.stonexthree.domin;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 回复的主题，持有所有相关的回复
 */
public class DiscussionTopic implements Serializable {
    /**
     * 关闭来源的枚举，当主题被关闭时，标识关闭主题的来源
     */
    public enum ClosedTypeEnum{
        /**
         * NONE:（初始值）,ADMIN（管理员）,TOPIC_AUTHOR（主题作者）,DOCUMENT_AUTHOR（文档作者）
         */
        NONE,ADMIN,TOPIC_AUTHOR,DOCUMENT_AUTHOR
    }

    /**
     * 主题所属的文档ID
     */
    private String docId;
    /**
     * 主题的ID
     */
    private String topicId;
    /**
     * 主题的具体内容
     */
    private String detail;
    /**
     * 主题作者
     */
    private String author;
    /**
     * 主题是否关闭
     */
    private Boolean closed;
    /**
     * 关闭类型：标识关闭的来源，当closed=ture时，这个属性才有效；
     * 取值：NONE（初始值）,ADMIN（管理员）,TOPIC_AUTHOR（主题创建者）,DOCUMENT_AUTHOR（文档作者）
     */
    private ClosedTypeEnum closedType;
    /**
     * 创建时间
     */
    private Long createTimeStamp;
    /**
     * 最近的一次回复时间
     */
    private Long lastReplyTimeStamp;
    /**
     * 主题下的回复
     */
    private List<Discussion> discussions;

    public DiscussionTopic(String docId, String topicId, String detail, String author) {
        this.docId = docId;
        this.topicId = topicId;
        this.detail = detail;
        this.author = author;
        this.closed = false;
        this.closedType = ClosedTypeEnum.NONE;
        this.createTimeStamp = Instant.now().toEpochMilli();
        this.lastReplyTimeStamp = Instant.now().toEpochMilli();
        this.discussions = new ArrayList<>();
    }

    public void addDiscussions(Discussion discussion) {
        this.discussions.add(discussion);
    }

    public int getDiscussionCount() {
        return this.discussions.size();
    }

    public void setLastReplyTimeStamp(Long lastReplyTimeStamp) {
        this.lastReplyTimeStamp = lastReplyTimeStamp;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public void setClosedType(ClosedTypeEnum closedType) {
        this.closedType = closedType;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getDetail() {
        return detail;
    }

    public String getAuthor() {
        return author;
    }

    public Boolean isClosed() {
        return closed;
    }

    public ClosedTypeEnum getClosedType() {
        return closedType;
    }

    public Long getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Long getLastReplyTimeStamp() {
        return lastReplyTimeStamp;
    }

    public List<Discussion> getDiscussions() {
        return discussions;
    }

    public String getDocId() {
        return docId;
    }
}
