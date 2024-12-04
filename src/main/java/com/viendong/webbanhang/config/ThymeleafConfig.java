package com.viendong.webbanhang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");  // Đảm bảo rằng thư mục "templates" nằm trong src/main/resources
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);  // Tắt cache trong môi trường phát triển
        return templateResolver;
    }
}
