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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;


@Configuration
public class AmqpConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpConfig.class);

    @Autowired
    private Globals globals;

    public final static String RP_FACTORY_NAME = "rpMQContainerFactory";
    public final static String BANK_FACTORY_BEAN_NAME = "bankMQContainerFactory";

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

    /** ******************************* ConnectionFactory ************************************************************  */

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

    /** *******************************************************************************************  */

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

   /** *******************************************************************************************  */

    /**
     * rabbitTemplate
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

    /** ********************************** 消费者工厂 *********************************************************  */

    /*消费者工厂  */
    @Bean(RP_FACTORY_NAME)
    public SimpleRabbitListenerContainerFactory rpRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("factoryProvider")ConnectionFactory factoryProvider) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(factoryProvider);
        factory.setMessageConverter(jackson2JsonMessageConverter());

        //单次从队列获取数量
        factory.setPrefetchCount(globals.getDefaultPrefetchNumber());
        //#默认消费者线程数
        factory.setConcurrentConsumers(globals.getMinConsumerNumber());
        //最大消费者线程数
        factory.setMaxConcurrentConsumers(globals.getMaxConsumerNumber());
        //消息格式转换
        factory.setMessageConverter(jackson2JsonMessageConverter());
        configurer.configure(factory, factoryProvider);
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

    /** ******************************* Queue ************************************************************  */

    @ConfigurationProperties(prefix = "mq.producer.bank")
    @Bean
    public QueueSetting bankProducerQueueSett() {
        return new QueueSetting();
    }

    @ConfigurationProperties(prefix = "mq.consumer.bank")
    @Bean
    public QueueSetting bankConsumerQueueSett() {
        return new QueueSetting();
    }

    @ConfigurationProperties(prefix = "mq.notify.bank")
    @Bean
    public QueueSetting bankNotifyQueueSett() {
        return new QueueSetting();
    }

   /** ******************************* Exchange ************************************************************  */

    /**
     * 发送
     * @param rabbitAdmin
     * @return
     */
    @Bean
    public Exchange bankProducerExchange(RabbitAdmin rabbitAdmin, @Qualifier("bankProducerQueueSett") QueueSetting bankProducerQueueSett){
        Exchange exchange = new DirectExchange(bankProducerQueueSett().getExchangeName());
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    /**
     * 发送
     * @param rabbitAdmin
     * @return
     */
    @Bean
    public Exchange bankConsumerExchange(RabbitAdmin rabbitAdmin, @Qualifier("bankConsumerQueueSett") QueueSetting bankConsumerQueueSett){
        Exchange exchange = new DirectExchange(bankConsumerQueueSett().getExchangeName());
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }


    /**
     * 广播结果
     * @param rabbitAdmin
     * @return
     */
    @Bean
    public Exchange bankNotifyExchange(RabbitAdmin rabbitAdmin, @Qualifier("bankNotifyQueueSett") QueueSetting bankNotifyQueueSett){
        Exchange exchange = new FanoutExchange(bankNotifyQueueSett().getExchangeName());
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    /** *******************************************************************************************  */

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

