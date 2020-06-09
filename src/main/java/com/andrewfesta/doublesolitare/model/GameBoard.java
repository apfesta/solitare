package com.andrewfesta.doublesolitare.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class GameBoard {
	
	private static final Logger GAME_LOG = LoggerFactory.getLogger("GameLog");

	final Integer gameId;
	final User createdBy;
	Foundation foundation;
	final boolean multiPlayer;
	boolean inProgress = false;
	boolean gameOver = false;
	
	boolean shuffle = true; //shuffle by default.  Tests should use false to have a predictable set
	int maxNumberOfCards = 3; //cards to discard
	
	Map<User, UserBoard> userBoards = new HashMap<>();
	Map<Integer, UserBoard.Score> userScores = new HashMap<>();
	
	public GameBoard(User createdBy, Integer gameId, boolean multiPlayer) {
		super();
		this.createdBy = createdBy;
		this.gameId = gameId;
		this.multiPlayer = multiPlayer;
	}

	/**
	 * For testing only.  Allows us to set up a game board with a set card sequence
	 * 
	 * @param stackedDeck
	 */
	public void setup(User user, Card[] stackedDeck) {
		foundation = new Foundation();
		
		join(user, stackedDeck);
			
		foundation.prettyPrint();
		userBoards.get(user).getTableau().prettyPrint();
	}
	
	public void setup(User user) {
		foundation = new Foundation();
		
		join(user);
		
		foundation.prettyPrint();
		userBoards.get(user).getTableau().prettyPrint();
	}
	
	public void join(User user, Card[] stackedDeck) {
		if (userBoards.size() >= 1 && !this.multiPlayer) {
			throw new RuntimeException("Cannot join single player game.");
		}
		UserBoard userBoard = new UserBoard(this, user);
		userBoard.setup(stackedDeck);
		userBoards.put(user, userBoard);
				
		foundation.addPlayer();
		
		foundation.prettyPrint();
		userBoard.getTableau().prettyPrint();
	}
	
	public void join(User user) {
		if (userBoards.size() >= 1 && !this.multiPlayer) {
			throw new RuntimeException("Cannot join single player game.");
		}
		UserBoard userBoard = new UserBoard(this, user);
		userBoard.setShuffle(shuffle);
		userBoard.setup();
		userBoards.put(user, userBoard);
				
		foundation.addPlayer();
		
		foundation.prettyPrint();
		userBoard.getTableau().prettyPrint();
		
		GAME_LOG.info("GameId:{} User:{} joined game", gameId, user);
	}
	
	public void leave(User user) {
		userBoards.remove(user);
		if (inProgress) {
			inProgress = false;
			gameOver = true;
		}
	}
	
	public Card lookupCard(User user, Integer cardId) {
		return userBoards.get(user).lookupCard(cardId);
	}
	
	public UserBoard.CanPush canPush(User user, Card card) {
		return userBoards.get(user).canPush(card);
	}
	
	public void discard(User user) {
		userBoards.get(user).discard(maxNumberOfCards);
	}
	
//	protected Integer getPileIdToFlip(Card card) {
//		Integer pileIdToFlip = null;
//		for (int i=0; i<getTableau().getPile().length; i++) {
//			if (card.getCurrentBuild()==getTableau().getBuild()[i]) {
//				pileIdToFlip = i;
//			}
//		}
//		return pileIdToFlip;
//	}
	
	public void moveToFoundation(User user, Integer cardId, Integer toFoundationId) {
		userBoards.get(user).moveToFoundation(cardId, toFoundationId);
	}
	
	/**
	 * Move card from discard pile or another tableau build.
	 * 
	 * @param cardId
	 * @param toBuildId
	 */
	public void moveToTableau(User user, Integer cardId, Integer toBuildId) {
		userBoards.get(user).moveToTableau(cardId, toBuildId);
	}
	
	public boolean isMultiPlayer() {
		return multiPlayer;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public boolean isInProgress() {
		return inProgress;
	}
	
	public boolean isReady() {
		for (UserBoard userBoard: userBoards.values()) {
			if (!userBoard.isUserReady()) {
				return false;
			}
		}
		return true;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	@JsonIgnore
	public User getCreatedBy() {
		return createdBy;
	}

	public Integer getGameId() {
		return gameId;
	}

	public Collection<User> getUsers() {
		return userBoards.keySet();
	}
	
	public UserBoard getUserBoard(User user) {
		return userBoards.get(user);
	}
	
	public Map<Integer, UserBoard.Score> getUserScores() {
		return userBoards.entrySet().stream()
				.collect(Collectors.toMap(
						(e)->e.getKey().getId(), (e)->e.getValue().getScore()));
	}

	public Tableau getTableau(User user) {
		return userBoards.get(user).tableau;
	}

	public Pile getStockPile(User user) {
		return userBoards.get(user).stockPile;
	}

	public Pile getDiscardPile(User user) {
		return userBoards.get(user).discardPile;
	}

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameWon) {
		this.gameOver = gameWon;
	}
	
}
