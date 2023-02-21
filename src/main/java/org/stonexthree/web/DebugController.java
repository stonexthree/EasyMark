package org.stonexthree.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.RestResponseFactory;

@RestController
@RequestMapping("/debug")
public class DebugController {
    private boolean debugDisabled;

    public DebugController(@Value(value = "${app-config.debug-disabled:#{true}}") Boolean debugDisabled){
        this.debugDisabled = debugDisabled == null ? true:debugDisabled;
    }
    @GetMapping("/userInfo")
    public CommonResponse userDetails(){
        Assert.isTrue(!debugDisabled,"debug已禁用");
        return RestResponseFactory.createSuccessResponseWithData(SecurityContextHolder.getContext().getAuthentication());
    }
}
