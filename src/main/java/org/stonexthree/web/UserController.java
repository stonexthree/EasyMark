package org.stonexthree.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.stonexthree.domin.UserService;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.ErrorCodeUtil;
import org.stonexthree.web.utils.RestResponseFactory;

import java.io.IOException;

/**
 * @author stonexthree
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public CommonResponse getAllUsers(){
        return RestResponseFactory.createSuccessResponseWithData(userService.getAllUser());
    }

    @PostMapping("/detail")
    public CommonResponse addUser(@RequestParam String username,@RequestParam String password){
        boolean result;
        try{
            result = userService.createUser(username,password);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if(result){
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户已存在");
    }

    @DeleteMapping("/by-name/{name}")
    public CommonResponse removeUser(@PathVariable("name") String userName){
        try{
            userService.deleteUser(userName);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        return RestResponseFactory.createSuccessResponse();
    }

    @PutMapping("/admin/grant")
    public CommonResponse grantAdmin(@RequestParam String username){
        boolean result;
        try{
            result = userService.grantAdmin(username);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if(result){
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户不存在");
    }

    @PutMapping("/admin/remove")
    public CommonResponse removeAdmin(@RequestParam String username){
        boolean result;
        try{
            result = userService.removeAdmin(username);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if(result){
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户不存在");
    }

    @PostMapping("/password")
    public CommonResponse changePassword(@RequestParam String newPassword){
        boolean result;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try{
            result = userService.changePassword(username,newPassword);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if(result){
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户不存在");
    }

    @PostMapping("/admin/password")
    public CommonResponse setPassword(@RequestParam String username,@RequestParam String password){
        boolean result;
        try{
            result = userService.changePassword(username,password);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if(result){
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户不存在");
    }

    @PutMapping("/nickname")
    public CommonResponse setUserNickname(@RequestParam String nickname){
        userService.setUserNickname(SecurityContextHolder.getContext().getAuthentication().getName(),nickname);
        return RestResponseFactory.createSuccessResponse();
    }

    @GetMapping("/nickname")
    public CommonResponse getUserNickname(){
        return RestResponseFactory.createSuccessResponseWithData(userService.getUserNickname(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @GetMapping("/admin/check")
    public CommonResponse hasRoleAdmin(){
        if(userService.hasRoleAdmin()){
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_AUTHORIZATION_ERROR);
    }

    @GetMapping("/profile")
    public CommonResponse getCurrentAccountProfile(){
        return RestResponseFactory.createSuccessResponseWithData(userService.getMe());
    }
}
