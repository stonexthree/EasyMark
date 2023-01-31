package org.stonexthree.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.stonexthree.domin.UserExtendProxy;
//import org.stonexthree.persistence.MyUserDataPersistence;
import org.stonexthree.persistence.ObjectPersistenceHandler;
import org.stonexthree.persistence.PersistenceManager;
import org.stonexthree.security.JsonAuthenticationFailureHandler;
import org.stonexthree.security.JsonAuthenticationSuccessHandler;
import org.stonexthree.security.JsonLogoutSuccessHandler;
import org.stonexthree.web.utils.ErrorCodeUtil;
import org.stonexthree.web.utils.ResponseWriter;
import org.stonexthree.web.utils.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Configuration
@Slf4j
public class MyWebSecurityConfigurer {
    private JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler;
    private JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler;
    private JsonLogoutSuccessHandler jsonLogoutSuccessHandler;
    private HashMap<String, UserExtendProxy> userDetailsHashMap;
    private ObjectPersistenceHandler<Map<String, UserExtendProxy>> objectPersistenceHandler;

    /**
     * 启用严格模式的路径，这些api会设置对应的http状态码
     */
    @Value("${static-files.security.strict-mode.path-prefix}")
    private List<String> strictModePath;

    public MyWebSecurityConfigurer(@Value("${app-config.storage.persistence.file.user}") String fileName,
                                   PersistenceManager persistenceManager,
                                   JsonAuthenticationFailureHandler jsonAuthenticationFailureHandler,
                                   JsonAuthenticationSuccessHandler jsonAuthenticationSuccessHandler,
                                   JsonLogoutSuccessHandler jsonLogoutSuccessHandler) throws IOException {
        this.jsonAuthenticationFailureHandler = jsonAuthenticationFailureHandler;
        this.jsonAuthenticationSuccessHandler = jsonAuthenticationSuccessHandler;
        this.jsonLogoutSuccessHandler = jsonLogoutSuccessHandler;
        this.userDetailsHashMap = new HashMap<>();
        fileName = fileName == null ? "users.data" : fileName;
        this.objectPersistenceHandler = persistenceManager.getHandler(fileName, HashMap::new);
    }

    public HashMap<String, UserExtendProxy> getUserDetailsHashMap() {
        return userDetailsHashMap;
    }

    public ObjectPersistenceHandler<Map<String, UserExtendProxy>> getUserPersistenceHandler(){
        return objectPersistenceHandler;
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        //Map<String, UserDetails> loadedUserDetailsMap = userDataPersistence.loadUserMap();
        Map<String, UserExtendProxy> loadedUserDetailsMap;
        try {
            loadedUserDetailsMap = objectPersistenceHandler.readObject();
        } catch (IOException e) {
            loadedUserDetailsMap = new HashMap<>();
            throw new RuntimeException(e);
        }
        for (UserDetails userDetails : loadedUserDetailsMap.values()) {
            if (userDetailsHashMap.containsKey(userDetails.getUsername())) {
                continue;
            }
            UserExtendProxy userProxy = userDetails instanceof UserExtendProxy ? (UserExtendProxy) userDetails : new UserExtendProxy(userDetails, "用户");
            userDetailsHashMap.put(userProxy.getUsername(), userProxy);
            userDetailsManager.createUser(userProxy);
        }
        if (!userDetailsHashMap.containsKey("admin")) {
            String password = UUID.randomUUID().toString();
            UserDetails defaultAdmin = User
                    .withUsername("admin")
                    .password("{bcrypt}" + new BCryptPasswordEncoder().encode(password))
                    .roles("USER", "ADMIN")
                    .build();
            UserExtendProxy defaultAdminProxy = new UserExtendProxy(defaultAdmin, "管理员");
            userDetailsManager.createUser(defaultAdminProxy);
            userDetailsHashMap.put(defaultAdminProxy.getUsername(), defaultAdminProxy);
            log.info("用户 admin : " + password);
        }
        return userDetailsManager;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .antMatchers(HttpMethod.POST, "/user/detail").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/user/by-name/**").hasRole("ADMIN")
                .antMatchers("/user/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginProcessingUrl("/login")
                .successHandler(jsonAuthenticationSuccessHandler)
                .failureHandler(jsonAuthenticationFailureHandler)

                .and()
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessHandler(jsonLogoutSuccessHandler)
                .and()

                //异常处理
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    this.strictModePath.stream().forEach((path) -> {
                        if (request.getRequestURI().startsWith(path)) {
                            response.setStatus(401);
                        }
                    });
                    ResponseWriter.writeObjectAsJsonToResponse(response,
                            RestResponseFactory.createFailedResponse()
                                    .setMessage("没有登录")
                                    .setCode(ErrorCodeUtil.CLIENT_AUTHENTICATION_ERROR)
                    );
                    //authException.printStackTrace();
                    //log.info("未登录请求：IP: "+request.getRemoteAddr());
                })
                .accessDeniedHandler((request, response, authException) -> {
                    this.strictModePath.stream().forEach((path) -> {
                        if (request.getRequestURI().startsWith(path)) {
                            response.setStatus(401);
                        }
                    });
                    ResponseWriter.writeObjectAsJsonToResponse(response,
                            RestResponseFactory.createFailedResponse()
                                    .setMessage("当前账户没有访问权限")
                                    .setCode(ErrorCodeUtil.CLIENT_AUTHORIZATION_ERROR)
                    );
                })
                .and()
                .csrf().disable();

        /*http
                .authorizeHttpRequests()
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginProcessingUrl("/login")
                .successHandler(jsonAuthenticationSuccessHandler)
                .failureHandler(jsonAuthenticationFailureHandler);*/
        return http.build();
    }

}
