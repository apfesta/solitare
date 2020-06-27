package com.andrewfesta.doublesolitare.service.impl;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.andrewfesta.doublesolitare.model.Foundation;
import com.andrewfesta.doublesolitare.model.GameBoard;
import com.andrewfesta.doublesolitare.model.User;
import com.andrewfesta.doublesolitare.model.UserBoard.Score;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
		
		simpMessageSending.convertAndSend("/topic/games/activity", 
				new AppUpdate(GameUpdateAction.GAME_RENAME, 
						user, game.getGameId(), game.getGameName(), game.getUsers()));
	}
	
	public void notifyPlayerStatus(GameBoard game, User user, boolean isReady) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new BasePayload(isReady?GameUpdateAction.PLAYER_READY:GameUpdateAction.PLAYER_NOT_READY, 
						user));
	}
	
	public void notifyCountdown(GameBoard game, User user, boolean isCountdown) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new BasePayload(isCountdown?GameUpdateAction.COUNTDOWN_STARTED:GameUpdateAction.COUNTDOWN_CANCELLED, 
						user));
	}
	
	public void notifyPlayerDrop(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new BasePayload(GameUpdateAction.PLAYER_DROP, user));
		
		simpMessageSending.convertAndSend("/topic/games/activity", 
				new AppUpdate(GameUpdateAction.GAME_RENAME, 
						user, game.getGameId(), game.getGameName(), game.getUsers()));
	}
	
	public void notifyPlayerRename(Collection<GameBoard> games, User user) {
		for (GameBoard game: games) {
			simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
					new BasePayload(GameUpdateAction.PLAYER_RENAME, 
							user));
		}
		
		simpMessageSending.convertAndSend("/topic/games/activity", 
				new BasePayload(GameUpdateAction.PLAYER_RENAME, user));
	}
	
	public void notifyGameRename(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameNameUpdate(GameUpdateAction.GAME_RENAME, 
						user, game));
		
		simpMessageSending.convertAndSend("/topic/games/activity", 
				new AppUpdate(GameUpdateAction.GAME_RENAME, 
						user, game.getGameId(), game.getGameName(), game.getUsers()));
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
	
	public void notifyDiscard(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.DISCARD, user, game, 
						null, null, null));
	}
	
	public void notifyBlocked(GameBoard game, User user, boolean blocked) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(blocked?GameUpdateAction.PLAY_IS_BLOCKED:GameUpdateAction.PLAY_NOT_BLOCKED, 
						user, game, null, null, null));
	}
	
	public void notifyPlayerSleep(GameBoard game, User user, boolean sleep) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(sleep?GameUpdateAction.PLAYER_SLEEP:GameUpdateAction.PLAYER_AWAKE,
						user, game, null, null, null));
	}
	
	public void notifyGameWon(GameBoard game, User user) {
		simpMessageSending.convertAndSend("/topic/game/" + game.getGameId() + "/activity", 
				new GameUpdate(GameUpdateAction.GAME_WON, 
						user, game, null, null, null));
	}
	
	enum GameUpdateAction {
		PLAYER_JOIN,
		PLAYER_DROP,
		PLAYER_SLEEP,
		PLAYER_AWAKE,
		PLAYER_READY,
		PLAYER_NOT_READY,
		PLAYER_RENAME,
		COUNTDOWN_STARTED,
		COUNTDOWN_CANCELLED,
		MOVE_TO_FOUNDATION,
		MOVE_TO_TABLEAU,
		DISCARD,
		PLAY_IS_BLOCKED,
		PLAY_NOT_BLOCKED,
		GAME_WON,
		GAME_RENAME,
		PUBLIC_GAME_AVAILABLE
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
	
	static class AppUpdate extends BasePayload {
		
		Integer gameId;
		String gameName;
		Collection<User> users;
		User startedBy;
		
		public AppUpdate(GameUpdateAction action, User user, 
				Integer gameId, String gameName, Collection<User> users) {
			super(action, user);
			this.gameId = gameId;
			this.gameName = gameName;
			this.users = users;
		}
	}
	
	static class GameNameUpdate extends BasePayload {
		final String gameName; 
		
		public GameNameUpdate(GameUpdateAction action, User user, GameBoard game) {
			super(action, user);
			this.gameName = game.getGameName();
		}

		public String getGameName() {
			return gameName;
		}

	}
		
	@JsonInclude(Include.NON_NULL)
	static class GameUpdate extends BasePayload {
		final Foundation foundation;
		Integer cardId;
		Integer toFoundationId;
		Integer toBuildId;
		final Integer numOfUsers;
		final Map<Integer, Score> score;
		Boolean gameWon;
		
		public GameUpdate(GameUpdateAction action, User user, GameBoard game) {
			super(action, user);
			this.foundation = game.getFoundation();
			this.numOfUsers = game.getUsers().size();
			this.score = game.getUserScores();
			if (game.getUserBoard(user).isGameWon() || game.getDebugProperties().isAdditionalResponseOutput()) {
				gameWon = game.getUserBoard(user).isGameWon();
			}
		}
		public GameUpdate(GameUpdateAction action, User user, GameBoard game, 
				Integer cardId, Integer toFoundationId, Integer toBuildId) {
			this(action, user, game);
			this.cardId = cardId;
			this.toFoundationId = toFoundationId;
			this.toBuildId = game.getDebugProperties().isAdditionalResponseOutput()?toBuildId:null;
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
		public Map<Integer, Score> getScore() {
			return score;
		}
		public Boolean getGameWon() {
			return gameWon;
		}
		public void setGameWon(Boolean gameWon) {
			this.gameWon = gameWon;
		}
		public Integer getToBuildId() {
			return toBuildId;
		}
		public void setToBuildId(Integer toBuildId) {
			this.toBuildId = toBuildId;
		}
		
		
	}

}
