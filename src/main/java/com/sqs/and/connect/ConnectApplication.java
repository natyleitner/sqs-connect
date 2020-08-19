package com.sqs.and.connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;

@SpringBootApplication(exclude = {ContextStackAutoConfiguration.class})
public class ConnectApplication {


    public static void main(String[] args)
    {
        SpringApplication.run(ConnectApplication.class, args);
    }

}
