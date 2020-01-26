package com.msa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MQEventListener {
	
	private Logger logger = LoggerFactory.getLogger(MQEventListener.class);
	
	@StreamListener(target=Sink.INPUT)
	public void processEvent(String msg) {
		logger.info("MQEventListener processEvent  : " + msg);
	}

}
