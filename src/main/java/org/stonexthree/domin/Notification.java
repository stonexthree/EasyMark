package org.stonexthree.domin;

import java.io.Serializable;
import java.util.Objects;

public class Notification implements Serializable {
    /**
     * 通知类型：
     * DOC_REPLY: 文档被回复
     * TOPIC_REPLY: 讨论主题被回复
     * DISCUSSION_REPLY: 回复被 引用/回复
     * TOPIC_CLOSED: 发布的讨论主题被关闭
     */
    public enum NotificationType implements Serializable{
        DOC_REPLY,TOPIC_REPLY,DISCUSSION_REPLY,TOPIC_CLOSED
    }

    private String id;

    /**
     * 通知类型
     */
    private NotificationType type;

    /**
     * 产生通知的实体的描述。实体和通知类型相关：
     * DOC_REPLY: 被回复的文档标题
     * TOPIC_REPLY: 被回复的主题
     * DISCUSSION_REPLY: 被回复的回复
     * TOPIC_CLOSED: 被关闭的主题
     */
    private String entityDescribe;
    /**
     * 和实体相关的键，用于检索实体相关的对象。
     * DOC_REPLY: 文档ID
     * TOPIC_REPLY: 文档ID
     * DISCUSSION_REPLY: 文档ID
     * TOPIC_CLOSED: 文档ID
     */
    private String entityKey;
    /**
     * 触发通知的因素的描述。因素和通知类型相关：
     * DOC_REPLY: 回复
     * TOPIC_REPLY: 回复
     * DISCUSSION_REPLY: 回复
     * TOPIC_CLOSED: 关闭来源
     */
    private String message;
    /**
     * 通知产生的时间戳
     */
    private Long createTimestamp;

    public Notification(String id,NotificationType type, String entityDescribe, String entityKey, String message, Long createTimestamp) {
        this.id = id;
        this.type = type;
        this.entityDescribe = entityDescribe;
        this.entityKey = entityKey;
        this.message = message;
        this.createTimestamp = createTimestamp;
    }

    public String getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public String getEntityDescribe() {
        return entityDescribe;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public String getMessage() {
        return message;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
