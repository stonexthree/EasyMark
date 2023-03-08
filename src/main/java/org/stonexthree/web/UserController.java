package org.stonexthree.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stonexthree.domin.DocService;
import org.stonexthree.domin.FileService;
import org.stonexthree.domin.UserService;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.ErrorCodeUtil;
import org.stonexthree.web.utils.RestResponseFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author stonexthree
 * <p>
 * 用户头像说明：上传后存到指定路径下，获取时先获取文件的名称，然后配合文件服务应用（如nginx静态代理）获取
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private UserService userService;
    private FileService fileService;

    private DocService docService;

    public UserController(UserService userService, FileService fileService, DocService docService) {
        this.userService = userService;
        this.fileService = fileService;
        this.docService = docService;
    }

    @GetMapping("/all")
    public CommonResponse listAllUsers() {
        return RestResponseFactory.createSuccessResponseWithData(userService.getAllUser());
    }

    @PostMapping("/detail")
    public CommonResponse addUser(@RequestParam String username, @RequestParam String password) {
        boolean result;
        try {
            result = userService.createUser(username, password);
        } catch (IOException e) {
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if (result) {
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户已存在");
    }

/*    @DeleteMapping("/by-name/{name}")
    public CommonResponse removeUser(@PathVariable("name") String userName){
        try{
            userService.deleteUser(userName);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        return RestResponseFactory.createSuccessResponse();
    }*/

    @PutMapping("/admin/grant")
    public CommonResponse grantAdmin(@RequestParam String username) {
        boolean result;
        try {
            result = userService.grantAdmin(username);
        } catch (IOException e) {
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if (result) {
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户不存在");
    }

    @PutMapping("/admin/remove")
    public CommonResponse removeAdmin(@RequestParam String username) {
        boolean result;
        try {
            result = userService.removeAdmin(username);
        } catch (IOException e) {
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if (result) {
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户不存在");
    }

    @PostMapping("/password")
    public CommonResponse changePassword(@RequestParam String newPassword) {
        //boolean result;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
/*        try{
            result = userService.changePassword(username,newPassword);
        }catch (IOException e){
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if(result){
            return RestResponseFactory.createSuccessResponse();
        }*/
        return setPassword(username, newPassword);
    }

    @PostMapping("/admin/password")
    public CommonResponse setPassword(@RequestParam String username, @RequestParam String password) {
        boolean result;
        try {
            result = userService.changePassword(username, password);
        } catch (IOException e) {
            log.error(e.getMessage());
            return RestResponseFactory.createFailedResponse();
        }
        if (result) {
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setMessage("用户不存在");
    }

    @PutMapping("/nickname")
    public CommonResponse setUserNickname(@RequestParam String nickname) throws IOException {
        userService.setUserNickname(SecurityContextHolder.getContext().getAuthentication().getName(), nickname);
        return RestResponseFactory.createSuccessResponse();
    }

    @GetMapping("/nickname")
    public CommonResponse getUserNickname() {
        return RestResponseFactory.createSuccessResponseWithData(userService.getUserNickname(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @GetMapping("/admin/check")
    public CommonResponse hasRoleAdmin() {
        if (userService.hasRoleAdmin()) {
            return RestResponseFactory.createSuccessResponse();
        }
        return RestResponseFactory.createFailedResponse().setCode(ErrorCodeUtil.CLIENT_AUTHORIZATION_ERROR);
    }

    @GetMapping("/profile")
    public CommonResponse getCurrentAccountProfile() {
        return RestResponseFactory.createSuccessResponseWithData(userService.getMe());
    }

    @GetMapping("/photos")
    public CommonResponse listUserPhotos(@RequestParam("usernames") Set<String> usernames) {
        return RestResponseFactory.createSuccessResponseWithData(userService.listUserPhotos(usernames));
    }

    @PostMapping("/photo")
    public CommonResponse changeMyPhoto(@RequestParam("photo") MultipartFile[] files) throws IOException {
        if (files.length != 1) {
            return RestResponseFactory.createFailedResponse()
                    .setCode(ErrorCodeUtil.CLIENT_REQUEST_PARAMETER_ERROR)
                    .setMessage("只允许上传一张图像");
        }
        for (MultipartFile file : files) {
            String[] cutArr = file.getOriginalFilename().split("\\.");
            String extendName = cutArr[cutArr.length - 1].toLowerCase();
            if(!("png".equals(extendName)||"bmp".equals(extendName)||"jpg".equals(extendName)||"jpeg".equals(extendName)||"svg".equals(extendName)||"gif".equals(extendName))){
                throw new IllegalArgumentException("上传的图片扩展名不支持");
            }
        }
        String location = fileService.fileUpload(files).get(0);
        userService.changeUserPhoto(SecurityContextHolder.getContext().getAuthentication().getName(), location);
        return RestResponseFactory.createSuccessResponse();
    }

    @DeleteMapping("/{username}")
    public CommonResponse deleteUser(@PathVariable("username") String username,
                                     @RequestParam(value = "receiver", required = false) String receiver) throws IOException {
        Assert.isTrue(userService.hasRoleAdmin(), "变更用户名：此操作仅允许管理员执行");
        String receiveUser = receiver == null ? "admin" : receiver;
        Assert.isTrue(userService.userExist(receiveUser), "指定的交接账号不存在");
        userService.deleteUser(username);
        docService.docHandOver(username, receiveUser);
        return RestResponseFactory.createSuccessResponse();
    }
}
