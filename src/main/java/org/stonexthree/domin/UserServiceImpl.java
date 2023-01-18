package org.stonexthree.domin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.model.UserVO;
import org.stonexthree.persistence.MyUserDataPersistence;
import org.stonexthree.security.config.MyWebSecurityConfigurer;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class UserServiceImpl implements UserService {
    private Map<String, UserExtendProxy> userMap;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final MyUserDataPersistence userDataPersistence;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(InMemoryUserDetailsManager userDetailsManager,
                           MyUserDataPersistence userDataPersistence,
                           MyWebSecurityConfigurer configurer) {
        this.userMap = configurer.getUserDetailsHashMap();
        this.userDetailsManager = userDetailsManager;
        this.userDataPersistence = userDataPersistence;
    }

    @Override
    public synchronized boolean createUser(String userName, String password) throws IOException {
        if (userMap.containsKey(userName)) {
            return false;
        }
        UserDetails user = User
                .withUsername(userName)
                .password("{bcrypt}" + bCryptPasswordEncoder.encode(password))
                .roles("USER")
                .build();
        UserExtendProxy userProxy = new UserExtendProxy(user);
        userMap.put(userName, userProxy);
        try {
            userDataPersistence.saveUserMap(userMap);
            userDetailsManager.createUser(user);
        } catch (IOException e) {
            log.error(e.getMessage());
            userMap.remove(userName);
            throw e;
        }
        return true;
    }

    @Override
    public synchronized void deleteUser(String userName) throws IOException {
        UserExtendProxy target;
        if (!userMap.containsKey(userName)) {
            return;
        }
        target = userMap.get(userName);
        userMap.remove(userName);
        try {
            userDataPersistence.saveUserMap(userMap);
            userDetailsManager.deleteUser(userName);
        } catch (IOException e) {
            log.error(e.getMessage());
            userMap.put(userName, target);
            throw e;
        }
    }

    @Override
    public List<UserVO> getAllUser() {
        List<UserVO> result = new ArrayList<>();
        for (Map.Entry<String, UserExtendProxy> entry : this.userMap.entrySet()) {
            result.add(new UserVO(entry.getKey(), entry.getValue().getNickname(),entry.getValue().getCreateTimestamp()));
        }
        return result;
    }

    @Override
    public boolean userExist(String username) {
        return userMap.containsKey(username);
    }

    @Override
    public UserVO getMe() {
        UserExtendProxy user = userMap.get(SecurityContextHolder.getContext().getAuthentication().getName());
        return new UserVO(user.getUsername(),user.getNickname(),user.getCreateTimestamp());
    }

    @Override
    public synchronized boolean grantAdmin(String userName) throws IOException {
        if (!userMap.containsKey(userName)) {
            return false;
        }
        UserExtendProxy target = userMap.get(userName);
        UserDetails newTarget = User.withUserDetails(target).roles("USER", "ADMIN").build();
        UserExtendProxy userProxy = new UserExtendProxy(newTarget, target.getNickname());
        updateUser(userProxy);
        return true;
    }

    @Override
    public synchronized boolean removeAdmin(String userName) throws IOException {
        if (!userMap.containsKey(userName)) {
            return false;
        }
        UserExtendProxy target = userMap.get(userName);
        UserDetails newTarget = User.withUserDetails(target).roles("USER").build();
        UserExtendProxy userProxy = new UserExtendProxy(newTarget, target.getNickname());
        updateUser(userProxy);
        return true;
    }

    @Override
    public synchronized boolean changePassword(String userName, String newPassword) throws IOException {
        if (!userMap.containsKey(userName)) {
            return false;
        }
        UserExtendProxy target = userMap.get(userName);
        UserDetails newTarget = User
                .withUserDetails(target)
                .password("{bcrypt}" + bCryptPasswordEncoder.encode(newPassword))
                .build();
        UserExtendProxy userProxy = new UserExtendProxy(newTarget, target.getNickname());
        updateUser(userProxy);
        return true;
    }

    public synchronized void updateUser(UserExtendProxy newUser) throws IOException {
        UserExtendProxy oldUser = userMap.get(newUser.getUsername());
        if (oldUser == null) {
            return;
        }
        userMap.put(newUser.getUsername(), newUser);
        try {
            userDataPersistence.saveUserMap(userMap);
            userDetailsManager.updateUser(newUser);
        } catch (IOException e) {
            log.error(e.getMessage());
            userMap.put(oldUser.getUsername(), oldUser);
            throw e;
        }
    }

    @Override
    public boolean setUserNickname(String userName, String nickname) {
        if (!userMap.containsKey(userName)) {
            return false;
        }
        userMap.get(userName).setNickname(nickname);
        return true;
    }

    @Override
    public String getUserNickname(String userName) {
        if (!userMap.containsKey(userName)) {
            return "用户";
        }
        return userMap.get(userName).getNickname();
    }

    @Override
    public String getCurrentUserNickname() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserNickname(username);
    }

    @Override
    public Map<String, String> getUserNicknameMap() {
        Map<String, String> result = new HashMap<>(this.userMap.size());
        this.userMap.entrySet()
                .stream()
                .forEach(entry -> result.put(entry.getKey(), entry.getValue().getNickname()));
        return result;
    }

    @Override
    public boolean hasRoleAdmin() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for(GrantedAuthority authority: authorities){
            if (authority.getAuthority().equals("ROLE_ADMIN")){
                return true;
            }
        }
        return false;
    }
}
