package org.stonexthree.domin;

import org.springframework.security.core.userdetails.UserDetails;
import org.stonexthree.domin.model.UserVO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {
    boolean createUser(String userName,String password) throws IOException;
    void deleteUser(String userName) throws IOException;
    List<UserVO> getAllUser();
    boolean userExist(String username);
    UserVO getMe();
    boolean grantAdmin(String name) throws IOException;
    boolean removeAdmin(String userName) throws IOException;
    boolean changePassword(String userName,String newPassword) throws IOException;
    boolean setUserNickname(String userName,String nickname);
    String getUserNickname(String userName);

    String getCurrentUserNickname();
    Map<String,String> getUserNicknameMap();

    boolean hasRoleAdmin();
}
