package org.stonexthree.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.stonexthree.domin.Notification;
import org.stonexthree.domin.NotificationService;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.RestResponseFactory;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public CommonResponse listMyNotification(){
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return RestResponseFactory.createSuccessResponseWithData(notificationService.listNotifications(user));
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteNotification(@PathVariable("id")String id){
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.closeNotification(user,id);
        return RestResponseFactory.createSuccessResponse();
    }
}
