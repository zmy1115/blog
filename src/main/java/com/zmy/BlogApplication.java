package com.zmy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication()
@ImportResource(locations = {"classpath:/kaptcha.xml"})
@EnableAsync
public class BlogApplication //implements EmbeddedServletContainerCustomizer
{


    public static void main(String[] args) {

        SpringApplication.run(BlogApplication.class, args);

    }


}
