package org.stonexthree.domin;
import org.stonexthree.domin.Notification.NotificationType;

import java.io.IOException;
import java.util.List;

public interface NotificationService {
    void createNotification(String username,NotificationType type,String entityDescribe,String entityKey,String message) ;
    void closeNotification(String username,String id);
    List<Notification> listNotifications(String username);
}
