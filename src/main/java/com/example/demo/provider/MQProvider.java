package com.example.demo.provider;

import com.example.demo.config.AmqpConfig;
import com.example.demo.config.QueueSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 广播银行返回结果
 *
 * Created by liulanhua on 2018/3/29.
 */
@Component
public class MQProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(MQProvider.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private QueueSetting bankNotifyQueueSett;

    public void send(String msg) {

        LOGGER.info("MQ广播消息:", msg);

        rabbitTemplate.convertAndSend(bankNotifyQueueSett.getExchangeName(), bankNotifyQueueSett.getRouteKey(), msg);
    }


}
