package org.stonexthree.domin;

import org.stonexthree.domin.model.DiscussionDTO;
import org.stonexthree.domin.model.DiscussionTopicVO;
import org.stonexthree.domin.model.DiscussionVO;

import java.io.IOException;
import java.util.List;

/**
 * 文档回复服务，用户可以对文档提交一个回复主题，并对主题进行回复来讨论
 */
public interface DiscussionService {
    /**
     * 在指定文档下创建一个回复的主题
     *
     * @param docId  文档ID
     * @param detail 主题内容
     * @param author 主题作者
     * @return
     * @throws IOException
     */
    DiscussionTopic createTopic(String docId, String detail, String author) throws IOException;

    /**
     * void closeTopic(String user, String docId, String topicId, boolean isAdmin)
     * <br/>
     * 关闭一个主题。只有管理员、主题创建者、文档作者可以执行这个操作。
     *
     * @param user    操作用户名
     * @param docId   文档ID
     * @param topicId 主题ID
     * @param isAdmin 是否为管理员操作
     * @return
     * @throws IOException
     */
    DiscussionTopic closeTopic(String user,String docAuthor, String docId, String topicId, boolean isAdmin) throws IOException;

    /**
     * 回复一个主题
     *
     * @param discussionDTO 回复的数据对象
     * @return
     * @throws IOException
     */
    Discussion replyTopic(DiscussionDTO discussionDTO) throws IOException;

    /**
     * 获取指定文档下的所有主题
     * @param docId 文档ID
     * @return
     */
    List<DiscussionTopicVO> listTopicsByDocId(String docId);

    /**
     * 获取指定主题下的所有回复
     * @param docId 文档ID
     * @param topicId 主题ID
     * @return
     */
    List<DiscussionVO> listDiscussionsByTopicId(String docId, String topicId);

    /**
     * 获取用户创建的所有主题
     * @param username 用户名称
     * @return
     */
    List<DiscussionTopicVO> listTopicByUser(String username);

    /**
     * 获取用户发表的所有回复
     * @param username
     * @return
     */
    List<DiscussionVO> listDiscussionByUser(String username);

    DiscussionTopicVO getTopicVO(String topicId);

    Discussion getDiscussion(String topicId,int quote);
}
