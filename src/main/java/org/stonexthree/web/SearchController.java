package org.stonexthree.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stonexthree.domin.UserService;
import org.stonexthree.domin.ViewObjectFactories;
import org.stonexthree.domin.composite.SearchService;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.RestResponseFactory;

@RestController
@RequestMapping("/search")
public class SearchController {
    private SearchService searchService;
    private UserService userService;

    public SearchController(SearchService searchService, UserService userService) {
        this.searchService = searchService;
        this.userService = userService;
    }

    @GetMapping("/scope/{scope}/{keyword}")
    public CommonResponse searchDocs(@PathVariable("scope") String scope,
                                     @PathVariable("keyword") String keyword) {
        return RestResponseFactory.createSuccessResponseWithData(
                ViewObjectFactories.batchToVO(searchService.searchDoc(scope, keyword), userService.getUserNicknameMap())
        );
    }
}