package com.example.achauthorizedservice.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.example.achauthorizedservice.model.AuthorizedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * Created by tnguyen on 11/20/18.
 */
@Service
public class AuthorizedStatementProducer {
    private static final Logger log = LoggerFactory.getLogger(AuthorizedStatementProducer.class);

    public void sendStatementDetail(AuthorizedStatement authorizedStatement, AmazonKinesis kinesisClient, String streamName) {
        byte[] bytes = authorizedStatement.toJsonAsBytes();
        // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
        if (bytes == null) {
            log.warn("Could not get JSON bytes for statement");
            return;
        }

        log.info("Putting Statement: " + authorizedStatement.toString());
        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);

        putRecord.setPartitionKey(authorizedStatement.getStatementId());
        putRecord.setData(ByteBuffer.wrap(bytes));

        try {
            kinesisClient.putRecord(putRecord);
        } catch (AmazonClientException ex) {
            log.warn("Error sending record to Amazon Kinesis.", ex);
        }
    }

    /**
     * Checks if the stream exists and is active
     *
     * @param kinesisClient Amazon Kinesis client instance
     * @param streamName Name of stream
     */
    public void validateStream(AmazonKinesis kinesisClient, String streamName) {
        try {
            DescribeStreamResult result = kinesisClient.describeStream(streamName);
            if(!"ACTIVE".equals(result.getStreamDescription().getStreamStatus())) {
                System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
                System.exit(1);
            }
        } catch (ResourceNotFoundException e) {
            System.err.println("Stream " + streamName + " does not exist. Please create it in the console.");
            System.err.println(e);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error found while describing the stream " + streamName);
            System.err.println(e);
            System.exit(1);
        }
    }
}
