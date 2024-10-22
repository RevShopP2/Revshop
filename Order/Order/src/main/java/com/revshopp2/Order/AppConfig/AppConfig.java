package com.revshopp2.Order.AppConfig;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


@Configuration
public class AppConfig {

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.messageConverters(new MappingJackson2HttpMessageConverter()).build();
    }
}
