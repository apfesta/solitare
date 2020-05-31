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
	
	public void notifyPlayerStatus(GameBoard game, User user, boolean isReady) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new BasePayload(isReady?GameUpdateAction.PLAYER_READY:GameUpdateAction.PLAYER_NOT_READY, user));
	}
	
	public void notifyCountdown(GameBoard game, User user, boolean isCountdown) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new BasePayload(isCountdown?GameUpdateAction.COUNTDOWN_STARTED:GameUpdateAction.COUNTDOWN_CANCELLED, user));
	}
	
	public void notifyPlayerDrop(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new BasePayload(GameUpdateAction.PLAYER_DROP, user));
	}
	
	public void notifyMoveToFoundation(GameBoard game, User user,
			Integer cardId,
			Integer toFoundationId) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.MOVE_TO_FOUNDATION, user, game, 
						cardId, toFoundationId, null));
	}
	
	public void notifyMoveToTableau(GameBoard game, User user,
			Integer cardId,
			Integer toBuildId) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.MOVE_TO_TABLEAU, user, game, 
						cardId, null, toBuildId));
	}
	
	enum GameUpdateAction {
		PLAYER_JOIN,
		PLAYER_DROP,
		PLAYER_READY,
		PLAYER_NOT_READY,
		COUNTDOWN_STARTED,
		COUNTDOWN_CANCELLED,
		MOVE_TO_FOUNDATION,
		MOVE_TO_TABLEAU
	}

	static class BasePayload {
		final GameUpdateAction action;
		final User user;
		
		public BasePayload(GameUpdateAction action, User user) {
			super();
			this.action = action;
			this.user = user;
		}
		public GameUpdateAction getAction() {
			return action;
		}
		public User getUser() {
			return user;
		}
	}
		
	static class GameUpdate extends BasePayload {
		final Foundation foundation;
		Integer cardId;
		Integer toFoundationId;
		Integer toBuildId;
		Integer numOfUsers;
		
		public GameUpdate(GameUpdateAction action, User user, GameBoard game) {
			super(action, user);
			this.foundation = game.getFoundation();
			this.numOfUsers = game.getUsers().size();
		}
		public GameUpdate(GameUpdateAction action, User user, GameBoard game, 
				Integer cardId, Integer toFoundationId, Integer toBuildId) {
			this(action, user, game);
			this.cardId = cardId;
			this.toFoundationId = toFoundationId;
			this.toBuildId = toBuildId;
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
		public Integer getNumOfUsers() {
			return numOfUsers;
		}
		public void setNumOfUsers(Integer numOfUsers) {
			this.numOfUsers = numOfUsers;
		}
		
		
	}

}
