package com.sqs.and.connect.controllers;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/sns")
public class SnsController {

    @Autowired private AmazonSNSClient snsClient;

    @Value("${cloud.aws.end-point.sns-arn}") private String snsArn;

    @PostMapping("/product/{product}/price/{price}")
    public String publishMessageToTopic(@PathVariable(name = "product") String product,
                                        @PathVariable(name = "price") double price)
    {
        try
        {
            PublishRequest publishRequest = new PublishRequest(snsArn, product + "-" + price, "New message");
            snsClient.publish(publishRequest);
        } catch (Exception e)
        {
            return "Something went wrong pushing to SNS simple Message topic - " + e;
        }

        return new StringBuilder().append("Your product (").append(product).append(
                ") was sent to the simpleMessage SNS topic.").toString();
    }
}
