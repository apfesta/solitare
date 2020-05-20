package com.andrewfesta.doublesolitare.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
public class SyncService {

	private SimpMessageSendingOperations simpMessageSending;

	@Autowired
	public void setSimpMessageSending(SimpMessageSendingOperations simpMessageSending) {
		this.simpMessageSending = simpMessageSending;
	}

	public void activity(Integer gameId) {
		simpMessageSending.convertAndSend("/topic/game/" + gameId + "/activity", new Payload());
	}

	static class Payload {

	}

}
