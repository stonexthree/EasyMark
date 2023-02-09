package org.stonexthree.web;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.stonexthree.domin.DocService;
import org.stonexthree.domin.LabelService;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.RestResponseFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/label")
public class LabelController {
    private LabelService labelService;
    private DocService docService;

    public LabelController(LabelService labelService,DocService docService) {
        this.labelService = labelService;
        this.docService = docService;
    }

    @GetMapping("/list")
    public CommonResponse getAllLabel(){
        return RestResponseFactory.createSuccessResponseWithData(labelService.getAllLabel());
    }

    @GetMapping("/of-doc/{doc-id}")
    public CommonResponse getLabelsOfDoc(@PathVariable("doc-id") String docId){
        Assert.isTrue(docService.docExist(docId),"文档不存在");
        return RestResponseFactory.createSuccessResponseWithData(labelService.getDocLabel(docId));
    }

    @GetMapping("/doc-list/{label-name}")
    public CommonResponse getDocListByLabel(@PathVariable("label-name") String labelName){
        return RestResponseFactory.createSuccessResponseWithData(labelService.getDocIdByLabel(labelName));
    }

    @PutMapping("/from/{old-name}/to/{new-name}")
    public CommonResponse changeLabelName(@PathVariable("old-name") String oldName,
                                          @PathVariable("new-name") String newName) throws IOException {
        if(!labelService.changeLabelName(oldName, newName)){
            return RestResponseFactory.createFailedResponse().setMessage("目标标签不存在或新名称已被使用");
        }
        return RestResponseFactory.createSuccessResponse();
    }

    @PostMapping("/map/new")
    public CommonResponse addNewMap(@RequestParam("label-name") List<String> labelName,
                                    @RequestParam("doc-id") String docId) throws IOException{
        Assert.isTrue(docService.docExist(docId),"文档不存在");
        labelService.bindLabelsToDoc(labelName,docId);
        return RestResponseFactory.createSuccessResponse();
    }

    @PostMapping("/map/set")
    public CommonResponse setMap(@RequestParam("label-name") Set<String> labelName,
                                    @RequestParam("doc-id") String docId) throws IOException{
        Assert.isTrue(docService.docExist(docId),"文档不存在");
        RestResponseFactory.createFailedResponse().setMessage("文档不存在");
        return RestResponseFactory.createSuccessResponse();
    }

    @DeleteMapping("/map/{label-name}/{doc-id}")
    public CommonResponse deleteMap(@PathVariable("label-name") String labelName,
                                    @PathVariable("doc-id") String docId) throws IOException{
        Assert.isTrue(docService.docExist(docId),"文档不存在");
        if(!labelService.removeBind(labelName,docId)){
            return RestResponseFactory.createFailedResponse().setMessage("标签不存在");
        }
        return RestResponseFactory.createSuccessResponse();
    }
/*    @DeleteMapping("/map/doc/{doc-id}")
    public CommonResponse deleteDocMap(@PathVariable("doc-id") String docId) throws IOException{
        if(!labelService.removeDocBinds(docId)){
            return RestResponseFactory.createFailedResponse().setMessage("文档不存在");
        }
        return RestResponseFactory.createSuccessResponse();
    }*/

    @GetMapping("/keyword/{keyword}")
    public CommonResponse getLabelByKeyword(@PathVariable("keyword") String keyword){
        return RestResponseFactory.createSuccessResponseWithData(labelService.searchLabel(keyword));
    }
}
