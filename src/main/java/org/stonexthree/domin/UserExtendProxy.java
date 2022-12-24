package org.stonexthree.domin;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

public class UserExtendProxy implements UserDetails {
    private UserDetails userDetails;
    private String nickname;
    private Long createTimestamp;

    public UserExtendProxy(UserDetails userDetails, String nickname) {
        this.userDetails = userDetails;
        this.nickname = nickname;
        this.createTimestamp = Instant.now().toEpochMilli();
    }

    public UserExtendProxy(UserDetails userDetails){
        this.userDetails = userDetails;
        this.nickname = "用户";
        this.createTimestamp = Instant.now().toEpochMilli();
    }

    public String getNickname(){
        return this.nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public Long getCreateTimestamp(){
        return this.createTimestamp;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userDetails.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.userDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.userDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.userDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.userDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.userDetails.isEnabled();
    }
}
