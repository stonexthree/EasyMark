package org.stonexthree.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.stonexthree.domin.DocService;
import org.stonexthree.domin.LabelService;
import org.stonexthree.domin.UserService;
import org.stonexthree.domin.ViewObjectFactories;
import org.stonexthree.domin.model.Document;
import org.stonexthree.domin.statistics.StatisticsService;
import org.stonexthree.domin.statistics.ViewAdd;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.ErrorCodeUtil;
import org.stonexthree.web.utils.RestResponseFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/doc")
@Slf4j
public class DocumentController {
    private DocService docService;
    private UserService userService;

    private LabelService labelService;
    private StatisticsService statisticsService;

    public DocumentController(DocService docService, UserService userService, LabelService labelService, StatisticsService statisticsService) {
        this.docService = docService;
        this.userService = userService;
        this.labelService = labelService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/list")
    public CommonResponse listDocs() {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(docService.listAllDoc(), userService.getUserNicknameMap()));
    }
    @PostMapping("/query/condition")
    public CommonResponse listDocsById(@RequestParam(value = "id") Set<String> idList) {
        return RestResponseFactory.createSuccessResponseWithData(
                    ViewObjectFactories.batchToVO(docService.listDocsByIds(idList), userService.getUserNicknameMap()));
    }

    @GetMapping("/list/user-doc/{username}")
    public CommonResponse getAllDocByUserName(@PathVariable("username") String username) {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(docService.listDocByUserName(username), userService.getUserNicknameMap()));
    }

    @GetMapping("/list/keyword/{keyword}")
    public CommonResponse getDocByKeyword(@PathVariable("keyword") String keyword) {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(docService.searchDocByName(keyword), userService.getUserNicknameMap()));
    }

    @GetMapping("/markdown/{id}")
    @ViewAdd(id = "id")
    public CommonResponse getDocContent(@PathVariable("id") String id) throws IOException {
        String content = docService.getDocContent(id);
        if (content == null) {
            return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR).setMessage("????????????????????????");
        }
        return RestResponseFactory.createSuccessResponseWithData(content);
    }

    @GetMapping("/markdown/detail/{id}")
    public CommonResponse getDoc(@PathVariable("id") String id) throws IOException {
        Document document = docService.getDocById(id);
        if (document == null) {
            return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR).setMessage("????????????????????????");
        }
        return RestResponseFactory.createSuccessResponseWithData(ViewObjectFactories.toVO(document, userService.getCurrentUserNickname()));
    }

    @DeleteMapping("/markdown/{doc-id}")
    public CommonResponse deleteDoc(@PathVariable("doc-id") String docId) throws IOException {
        if (docService.deleteDoc(SecurityContextHolder.getContext().getAuthentication().getName(), docId)) {
            return RestResponseFactory.createSuccessResponse().setMessage("????????????");
        }
        labelService.removeDocBinds(docId);
        return RestResponseFactory.createFailedResponse().setMessage("??????????????????????????????????????????????????????");
    }

    @PostMapping("/markdown")
    public CommonResponse createDoc(@RequestParam("content") String content,
                                    @RequestParam("docName") String docName) throws IOException {
        try {
            docService.createDoc(SecurityContextHolder.getContext().getAuthentication().getName(),
                    docName,
                    content, false);
        } catch (IllegalArgumentException e) {
            return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR).setMessage("??????????????????");
        }
        return RestResponseFactory.createSuccessResponse().setMessage("????????????");
    }


    /**
     * ????????????????????????????????????
     * ???????????????????????????????????????API??????
     *
     * @param content
     * @param docName
     * @param labelName
     * @return
     */
    @PostMapping("/markdown-tags")
    public CommonResponse createDocWithLabel(@RequestParam("content") String content,
                                             @RequestParam("docName") String docName,
                                             @RequestParam("labelName") Set<String> labelName) throws IOException {
        try {
            String docId = docService.createDoc(SecurityContextHolder.getContext().getAuthentication().getName(),
                    docName,
                    content, false);
            labelService.rebindLabelsToDoc(labelName, docId);
        } catch (IllegalArgumentException e) {
            return RestResponseFactory.createFailedResponse().setMessage("??????????????????");
        }
        return RestResponseFactory.createSuccessResponse().setMessage("????????????");
    }

    @PutMapping("/markdown/content/{doc-id}")
    public CommonResponse modifyDocContent(@RequestParam("content") String content,
                                           @PathVariable("doc-id") String docId) throws IOException {
        if (docService.updateDoc(SecurityContextHolder.getContext().getAuthentication().getName(), docId, content)) {
            return RestResponseFactory.createSuccessResponse().setMessage("????????????");
        }
        return RestResponseFactory.createFailedResponse().setMessage("???????????????????????????????????????????????????");
    }

    @PutMapping("/markdown/doc-name/{doc-id}")
    public CommonResponse changeDocName(@RequestParam("name") String newDocName,
                                        @PathVariable("doc-id") String docId) {
        if (docService.changeDocName(SecurityContextHolder.getContext().getAuthentication().getName(), docId, newDocName)) {
            return RestResponseFactory.createSuccessResponse().setMessage("????????????");
        }
        return RestResponseFactory.createFailedResponse().setMessage("?????????????????????????????????????????????????????????????????????");
    }

    @GetMapping("/my-doc")
    public CommonResponse getMyDocs() {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(
                        docService.listDocByUserName(SecurityContextHolder.getContext().getAuthentication().getName()),
                        userService.getUserNicknameMap())
        );
    }

    @GetMapping("/my-drafts")
    public CommonResponse listMyDrafts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return RestResponseFactory.createSuccessResponseWithData(docService.listDrafts(username));
    }

    @PostMapping("/draft")
    public CommonResponse createDraft(@RequestParam("content") String content,
                                      @RequestParam("docName") String docName) throws IOException {
        docService.createDoc(SecurityContextHolder.getContext().getAuthentication().getName(),
                docName,
                content, true);
        return RestResponseFactory.createSuccessResponse().setMessage("????????????");
    }

    @PutMapping("/draft/submit")
    public CommonResponse submitDraft(@RequestParam("doc-id") String docId,
                                      @RequestParam(value = "label-name", required = false) Set<String> labelName) throws IOException {
        docService.submitDraft(SecurityContextHolder.getContext().getAuthentication().getName(), docId);
        if (labelName != null && labelName.size() != 0) {
            labelService.rebindLabelsToDoc(labelName, docId);
        }
        return RestResponseFactory.createSuccessResponse();
    }

    @GetMapping("/collections")
    public CommonResponse listMyCollections() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<Document> collections = docService.listCollectedDocument(username);
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(collections, userService.getUserNicknameMap()));
    }

    @PutMapping("/collections/{docId}")
    public CommonResponse collectDoc(@PathVariable("docId") String docId) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        docService.collectDoc(username, docId);
        return RestResponseFactory.createSuccessResponse();
    }

    @DeleteMapping("/collections/{docId}")
    public CommonResponse removeCollectedDoc(@PathVariable("docId") String docId) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        docService.removeCollect(username, docId);
        return RestResponseFactory.createSuccessResponse();
    }

    @GetMapping("/collect/status/{docId}")
    public CommonResponse isDocCollected(@PathVariable("docId") String docId) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return RestResponseFactory.createSuccessResponseWithData(docService.isDocCollected(username, docId));
    }

    @PostMapping("/doc-archive/task/{type}")
    public CommonResponse exportDocs(@PathVariable("type") String typeString,
                                     @RequestParam(value = "target", required = false) String target) {
        DocService.ExportType type;
        Integer id;
        switch (typeString) {
            case "all" -> {
                Assert.isTrue(userService.hasRoleAdmin(), "??????????????????????????????????????????????????????");
                id = docService.createExportTask(DocService.ExportType.ALL, null);
            }
            case "user" -> {
                Assert.isTrue(target != null, "??????????????????");
                Assert.isTrue(userService.hasRoleAdmin(), "????????????????????????????????????????????????????????????");
                id = docService.createExportTask(DocService.ExportType.USER, target);
            }
            case "me" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                id = docService.createExportTask(DocService.ExportType.USER, username);
            }
            default -> throw new IllegalArgumentException("??????????????????");
        }
        docService.runExportTask(id);
        return RestResponseFactory.createSuccessResponseWithData(id);
    }

    @GetMapping("/doc-archive/task/status/{id}")
    public CommonResponse isTaskFinish(@PathVariable("id") Integer id) {
        return RestResponseFactory.createSuccessResponseWithData(docService.isTaskFinish(id));
    }

    @GetMapping("/doc-archive/task/result/{id}")
    public CommonResponse getTaskResult(@PathVariable("id") Integer id) {
        return RestResponseFactory.createSuccessResponseWithData(docService.getTaskResult(id));
    }

    @PostMapping("/owner")
    public CommonResponse distributeDocs(@RequestParam("receiver") String receiver,
                                         @RequestParam("docId") List<String> docIds) throws IOException {
        Assert.isTrue(userService.hasRoleAdmin(), "?????????????????????????????????????????????????????????");
        Assert.isTrue(userService.userExist(receiver), "???????????????????????????????????????????????????");
        docService.docDistribute(receiver, docIds);
        return RestResponseFactory.createSuccessResponse();
    }
}
