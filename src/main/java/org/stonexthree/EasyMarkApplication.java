package org.stonexthree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.stonexthree.security.config.MyWebSecurityConfigurer;

@SpringBootApplication
@Import(MyWebSecurityConfigurer.class)
@EnableWebSecurity
public class EasyMarkApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyMarkApplication.class, args);
    }

}
