package org.stonexthree.domin;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.stonexthree.domin.model.DiscussionDTO;
import org.stonexthree.domin.model.DiscussionTopicVO;
import org.stonexthree.domin.model.DiscussionVO;
import org.stonexthree.persistence.DiscussionsPersistence;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * 回复服务的实现。所有的主题都放在一个ArrayList中，主题对象持有相关的所有回复
 */
@Component
public class DiscussionServiceImpl implements DiscussionService {
    private DiscussionsPersistence discussionsPersistence;
    private List<DiscussionTopic> topicList;
    private DocService docService;
    private UserService userService;

    public DiscussionServiceImpl(DiscussionsPersistence discussionsPersistence, DocService docService, UserService userService) throws IOException {
        this.userService = userService;
        this.docService = docService;
        this.discussionsPersistence = discussionsPersistence;
        this.topicList = discussionsPersistence.loadDiscussions();
    }


    @Override
    public void createTopic(String docId, String detail, String author) throws IOException {
        Assert.isTrue(docService.docExist(docId), "文档不存在");
        String id = UUID.randomUUID().toString();
        DiscussionTopic topic = new DiscussionTopic(docId, id, detail, author);
        topicList.add(topic);
        discussionsPersistence.saveDiscussions(topicList);
    }

    @Override
    public void closeTopic(String user, String docId, String topicId, boolean isAdmin) throws IOException {
        Assert.isTrue(docService.docExist(docId), "文档不存在");
        String docAuthor = docService.getDocById(docId).getDocAuthor();
        Optional<DiscussionTopic> targetTopic = topicList.stream().filter(t -> topicId.equals(t.getTopicId())).findFirst();
        Assert.isTrue(targetTopic.isPresent(), "关闭的主题不存在");
        //设置关闭类型（来源），前面的会被后面的覆盖
        DiscussionTopic.ClosedTypeEnum closedType = DiscussionTopic.ClosedTypeEnum.NONE;
        if(isAdmin){
            closedType = DiscussionTopic.ClosedTypeEnum.ADMIN;
        }
        if(user.equals(docAuthor)){
            closedType = DiscussionTopic.ClosedTypeEnum.DOCUMENT_AUTHOR;
        }
        if(user.equals(targetTopic.get().getAuthor())){
            closedType = DiscussionTopic.ClosedTypeEnum.TOPIC_AUTHOR;
        }
        Assert.isTrue(closedType.compareTo(DiscussionTopic.ClosedTypeEnum.NONE)!=0,"无法关闭其他用户创建的主题");
        targetTopic.get().setClosed(true);
        targetTopic.get().setClosedType(closedType);
        discussionsPersistence.saveDiscussions(topicList);
    }

    @Override
    synchronized public void replyTopic(DiscussionDTO discussionDTO) throws IOException {
        Assert.isTrue(docService.docExist(discussionDTO.getDocId()), "文档不存在");
        Optional<DiscussionTopic> targetTopic = topicList.stream()
                .filter(topic -> topic.getTopicId().equals(discussionDTO.getTopicId())).findFirst();
        Assert.isTrue(targetTopic.isPresent(), "回复的主题不存在");
        Assert.isTrue(!targetTopic.get().isClosed(),"回复的主题已关闭");
        int existedDiscussionCount = targetTopic.get().getDiscussionCount();
        Assert.isTrue(discussionDTO.getQuote() < existedDiscussionCount,"引用的回复不存在");
        Discussion discussion = Discussion.buildDiscussion(targetTopic.get(),discussionDTO);
        targetTopic.get().addDiscussions(discussion);
        targetTopic.get().setLastReplyTimeStamp(Instant.now().toEpochMilli());
        discussionsPersistence.saveDiscussions(topicList);
    }

    @Override
    public List<DiscussionTopicVO> listTopicsByDocId(String docId) {
        Assert.isTrue(docService.docExist(docId), "文档不存在");
        List<DiscussionTopicVO> result = new ArrayList<>();
        topicList.stream().filter(topic -> docId.equals(topic.getDocId())).forEach(topic -> result.add(new DiscussionTopicVO(topic)));
        return result;
    }

    @Override
    public List<DiscussionVO> listDiscussionsByTopicId(String docId, String topicId) {
        Assert.isTrue(docService.docExist(docId), "文档不存在");
        Optional<DiscussionTopic> targetTopic = topicList.stream().filter(t->topicId.equals(t.getTopicId())).findFirst();
        Assert.isTrue(targetTopic.isPresent(), "查询的主题不存在");
        List<DiscussionVO> result = new ArrayList<>();
        targetTopic.get().getDiscussions().forEach(discussionDTO -> result.add(new DiscussionVO(targetTopic.get(),discussionDTO)));
        return result;
    }

    @Override
    public List<DiscussionTopicVO> listTopicByUser(String username) {
        Assert.isTrue(userService.userExist(username), "查询的用户不存在");
        List<DiscussionTopicVO> result = new ArrayList<>();
        topicList.stream().filter(topic -> username.equals(topic.getAuthor())).forEach(topic -> result.add(new DiscussionTopicVO(topic)));
        return result;
    }

    @Override
    public List<DiscussionVO> listDiscussionByUser(String username) {
        Assert.isTrue(userService.userExist(username), "查询的用户不存在");
        List<DiscussionVO> result = new ArrayList<>();
        topicList.forEach(topic -> {
            List<Discussion> discussions = topic.getDiscussions();
            discussions.forEach(discussion -> {
                if(username.equals(discussion.getAuthor())){
                    result.add(new DiscussionVO(topic,discussion));
                }
            });
        });
        return result;
    }
}
