package com.sqs.and.connect.controllers;

import java.util.ArrayList;
import java.util.List;

import com.sqs.and.connect.models.Product;
import com.sqs.and.connect.repositories.ProductRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sqs")
public class SqsController {

    private List<String> messages = new ArrayList<>();

    @Autowired private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired private ProductRepository productRepository;

    @Value("${cloud.aws.end-point.sqs}") private String endpoint;


    /**
     * Test implementation of sending messages directly to the queue.
     * Note that these values won't be added to the DB automatically but can be queried with /products endpoint.
     *
     * @param product
     * @param price
     * @return message that it was sent to the queue.
     */
    @Deprecated
    @PostMapping("/product/{product}/price/{price}")
    public String sendMessageToQueue(@PathVariable String product, @PathVariable double price)
    {
        try
        {
            queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(product + "-" + price).build());
        } catch (Exception e)
        {
            return "Something went wrong pushing to SQS - " + e;
        }
        return new StringBuilder().append("Your product (")
                                  .append(product)
                                  .append(") was sent to the queue.")
                                  .toString();
    }

    @SqsListener("RS-SQS-standard-queue")
    public void loadMessageFromSQS(String message)
    {
        try
        {
            JSONObject outputJson = new JSONObject(message);
            String mes = outputJson.get("Message").toString();
            messages.add("SNS->SQS: " + mes);
            String[] mesParts = mes.split("-");
            if (mesParts.length == 2)
            {
                productRepository.saveAndFlush(new Product(mesParts[0], Double.parseDouble(mesParts[1])));
            }
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
    @GetMapping("/products")
    public List<String> readMessages()
    {
        return messages;
    }

}
