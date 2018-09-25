package com.example.demo.consumer;

import com.example.demo.config.AmqpConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * MQ消费者
 * Created by liulanhua on 2018/1/22.
 */
@Component
public class MQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MQConsumer.class);

     /**
     * 消费数据发到MQ
     *
     * @param messageSource
     * @param channel
     * @param tag
     * @throws IOException
     * @throws InterruptedException
     * @throws ListenerExecutionFailedException
     */
     @RabbitListener(containerFactory = AmqpConfig.BANK_FACTORY_BEAN_NAME,
             bindings = @QueueBinding(value = @Queue(value = "${mq.consumer.bank.queueName}", durable = "true",
             exclusive = "false", autoDelete = "false"),
                     exchange = @Exchange(value = "${mq.consumer.bank.exchangeName}", durable = "${mq.consumer.bank.durable}",
                     type = ExchangeTypes.DIRECT), key = "${mq.consumer.bank.routeKey}"), admin = "rabbitAdmin")
     public void processBank(byte[] messageSource, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
            throws IOException, InterruptedException, ListenerExecutionFailedException {
        try {
            String message = new String(messageSource, "UTF-8");
            logger.info("接收[${mq.queueNamePre}]的消息：{}" ,message);
            if (StringUtils.isEmpty(message)) {
                logger.error("接收 message 为空");
            }

            //TODO

        } finally {
            channel.basicAck(tag, false);
        }
    }


}
