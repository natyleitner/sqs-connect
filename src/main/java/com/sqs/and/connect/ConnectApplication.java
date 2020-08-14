package com.sqs.and.connect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {ContextStackAutoConfiguration.class})
@RestController
public class ConnectApplication {

    @Autowired private QueueMessagingTemplate queueMessagingTemplate;

    @Value("${cloud.aws.end-point.uri}") private String endpoint;

    @GetMapping(value = "/send/{message}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendMessageToQueue(@PathVariable String message)
    {
        try
        {
            queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(message).build());
        } catch (Exception e)
        {
            return "Something went wrong - " + e;
        }
        return new StringBuilder().append("You message (")
                                  .append(message)
                                  .append(") was sent to the queue.")
                                  .toString();
    }


    //@SqsListener("RS-SQS-standard-queue")
    //public void loadMessageFromSQS(String message)  {
    //	logger.info("message from SQS Queue {}", message);
    //}


    public static void main(String[] args)
    {
        SpringApplication.run(ConnectApplication.class, args);
    }

}
