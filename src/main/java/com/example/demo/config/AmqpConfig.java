package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;

@Configuration
public class AmqpConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpConfig.class);

    public final static String RP_FACTORY_NAME = "rpMQContainerFactory";
    public final static String BANK_FACTORY_BEAN_NAME = "bankMQContainerFactory";

    @Value("${mq.exchangeNameBank:amq.direct}")
    private String exchangeNameBank;
    @Value("${mq.exchangeNameTypeBank:direct}")
    private String exchangeNameTypeBank;
    @Value("${mq.queueNameBank:amq_direct_queue}")
    private String queueNameBank;
    @Value("${mq.routeKeyBank:route_direct}")
    private String routeKeyBank;

    @Value("${mq.exchangeNameNotify:amq.fanout}")
    private String exchangeNameNotify;
    @Value("${mq.exchangeNameTypeNotify:fanout}")
    private String exchangeNameTypeNotify;
    @Value("${mq.queueNameNotify:amq_fanout_queue}")
    private String queueNameNotify;
    @Value("${mq.routeKeyNotify:route_fanout}")
    private String routeKeyNotify;

    @Value("${spring.rabbitmq.host:127.0.0.1}")
    private String notifyMqHost;
    @Value("${spring.rabbitmq.port:5672}")
    private Integer notifyMqPort;
    @Value("${spring.rabbitmq.username:admin}")
    private String notifyMqUsername;
    @Value("${spring.rabbitmq.password:admin}")
    private String notifyMqPassword;
    @Value("${spring.rabbitmq.channelCacheSize:1024}")
    private Integer notifyMqChannelCacheSize;
    @Value("${spring.rabbitmq.publisherConfirms:true}")
    private String notifyMqPublisherConfirms;

    @Bean(name = "factoryProvider")
    @Primary
    public CachingConnectionFactory factoryProvider(){
        CachingConnectionFactory factoryProvider = new CachingConnectionFactory();
        factoryProvider.setHost(notifyMqHost);
        factoryProvider.setPort(notifyMqPort);
        factoryProvider.setUsername(notifyMqUsername);
        factoryProvider.setPassword(notifyMqPassword);
        factoryProvider.setVirtualHost("/");
        return factoryProvider;
    }

    @Bean(name = "factoryConsumerCgt")
    public CachingConnectionFactory factoryConsumerCgt(){
        CachingConnectionFactory factoryConsumer = new CachingConnectionFactory();
        factoryConsumer.setHost(notifyMqHost);
        factoryConsumer.setPort(notifyMqPort);
        factoryConsumer.setUsername(notifyMqUsername);
        factoryConsumer.setPassword(notifyMqPassword);
        factoryConsumer.setVirtualHost("/bank");
        return factoryConsumer;
    }

    /**
     * 生产者
     *
     * @param rabbitAdmin
     * @return
     */
    @Bean
    public AmqpTemplate rabbitTemplate(RabbitAdmin rabbitAdmin) {
        RabbitTemplate rabbitTemplate = rabbitAdmin.getRabbitTemplate();
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory factoryProvider) {
        return new RabbitAdmin(factoryProvider);
    }


    /*消费者工厂 start*/
    @Bean(RP_FACTORY_NAME)
    public SimpleRabbitListenerContainerFactory rpRabbitListenerContainerFactory(
            //SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("factoryProvider")ConnectionFactory factoryProvider) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(factoryProvider);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    @Bean(BANK_FACTORY_BEAN_NAME)
    public SimpleRabbitListenerContainerFactory bankRabbitListenerContainerFactory(
            @Qualifier("factoryConsumerCgt")ConnectionFactory factoryConsumerCgt) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(factoryConsumerCgt);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }
    /*消费者工厂 end*/

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 发送
     * @param rabbitAdmin
     * @return
     */
    @Bean
    public Exchange bankExchange(RabbitAdmin rabbitAdmin){
        Exchange exchange = new DirectExchange(exchangeNameBank);
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }


    /**
     * 广播结果
     * @param rabbitAdmin
     * @return
     */
    @Bean
    public Exchange notifyExchange(RabbitAdmin rabbitAdmin){
        Exchange exchange = new FanoutExchange(exchangeNameNotify);
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    /**
     * 发送队列
     * @param rabbitAdmin
     * @param bankExchange
     * @return
     */
    @Bean
    public Queue bankQueue(RabbitAdmin rabbitAdmin,Exchange bankExchange){
        Queue queue = new Queue(queueNameBank, true);
        rabbitAdmin.declareQueue(queue);
        Binding binding = new Binding(queue.getName(), Binding.DestinationType.QUEUE,
                exchangeNameBank,queueNameBank, Collections.emptyMap());
        rabbitAdmin.declareBinding(binding);
        return queue;
    }

    /**
     * 广播返回结果
     *
     */
    @Bean
    public Queue notifyQueue(RabbitAdmin rabbitAdmin,Exchange notifyExchange){
        Queue queue = new Queue(queueNameNotify, true);
        rabbitAdmin.declareQueue(queue);
        Binding binding = new Binding(queue.getName(), Binding.DestinationType.QUEUE,
                exchangeNameNotify,queueNameNotify, Collections.emptyMap());
        rabbitAdmin.declareBinding(binding);
        return queue;
    }

    public String getExchangeNameBank() {
        return exchangeNameBank;
    }

    public void setExchangeNameBank(String exchangeNameBank) {
        this.exchangeNameBank = exchangeNameBank;
    }

    public String getExchangeNameTypeBank() {
        return exchangeNameTypeBank;
    }

    public void setExchangeNameTypeBank(String exchangeNameTypeBank) {
        this.exchangeNameTypeBank = exchangeNameTypeBank;
    }

    public String getQueueNameBank() {
        return queueNameBank;
    }

    public void setQueueNameBank(String queueNameBank) {
        this.queueNameBank = queueNameBank;
    }

    public String getRouteKeyBank() {
        return routeKeyBank;
    }

    public void setRouteKeyBank(String routeKeyBank) {
        this.routeKeyBank = routeKeyBank;
    }

    public String getExchangeNameNotify() {
        return exchangeNameNotify;
    }

    public void setExchangeNameNotify(String exchangeNameNotify) {
        this.exchangeNameNotify = exchangeNameNotify;
    }

    public String getExchangeNameTypeNotify() {
        return exchangeNameTypeNotify;
    }

    public void setExchangeNameTypeNotify(String exchangeNameTypeNotify) {
        this.exchangeNameTypeNotify = exchangeNameTypeNotify;
    }

    public String getQueueNameNotify() {
        return queueNameNotify;
    }

    public void setQueueNameNotify(String queueNameNotify) {
        this.queueNameNotify = queueNameNotify;
    }

    public String getRouteKeyNotify() {
        return routeKeyNotify;
    }

    public void setRouteKeyNotify(String routeKeyNotify) {
        this.routeKeyNotify = routeKeyNotify;
    }

    public String getNotifyMqHost() {
        return notifyMqHost;
    }

    public void setNotifyMqHost(String notifyMqHost) {
        this.notifyMqHost = notifyMqHost;
    }

    public Integer getNotifyMqPort() {
        return notifyMqPort;
    }

    public void setNotifyMqPort(Integer notifyMqPort) {
        this.notifyMqPort = notifyMqPort;
    }

    public String getNotifyMqUsername() {
        return notifyMqUsername;
    }

    public void setNotifyMqUsername(String notifyMqUsername) {
        this.notifyMqUsername = notifyMqUsername;
    }

    public String getNotifyMqPassword() {
        return notifyMqPassword;
    }

    public void setNotifyMqPassword(String notifyMqPassword) {
        this.notifyMqPassword = notifyMqPassword;
    }

    public Integer getNotifyMqChannelCacheSize() {
        return notifyMqChannelCacheSize;
    }

    public void setNotifyMqChannelCacheSize(Integer notifyMqChannelCacheSize) {
        this.notifyMqChannelCacheSize = notifyMqChannelCacheSize;
    }

    public String getNotifyMqPublisherConfirms() {
        return notifyMqPublisherConfirms;
    }

    public void setNotifyMqPublisherConfirms(String notifyMqPublisherConfirms) {
        this.notifyMqPublisherConfirms = notifyMqPublisherConfirms;
    }


}

