package org.stonexthree.domin;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.DiscussionDTO;
import org.stonexthree.domin.Discussion;
import org.stonexthree.domin.model.DiscussionTopicVO;
import org.stonexthree.domin.model.DiscussionVO;
import org.stonexthree.domin.model.Document;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Aspect
@Component
@Slf4j
public class NotificationAspect {
    private DocService docService;
    private DiscussionService discussionService;
    private NotificationService notificationService;

    public NotificationAspect(DocService docService,
                              DiscussionService discussionService,
                              NotificationService notificationService) {
        this.docService = docService;
        this.discussionService = discussionService;
        this.notificationService = notificationService;
    }

    @Pointcut("@annotation(org.stonexthree.domin.UseNotification)")
    public void notificationCut(){};

    @Around("notificationCut()")
    public Object useNotification(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if(!(signature instanceof MethodSignature)){
            log.warn("切点类型解析异常");
            throw new RuntimeException("切点类型解析异常");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method =methodSignature.getMethod();
        UseNotification annotation = method.getAnnotation(UseNotification.class);
        Notification.NotificationType[] types = annotation.type();
        Object[] args = joinPoint.getArgs();
        String[] argNames = methodSignature.getParameterNames();
        Object result = joinPoint.proceed(args);
        try {
            for (int i = 0; i < types.length; i++) {
                switch (types[i]) {
                    case DOC_REPLY -> whenDocReply(argNames, args, result);
                    case TOPIC_REPLY -> whenTopicReply(argNames, args, result);
                    case DISCUSSION_REPLY -> whenDiscussionReply(argNames, args, result);
                    case TOPIC_CLOSED -> whenTopicClose(argNames, args, result);
                    default -> {
                    }
                }
            }
        }catch (RuntimeException e){
            log.warn(e.getMessage()+":\n"+methodSignature.getName());
        }
        return result;
    }

    private void whenDocReply(String[] argNames,Object[] args,Object result) {
        if(result instanceof Discussion){
            Discussion discussion = (Discussion) result;
            Document document = docService.getDocById(discussion.getDocId());
            notificationService.createNotification(
                    document.getDocAuthor(),
                    Notification.NotificationType.DOC_REPLY,
                    document.getDocName(),
                    discussion.getDocId()+"#"+discussion.getTopicId()+"#"+discussion.getIndex(),
                    discussion.getDetail()
            );
            return;
        }
        if(result instanceof DiscussionTopic){
            DiscussionTopic topic = (DiscussionTopic) result;
            Document document = docService.getDocById(topic.getDocId());
            notificationService.createNotification(
                    docService.getDocById(topic.getDocId()).getDocAuthor(),
                    Notification.NotificationType.DOC_REPLY,
                    document.getDocName(),
                    document.getDocId()+"#"+topic.getTopicId(),
                    topic.getDetail()
            );
            return;
        }
        throw new RuntimeException("创建通知失败：没有合适的返回值");
    }
    private void whenTopicReply(String[] argNames,Object[] args,Object result) {
        if(result instanceof Discussion){
            Discussion discussion = (Discussion) result;
            Document document = docService.getDocById(discussion.getDocId());
            DiscussionTopicVO topicVO;
            try{
                topicVO = discussionService.getTopicVO(discussion.getTopicId());
            }catch (IllegalArgumentException e){
                log.warn("创建通知异常：没有找到对应的回复主题\n"+discussion.getTopicId());
                return;
            }
            notificationService.createNotification(
                    topicVO.getAuthor(),
                    Notification.NotificationType.TOPIC_REPLY,
                    topicVO.getDetail(),
                    discussion.getDocId()+"#"+discussion.getTopicId()+"#"+discussion.getIndex(),
                    discussion.getDetail()
            );
            return;
        }
        throw new RuntimeException("创建通知失败：没有合适的返回值");
    }
    private void whenDiscussionReply(String[] argNames,Object[] args,Object result) {
        if(result instanceof Discussion){
            Discussion discussion = (Discussion) result;
            if(discussion.getQuote()<0){
                return;
            }
            Discussion quoteDiscussion = discussionService.getDiscussion(discussion.getTopicId(), discussion.getQuote());
            notificationService.createNotification(
                    quoteDiscussion.getAuthor(),
                    Notification.NotificationType.DISCUSSION_REPLY,
                    quoteDiscussion.getDetail(),
                    discussion.getDocId()+"#"+discussion.getTopicId()+"#"+discussion.getIndex(),
                    discussion.getDetail()
            );
            return;
        }
        throw new RuntimeException("创建通知失败：没有合适的返回值");
    }
    private void whenTopicClose(String[] argNames,Object[] args,Object result) {
        if(result instanceof DiscussionTopic){
            DiscussionTopic topic = (DiscussionTopic) result;
            notificationService.createNotification(
                    topic.getAuthor(),
                    Notification.NotificationType.TOPIC_CLOSED,
                    topic.getDetail(),
                    topic.getDocId()+"#"+topic.getTopicId(),
                    topic.getClosedType().name()
            );
            return;
        }
        throw new RuntimeException("创建通知失败：没有合适的返回值");
    }
}
