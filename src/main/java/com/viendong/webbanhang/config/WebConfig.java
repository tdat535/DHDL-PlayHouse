package com.viendong.webbanhang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tạo mapping để phục vụ file trong thư mục "uploads"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // Đường dẫn thực tế của thư mục uploads trên máy chủ
    }
}
