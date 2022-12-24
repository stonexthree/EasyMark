package org.stonexthree.domin.composite;

import org.springframework.stereotype.Component;
import org.stonexthree.domin.DocService;
import org.stonexthree.domin.LabelService;
import org.stonexthree.domin.model.DocDTO;

import java.util.HashSet;
import java.util.Set;

@Component
public class SearchServiceImpl implements SearchService{
    private DocService docService;
    private LabelService labelService;

    /**
     * 查询的范围
     */
    public static enum scope{
        //仅按文档名称查询文档
        NAME,
        //仅查找拥有指定的标签的文档
        LABEL,
        //同时查询符合上诉条件的文档
        COMPOSITE
    }

    public SearchServiceImpl(DocService docService, LabelService labelService) {
        this.docService = docService;
        this.labelService = labelService;
    }

    @Override
    public Set<DocDTO> searchDoc(String scope,String keyword) {
        Set<DocDTO> dtos = new HashSet<>();
        if(scope.equals("name")){
            dtos.addAll(docService.searchDocByName(keyword));
            return dtos;
        }
        if(scope.equals("label")){
            dtos.addAll(docService.getDocsByIds(labelService.getDocIdByLabel(keyword)));
            return dtos;
        }
        dtos.addAll(docService.searchDocByName(keyword));
        dtos.addAll(docService.getDocsByIds(labelService.searchDocByLabelKeyword(keyword)));
        return dtos;
    }
}
