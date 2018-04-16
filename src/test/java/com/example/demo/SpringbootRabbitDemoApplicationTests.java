package com.example.demo;

import com.example.demo.provider.MQProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitDemoApplicationTests {


	@Autowired
	MQProvider mqProviderm;

	@Test
	public void contextLoads() {
		String msg = "{\"descr\":\"发送消息\"}";
		mqProviderm.send(msg);
	}


}
