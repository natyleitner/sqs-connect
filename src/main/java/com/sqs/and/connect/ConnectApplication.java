package com.sqs.and.connect;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {ContextStackAutoConfiguration.class})
@RestController
public class ConnectApplication {

    private List<String> messages = new ArrayList<>();

    @Autowired private QueueMessagingTemplate queueMessagingTemplate;
    @Autowired private AmazonSNSClient snsClient;

    @Value("${cloud.aws.end-point.sqs}") private String endpoint;
    @Value("${cloud.aws.end-point.sns-arn}") private String snsArn;

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendWelcomeMessage()
    {
        return "Go to send/{message} to send a message to the queue.";
    }


    /**
     * Enpoint to send a message directly to AWS SQS
     *
     * @param message a message that will be added to the queue
     * @return text with the message or error message
     */
    @GetMapping(value = "/send/{message}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendMessageToQueue(@PathVariable String message)
    {
        try
        {
            queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(message).build());
        } catch (Exception e)
        {
            return "Something went wrong pushing to SQS - " + e;
        }
        return new StringBuilder().append("You message (")
                                  .append(message)
                                  .append(") was sent to the queue.")
                                  .toString();
    }

    /**
     * Send message to SNS simpleMessage topic which will pass it onto SQS queue
     *
     * @param message a message that will be added to the exchange and to the queue
     * @return text with the message or error message
     */
    @GetMapping("/sendMessage/{message}")
    public String publishMessageToTopic(@PathVariable String message)
    {
        try
        {
            PublishRequest publishRequest = new PublishRequest(snsArn, message, "New message");
            snsClient.publish(publishRequest);
        } catch (Exception e)
        {
            return "Something went wrong pushing to SNS simple Message topic - " + e;
        }

        return new StringBuilder().append("You message (").append(message).append(
                ") was sent to the simpleMessage SNS topic.").toString();
    }


    @SqsListener("RS-SQS-standard-queue")
    public void loadMessageFromSQS(String message)
    {
        try
        {
            JSONObject outputJson = new JSONObject(message);
            messages.add("SNS->SQS: " + outputJson.get("Message").toString());
        } catch (Exception e)
        {
            messages.add("SQS: " + message);
        }
    }

    /**
     * Shows all messages that have been polled from the queue
     *
     * @return list of messages
     */
    @GetMapping("/read")
    public String readMessages()
    {
        return messages.toString();
    }


    public static void main(String[] args)
    {
        SpringApplication.run(ConnectApplication.class, args);
    }

}
