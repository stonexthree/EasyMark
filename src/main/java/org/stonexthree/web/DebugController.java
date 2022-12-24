package org.stonexthree.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.RestResponseFactory;

@RestController
@RequestMapping("/debug")
public class DebugController {
    @GetMapping("/userInfo")
    public CommonResponse userDetails(){
        return RestResponseFactory.createSuccessResponseWithData(SecurityContextHolder.getContext().getAuthentication());
    }
}
