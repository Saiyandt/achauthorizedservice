package com.example.achauthorizedservice.controller;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.example.achauthorizedservice.config.AWSConfiguration;
import com.example.achauthorizedservice.model.AuthorizedStatement;
import com.example.achauthorizedservice.service.AuthorizedStatementProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Created by tnguyen on 11/20/18.
 */
@RestController
public class AuthorizedStatementController {

    @Autowired
    private AmazonKinesis geAmazonKinesis;

    @Autowired
    private AuthorizedStatementProducer authorizedStatementProducer;

    @Autowired
    private AWSConfiguration awsConfiguration;


    @RequestMapping("/sendauthorizedstatement")
    public String send() throws Exception {
        // Validate that the stream exists and is active
        authorizedStatementProducer.validateStream(geAmazonKinesis, awsConfiguration.getStreamName());

        Integer tmp = new Random().nextInt(10);
        AuthorizedStatement stm = new AuthorizedStatement("stm123", tmp.toString());
        authorizedStatementProducer.sendStatementDetail(stm, geAmazonKinesis, awsConfiguration.getStreamName());

        return "Sent!";
    }
}
