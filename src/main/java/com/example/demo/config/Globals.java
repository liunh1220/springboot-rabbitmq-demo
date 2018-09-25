package com.example.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Globals {

    @Value("${cg.project.maxerror:5}")
    private int maxError;

    @Value("${mq.defaultPrefetchNumber:1}")
    private int defaultPrefetchNumber;

    @Value("${mq.minConsumerNumber:1}")
    private int minConsumerNumber;

    @Value("${mq.minConsumerNumber:1}")
    private int maxConsumerNumber;



    public int getMaxError() {
        return maxError;
    }

    public Globals setMaxError(int maxError) {
        this.maxError = maxError;
        return this;
    }

    public int getDefaultPrefetchNumber() {
        return defaultPrefetchNumber;
    }

    public void setDefaultPrefetchNumber(int defaultPrefetchNumber) {
        this.defaultPrefetchNumber = defaultPrefetchNumber;
    }

    public int getMinConsumerNumber() {
        return minConsumerNumber;
    }

    public void setMinConsumerNumber(int minConsumerNumber) {
        this.minConsumerNumber = minConsumerNumber;
    }

    public int getMaxConsumerNumber() {
        return maxConsumerNumber;
    }

    public void setMaxConsumerNumber(int maxConsumerNumber) {
        this.maxConsumerNumber = maxConsumerNumber;
    }



}
