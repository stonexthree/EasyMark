package org.stonexthree.domin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.stonexthree.domin.model.DiscussionDTO;
import org.stonexthree.domin.model.DiscussionTopicVO;
import org.stonexthree.domin.model.DiscussionVO;
import org.stonexthree.persistence.ObjectPersistenceHandler;
import org.stonexthree.persistence.PersistenceManager;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * 回复服务的实现。所有的主题都放在一个ArrayList中，主题对象持有相关的所有回复
 */
@Component
public class DiscussionServiceImpl implements DiscussionService {
    //private DiscussionsPersistence discussionsPersistence;
    private ObjectPersistenceHandler<List<DiscussionTopic>> discussionsPersistenceHandler;
    private List<DiscussionTopic> topicList;
    //private DocService docService;

    public DiscussionServiceImpl(@Value("${app-config.storage.persistence.file.discussion}") String fileName,
                                 PersistenceManager persistenceManager) throws IOException {
        //this.discussionsPersistence = discussionsPersistence;
        fileName = fileName == null ? "discussions.data" : fileName;
        this.discussionsPersistenceHandler = persistenceManager.getHandler(fileName, ArrayList::new);
        //this.topicList = discussionsPersistence.loadDiscussions();
        this.topicList = discussionsPersistenceHandler.readObject();
    }


    @Override
    @UseNotification(type = {Notification.NotificationType.DOC_REPLY})
    public DiscussionTopic createTopic(String docId, String detail, String author) throws IOException {
        String id = UUID.randomUUID().toString();
        DiscussionTopic topic = new DiscussionTopic(docId, id, detail, author);
        topicList.add(topic);
        //discussionsPersistence.saveDiscussions(topicList);
        discussionsPersistenceHandler.writeObject(topicList);
        return topic;
    }

    @Override
    @UseNotification(type = {Notification.NotificationType.TOPIC_CLOSED})
    public DiscussionTopic closeTopic(String user, String docAuthor,String docId, String topicId, boolean isAdmin) throws IOException {
        Optional<DiscussionTopic> targetTopic = topicList.stream().filter(t -> topicId.equals(t.getTopicId())).findFirst();
        Assert.isTrue(targetTopic.isPresent(), "关闭的主题不存在");
        //设置关闭类型（来源），前面的会被后面的覆盖
        DiscussionTopic.ClosedTypeEnum closedType = DiscussionTopic.ClosedTypeEnum.NONE;
        if (isAdmin) {
            closedType = DiscussionTopic.ClosedTypeEnum.ADMIN;
        }
        if (user.equals(docAuthor)) {
            closedType = DiscussionTopic.ClosedTypeEnum.DOCUMENT_AUTHOR;
        }
        if (user.equals(targetTopic.get().getAuthor())) {
            closedType = DiscussionTopic.ClosedTypeEnum.TOPIC_AUTHOR;
        }
        Assert.isTrue(closedType.compareTo(DiscussionTopic.ClosedTypeEnum.NONE) != 0, "无法关闭其他用户创建的主题");
        targetTopic.get().setClosed(true);
        targetTopic.get().setClosedType(closedType);
        //discussionsPersistence.saveDiscussions(topicList);
        discussionsPersistenceHandler.writeObject(topicList);
        return targetTopic.get();
    }

    @Override
    @UseNotification(type = {Notification.NotificationType.DOC_REPLY,
            Notification.NotificationType.TOPIC_REPLY,
            Notification.NotificationType.DISCUSSION_REPLY})
    synchronized public Discussion replyTopic(DiscussionDTO discussionDTO) throws IOException {
        Optional<DiscussionTopic> targetTopic = topicList.stream()
                .filter(topic -> topic.getTopicId().equals(discussionDTO.getTopicId())).findFirst();
        Assert.isTrue(targetTopic.isPresent(), "回复的主题不存在");
        Assert.isTrue(!targetTopic.get().isClosed(), "回复的主题已关闭");
        int existedDiscussionCount = targetTopic.get().getDiscussionCount();
        Assert.isTrue(discussionDTO.getQuote() < existedDiscussionCount, "引用的回复不存在");
        Discussion discussion = Discussion.buildDiscussion(targetTopic.get(), discussionDTO);
        targetTopic.get().addDiscussions(discussion);
        targetTopic.get().setLastReplyTimeStamp(Instant.now().toEpochMilli());
        //discussionsPersistence.saveDiscussions(topicList);
        discussionsPersistenceHandler.writeObject(topicList);
        return discussion;
    }

    @Override
    public List<DiscussionTopicVO> listTopicsByDocId(String docId) {
        List<DiscussionTopicVO> result = new ArrayList<>();
        topicList.stream().filter(topic -> docId.equals(topic.getDocId())).forEach(topic -> result.add(new DiscussionTopicVO(topic)));
        return result;
    }

    @Override
    public List<DiscussionVO> listDiscussionsByTopicId(String docId, String topicId) {
        Optional<DiscussionTopic> targetTopic = topicList.stream().filter(t -> topicId.equals(t.getTopicId())).findFirst();
        Assert.isTrue(targetTopic.isPresent(), "查询的主题不存在");
        List<DiscussionVO> result = new ArrayList<>();
        targetTopic.get().getDiscussions().forEach(discussionDTO -> result.add(new DiscussionVO(targetTopic.get(), discussionDTO)));
        return result;
    }

    @Override
    public List<DiscussionTopicVO> listTopicByUser(String username) {
        List<DiscussionTopicVO> result = new ArrayList<>();
        topicList.stream().filter(topic -> username.equals(topic.getAuthor())).forEach(topic -> result.add(new DiscussionTopicVO(topic)));
        return result;
    }

    @Override
    public List<DiscussionVO> listDiscussionByUser(String username) {
        List<DiscussionVO> result = new ArrayList<>();
        topicList.forEach(topic -> {
            List<Discussion> discussions = topic.getDiscussions();
            discussions.forEach(discussion -> {
                if (username.equals(discussion.getAuthor())) {
                    result.add(new DiscussionVO(topic, discussion));
                }
            });
        });
        return result;
    }

    @Override
    public DiscussionTopicVO getTopicVO(String topicId) {
        DiscussionTopic target = topicList.stream()
                .filter(topic -> topicId.equals(topic.getTopicId()))
                .findFirst().get();
        Assert.isTrue(target != null,"回复主题不存在");
        return new DiscussionTopicVO(target);
    }

    @Override
    public Discussion getDiscussion(String topicId,int quote) {
        DiscussionTopic target = topicList.stream()
                .filter(topic -> topicId.equals(topic.getTopicId()))
                .findFirst().get();
        Assert.isTrue(target != null,"回复主题不存在");
        Assert.isTrue(quote>=0&&quote < target.getDiscussionCount(),"引用的回复不存在");
        List<Discussion> list = target.getDiscussions();
        Discussion inIndex = list.get(quote);
        if(inIndex.getIndex() == quote){
            return list.get(quote);
        }
        for(Discussion discussion: list){
            if (discussion.getIndex() == quote){
                return discussion;
            }
        }
        throw new RuntimeException("回复索引错误，主题："+topicId);
    }
}
