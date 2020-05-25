package com.andrewfesta.doublesolitare.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.andrewfesta.doublesolitare.model.Foundation;
import com.andrewfesta.doublesolitare.model.GameBoard;
import com.andrewfesta.doublesolitare.model.User;

@Component
public class SyncService {

	private SimpMessageSendingOperations simpMessageSending;

	@Autowired
	public void setSimpMessageSending(SimpMessageSendingOperations simpMessageSending) {
		this.simpMessageSending = simpMessageSending;
	}
	
	public void notifyPlayerJoin(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.PLAYER_JOIN, user, game));
	}
	
	public void notifyMoveToFoundation(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.MOVE_TO_FOUNDATION, user, game));
	}
	
	enum GameUpdateAction {
		PLAYER_JOIN,
		MOVE_TO_FOUNDATION
	}

	static class GameUpdate {
		GameUpdateAction action;
		User user;
		Foundation foundation;
		
		public GameUpdate(GameUpdateAction action, User user, GameBoard game) {
			super();
			this.action = action;
			this.user = user;
			this.foundation = game.getFoundation();
		}
		public GameUpdateAction getAction() {
			return action;
		}
		public void setAction(GameUpdateAction action) {
			this.action = action;
		}
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public Foundation getFoundation() {
			return foundation;
		}
		public void setFoundation(Foundation foundation) {
			this.foundation = foundation;
		}
		
	}

}
