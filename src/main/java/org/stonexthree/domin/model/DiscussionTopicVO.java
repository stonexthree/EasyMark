package org.stonexthree.domin.model;

import org.stonexthree.domin.DiscussionTopic;
import org.stonexthree.domin.DiscussionTopic.ClosedTypeEnum;

import java.time.Instant;
import java.util.ArrayList;

/**
 * 回复主题的展示对象
 */
public class DiscussionTopicVO {
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
     * 主题关闭类型（关闭来源）
     */
    private String closedType;
    /**
     * 创建时间
     */
    private Long createTimeStamp;
    /**
     * 最近的一次回复时间
     */
    private Long lastReplyTimeStamp;
    /**
     * 主题下的回复条数
     */
    private Integer discussionCount;

/*    public DiscussionTopicVO(String docId, String topicId, String detail, String author, Boolean closed, ClosedTypeEnum closedType, Long createTimeStamp, Long lastReplyTimeStamp) {
        this.docId = docId;
        this.topicId = topicId;
        this.detail = detail;
        this.author = author;
        this.closed = closed;
        this.closedType = closedType.name();
        this.createTimeStamp = createTimeStamp;
        this.lastReplyTimeStamp = lastReplyTimeStamp;
    }*/

    public DiscussionTopicVO(DiscussionTopic topic) {
        this.docId = topic.getDocId();
        this.topicId = topic.getTopicId();
        this.detail = topic.getDetail();
        this.author = topic.getAuthor();
        this.closed = topic.isClosed();
        this.closedType = topic.getClosedType().name();
        this.createTimeStamp = topic.getCreateTimeStamp();
        this.lastReplyTimeStamp = topic.getLastReplyTimeStamp();
        this.discussionCount = topic.getDiscussionCount();
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

    public Boolean getClosed() {
        return closed;
    }

    public String getClosedType() {
        return closedType;
    }

    public Long getCreateTimeStamp() {
        return createTimeStamp;
    }

    public Long getLastReplyTimeStamp() {
        return lastReplyTimeStamp;
    }

    public String getDocId() {
        return docId;
    }

    public Integer getDiscussionCount() {
        return discussionCount;
    }
}
