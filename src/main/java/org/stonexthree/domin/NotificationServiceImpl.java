package org.stonexthree.domin;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.stonexthree.persistence.PersistenceManager;
import org.stonexthree.persistence.ObjectPersistenceHandler;

@Component
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private Map<String, List<Notification>> userNotificationMap;
    private ObjectPersistenceHandler<Map<String, List<Notification>>> persistenceHandler;

    public NotificationServiceImpl(@Value("${app-config.storage.persistence.file.notification}") String filename,
                                   PersistenceManager persistenceManager) throws IOException {
        this.persistenceHandler = persistenceManager.getHandler(filename, HashMap::new);
        this.userNotificationMap = persistenceHandler.readObject();
    }

    @Override
    public void createNotification(String username, Notification.NotificationType type,
                                   String entityDescribe, String entityKey, String message) {
        List<Notification> target = userNotificationMap.get(username);
        target = target == null ? new ArrayList<>() : target;
        Notification notification = new Notification(UUID.randomUUID().toString(), type, entityDescribe, entityKey, message, Instant.now().toEpochMilli());
        target.add(notification);
        userNotificationMap.put(username, target);
        try {
            persistenceHandler.writeObject(userNotificationMap);
        } catch (IOException e) {
            log.warn("创建通知异常");
            e.printStackTrace();
        }
    }

    @Override
    public void closeNotification(String username, String id) {
        List<Notification> notificationList = userNotificationMap.get(username);
        if (notificationList == null) {
            return;
        }
        Optional<Notification> target = notificationList.stream().filter(notification -> id.equals(notification.getId())).findFirst();
        target.ifPresent(notification -> userNotificationMap.remove(notification));
        try {
            persistenceHandler.writeObject(userNotificationMap);
        } catch (IOException e) {
            log.warn("创建通知异常");
            e.printStackTrace();
        }
    }

    @Override
    public List<Notification> listNotifications(String username) {
        List<Notification> result = new ArrayList<>();
        if (userNotificationMap.containsKey(username)) {
            result.addAll(userNotificationMap.get(username));
        }
        return result;
    }


}
