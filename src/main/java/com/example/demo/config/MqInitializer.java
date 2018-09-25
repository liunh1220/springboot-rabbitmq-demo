package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;


public class MqInitializer extends TdInitializer {

    @Autowired
    private RabbitAdmin rabbitAdmin;


    @Override
    protected void doInit() {
        Map<String, QueueSetting> beansOfType = ApplicationContextHolder.context.getBeansOfType(QueueSetting.class, true, false);
        beansOfType.values().forEach(queueSetting -> {
            Queue queue = new Queue(queueSetting.getQueueName());
            rabbitAdmin.declareQueue(queue);
            Binding binding = new Binding(queue.getName(), Binding.DestinationType.QUEUE,
                    queueSetting.getExchangeName(), queueSetting.getRouteKey(), Collections.emptyMap());

            rabbitAdmin.declareBinding(binding);


        });
    }
}
