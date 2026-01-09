package com.example.awsNotifySES.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsSdkConfig {

    private final Region SEOUL_REGION = Region.AP_NORTHEAST_2;

    @Bean
    SesClient sesClient() {
        return SesClient.builder()
                .region(SEOUL_REGION)
                .build();
    }


}
