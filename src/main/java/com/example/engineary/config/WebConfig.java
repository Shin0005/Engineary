package com.example.engineary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // '/api'以外の全パスをindex.htmlに転送
        registry.addViewController("/{path:[^\\.]*}")
                .setViewName("forward:/test2.html");
    }
}