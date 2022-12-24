package org.stonexthree.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.stonexthree.domin.DocService;
import org.stonexthree.domin.LabelService;
import org.stonexthree.domin.UserService;
import org.stonexthree.domin.ViewObjectFactories;
import org.stonexthree.domin.model.DocDTO;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.ErrorCodeUtil;
import org.stonexthree.web.utils.RestResponseFactory;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/doc")
public class DocumentController {
    private DocService docService;
    private UserService userService;

    private LabelService labelService;

    public DocumentController(DocService docService, UserService userService, LabelService labelService) {
        this.docService = docService;
        this.userService = userService;
        this.labelService = labelService;
    }

    @GetMapping("/list")
    public CommonResponse getAllDoc() {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(docService.getAllDoc(), userService.getUserNicknameMap()));
    }

    @GetMapping("/list/user-doc/{username}")
    public CommonResponse getAllDocByUserName(@PathVariable("username") String username) {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(docService.getDocByUserName(username), userService.getUserNicknameMap()));
    }

    @GetMapping("/list/keyword/{keyword}")
    public CommonResponse getDocByKeyword(@PathVariable("keyword") String keyword) {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(docService.searchDocByName(keyword), userService.getUserNicknameMap()));
    }

    @GetMapping("/markdown/{id}")
    public CommonResponse getDocContent(@PathVariable("id") String id) throws IOException {
        String content = docService.getDocContent(id);
        if (content == null) {
            return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR).setMessage("请求的文档不存在");
        }
        return RestResponseFactory.createSuccessResponseWithData(content);
    }

    @GetMapping("/markdown/detail/{id}")
    public CommonResponse getDoc(@PathVariable("id") String id) throws IOException {
        DocDTO docDTO = docService.getDocById(id);
        if (docDTO == null) {
            return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR).setMessage("请求的文档不存在");
        }
        return RestResponseFactory.createSuccessResponseWithData(ViewObjectFactories.toVO(docDTO, userService.getCurrentUserNickname()));
    }

    @DeleteMapping("/markdown/{name}")
    public CommonResponse deleteDoc(@PathVariable("name") String name) throws IOException {
        if (docService.deleteDoc(SecurityContextHolder.getContext().getAuthentication().getName(), name)) {
            return RestResponseFactory.createSuccessResponse().setMessage("删除成功");
        }
        return RestResponseFactory.createFailedResponse().setMessage("删除失败：文件不存在或不属于操作用户");
    }

    @PostMapping("/markdown")
    public CommonResponse createDoc(@RequestParam("content") String content,
                                    @RequestParam("docName") String docName) throws IOException {
        try {
            docService.createDoc(SecurityContextHolder.getContext().getAuthentication().getName(),
                    docName,
                    content);
        }catch (IllegalArgumentException e){
            return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR).setMessage("文件名称重复");
        }
        return RestResponseFactory.createSuccessResponse().setMessage("创建成功");
    }


    /**
     * 创建文档，并同时绑定标签
     * 暂不支持原子性，只作为组合API使用
     *
     * @param content
     * @param docName
     * @param labelName
     * @return
     */
    @PostMapping("/markdown_tags")
    public CommonResponse createDocWithLabel(@RequestParam("content") String content,
                                             @RequestParam("docName") String docName,
                                             @RequestParam("label-name") Set<String> labelName) throws IOException {
        try {
             String docId= docService.createDoc(SecurityContextHolder.getContext().getAuthentication().getName(),
                    docName,
                    content);
            labelService.rebindLabelsToDoc(labelName,docId);
        }catch (IllegalArgumentException e){
            return RestResponseFactory.createFailedResponse().setMessage("文件名称重复");
        }
        return RestResponseFactory.createSuccessResponse().setMessage("创建成功");
    }

    @PutMapping("/markdown/content/{doc-id}")
    public CommonResponse modifyDocContent(@RequestParam("content") String content,
                                           @PathVariable("doc-id") String docId) throws IOException {
        if (docService.updateDoc(SecurityContextHolder.getContext().getAuthentication().getName(), docId, content)) {
            return RestResponseFactory.createSuccessResponse().setMessage("提交成功");
        }
        return RestResponseFactory.createFailedResponse().setMessage("修改的文档不存在或试图操作他人文档");
    }

    @PutMapping("/markdown/doc-name/{doc-id}")
    public CommonResponse changeDocName(@RequestParam("name") String newDocName,
                                        @PathVariable("doc-id") String docId) {
        if (docService.changeDocName(SecurityContextHolder.getContext().getAuthentication().getName(), docId, newDocName)) {
            return RestResponseFactory.createSuccessResponse().setMessage("提交成功");
        }
        return RestResponseFactory.createFailedResponse().setMessage("修改的文档不存在或新名称重复或试图操作他人文档");
    }

    @GetMapping("/my-doc")
    public CommonResponse getMyDocs() {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(
                        docService.getDocByUserName(SecurityContextHolder.getContext().getAuthentication().getName()),
                        userService.getUserNicknameMap())
        );
    }

}
