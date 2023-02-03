package org.stonexthree.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.stonexthree.domin.DiscussionService;
import org.stonexthree.domin.model.DiscussionDTO;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.RestResponseFactory;

import java.io.IOException;

@RestController
@RequestMapping("/discussion")
public class DiscussionController {
    private DiscussionService discussionService;

    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @PostMapping("/topic")
    public CommonResponse createTopic(@RequestParam("docId") String docId,
                                      @RequestParam("detail") String detail) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        this.discussionService.createTopic(docId, detail, user);
        return RestResponseFactory.createSuccessResponse();
    }

    @PostMapping("/topic/close")
    public CommonResponse closeTopic(@RequestParam("docId") String docId,
                                     @RequestParam("topicId") String topicId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication.getName();
        Boolean isAdmin = authentication.getAuthorities().stream().anyMatch(aut -> "ROLE_ADMIN".equals(aut.getAuthority()));
        this.discussionService.closeTopic(user, docId, topicId, isAdmin);
        return RestResponseFactory.createSuccessResponse();
    }

    @PostMapping("/topic/reply")
    public CommonResponse replyTopic(@RequestParam("docId") String docId,
                                     @RequestParam("topicId") String topicId,
                                     @RequestParam("detail") String detail,
                                     @RequestParam("quote") String quote) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer quoteInt = Integer.parseInt(quote);
        DiscussionDTO discussionDTO = new DiscussionDTO(docId,topicId,detail,user,quoteInt);
        this.discussionService.replyTopic(discussionDTO);
        return RestResponseFactory.createSuccessResponse();
    }

    @GetMapping("/topics/{docId}")
    public CommonResponse getTopicsByDocId(@PathVariable("docId") String docId) {
        return RestResponseFactory.createSuccessResponseWithData(this.discussionService.listTopicsByDocId(docId));
    }

    @GetMapping("/of-topics/{docId}/{topicId}")
    public CommonResponse getDiscussionsByTopicId(@PathVariable("docId") String docId,
                                                  @PathVariable("topicId") String topicId) {
        return RestResponseFactory.createSuccessResponseWithData(this.discussionService.listDiscussionsByTopicId(docId, topicId));
    }

    @GetMapping("/topics/my")
    public CommonResponse getTopicByUser() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return RestResponseFactory.createSuccessResponseWithData(this.discussionService.listTopicByUser(user));
    }

    @GetMapping("/my")
    public CommonResponse getDiscussionByUser() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return RestResponseFactory.createSuccessResponseWithData(this.discussionService.listDiscussionByUser(user));
    }
}
