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
	
	public void notifyPlayerDrop(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.PLAYER_DROP, user, game));
	}
	
	public void notifyMoveToFoundation(GameBoard game, User user,
			Integer cardId,
			Integer toFoundationId) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.MOVE_TO_FOUNDATION, user, game, cardId, toFoundationId));
	}
	
	enum GameUpdateAction {
		PLAYER_JOIN,
		PLAYER_DROP,
		MOVE_TO_FOUNDATION
	}

	static class GameUpdate {
		final GameUpdateAction action;
		final User user;
		final Foundation foundation;
		Integer cardId;
		Integer toFoundationId;
		
		public GameUpdate(GameUpdateAction action, User user, GameBoard game) {
			super();
			this.action = action;
			this.user = user;
			this.foundation = game.getFoundation();
		}
		public GameUpdate(GameUpdateAction action, User user, GameBoard game, 
				Integer cardId, Integer toFoundationId) {
			this(action, user, game);
			this.cardId = cardId;
			this.toFoundationId = toFoundationId;
		}
		public GameUpdateAction getAction() {
			return action;
		}
		public User getUser() {
			return user;
		}
		public Foundation getFoundation() {
			return foundation;
		}
		public Integer getCardId() {
			return cardId;
		}
		public void setCardId(Integer cardId) {
			this.cardId = cardId;
		}
		public Integer getToFoundationId() {
			return toFoundationId;
		}
		public void setToFoundationId(Integer toFoundationId) {
			this.toFoundationId = toFoundationId;
		}
		
		
	}

}
