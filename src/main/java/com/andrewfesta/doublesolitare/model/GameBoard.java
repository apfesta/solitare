package com.andrewfesta.doublesolitare.model;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.andrewfesta.doublesolitare.DoubleSolitareConfig.DoubleSolitareDebugProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class GameBoard {
	
	private static final Logger GAME_LOG = LoggerFactory.getLogger("GameLog");

	final Integer gameId;
	final User createdBy;
	final ZonedDateTime createdOn;
	Instant startTime;
	Instant endTime;
	String gameName;
	Foundation foundation;
	final boolean multiPlayer;
	boolean inProgress = false;
	boolean gameOver = false;
	boolean testMode;
	
	int maxNumberOfCards = 3; //cards to discard
	
	Map<User, UserBoard> userBoards = new HashMap<>();
	Set<Integer> blocked = new HashSet<>();
	
	DoubleSolitareDebugProperties debugProperties = new DoubleSolitareDebugProperties();
	
	public static final Card[] TEST_DECK = new Card[] {
			//STOCK PILE (in reverse order)
			new Card(Card.KING, Suit.HEARTS),
			new Card(Card.KING, Suit.CLUBS),
			new Card(Card.KING, Suit.DIAMONDS),
			
			new Card(Card.QUEEN, Suit.CLUBS),
			new Card(Card.QUEEN, Suit.DIAMONDS),
			new Card(Card.KING, Suit.SPADES),
			
			new Card(Card.JACK, Suit.DIAMONDS),
			new Card(Card.QUEEN, Suit.SPADES),
			new Card(Card.QUEEN, Suit.HEARTS),
			
			new Card(Card.JACK, Suit.SPADES),
			new Card(Card.JACK, Suit.HEARTS),
			new Card(Card.JACK, Suit.CLUBS),
			
			new Card(10, Suit.HEARTS),
			new Card(10, Suit.CLUBS),
			new Card(10, Suit.DIAMONDS),
			
			new Card(9, Suit.CLUBS),
			new Card(9, Suit.DIAMONDS),
			new Card(10, Suit.SPADES),
			
			new Card(8, Suit.DIAMONDS),
			new Card(9, Suit.SPADES),
			new Card(9, Suit.HEARTS),
			
			new Card(8, Suit.SPADES),
			new Card(8, Suit.HEARTS),
			new Card(8, Suit.CLUBS),
			
			//TABLEAU (in reverse order)
			new Card(6, Suit.HEARTS),
			
			new Card(6, Suit.CLUBS),
			new Card(4, Suit.DIAMONDS),
			
			new Card(6, Suit.DIAMONDS),
			new Card(5, Suit.SPADES),
			new Card(3, Suit.CLUBS),
			
			new Card(7, Suit.SPADES),
			new Card(5, Suit.HEARTS),
			new Card(3, Suit.DIAMONDS),
			new Card(2, Suit.CLUBS),
			
			new Card(7, Suit.HEARTS),
			new Card(5, Suit.CLUBS),
			new Card(4, Suit.SPADES),
			new Card(2, Suit.DIAMONDS),
			new Card(Card.ACE, Suit.CLUBS),
			
			new Card(7, Suit.CLUBS),
			new Card(5, Suit.DIAMONDS),
			new Card(4, Suit.HEARTS),
			new Card(3, Suit.SPADES),
			new Card(2, Suit.SPADES),
			new Card(Card.ACE, Suit.HEARTS),
			
			new Card(7, Suit.DIAMONDS),
			new Card(6, Suit.SPADES),
			new Card(4, Suit.CLUBS),
			new Card(3, Suit.HEARTS),
			new Card(2, Suit.HEARTS),
			new Card(Card.ACE, Suit.DIAMONDS),
			new Card(Card.ACE, Suit.SPADES),
	};
	
	public GameBoard(User createdBy, Integer gameId, boolean multiPlayer) {
		super();
		this.createdBy = createdBy;
		this.createdOn = ZonedDateTime.now();
		this.gameId = gameId;
		this.gameName = "Game "+gameId;
		this.multiPlayer = multiPlayer;
		if (!multiPlayer) {
			this.startTime = Instant.now();
		}
	}
	
	public GameBoard(User createdBy, Integer gameId, boolean multiPlayer, 
			boolean demoMode) {
		this(createdBy, gameId, multiPlayer);
		this.testMode = demoMode;
	}
	
	/**
	 * For testing only.  Allows us to set up a game board with a set card sequence
	 * 
	 * @param stackedDeck
	 */
	public void setupTest(User user) {
		//We need to copy since the Card object holds state
		Card[] testDeck = new Card[52];
		for (int i =0; i<TEST_DECK.length; i++) {
			testDeck[i] = new Card(TEST_DECK[i]);
		}
		
		setup(user, testDeck);
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
	
	/**
	 * For testing only.  Allows us to set up a game board with a set card sequence
	 * 
	 * @param stackedDeck
	 */
	public void joinTest(User user) {
		//We need to copy since the Card object holds state
		Card[] testDeck = new Card[52];
		for (int i =0; i<TEST_DECK.length; i++) {
			testDeck[i] = new Card(TEST_DECK[i]);
		}
		
		join(user, testDeck);
	}
	
	/**
	 * For testing only.  Allows us to set up a game board with a set card sequence
	 * 
	 * @param stackedDeck
	 */
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
		if (!userBoards.containsKey(user)) {
			UserBoard userBoard = new UserBoard(this, user);
			userBoard.setShuffle(debugProperties.isShuffle());
			userBoard.setup();
			userBoards.put(user, userBoard);
					
			foundation.addPlayer();
			
			foundation.prettyPrint();
			userBoard.getTableau().prettyPrint();
			
			GAME_LOG.info("GameId:({}){} User:({}){} joined game", 
					gameId, gameName, 
					user.id, user.username);
		} else {
			GAME_LOG.info("GameId:({}){} User:({}){} re-joined game", 
					gameId, gameName, 
					user.id, user.username);
		}
	}
		
	public void leave(User user) {
		if (inProgress) {
			endTime = Instant.now();
			inProgress = false;
			gameOver = true;
		} else if (!inProgress && !gameOver) {
			//Game hasn't started yet.  Remove them.
			userBoards.remove(user);
		}
	}
	
	public void start() {
		GAME_LOG.debug("GameId:({}){} has started", 
				gameId, gameName);
		startTime = Instant.now();
		inProgress = true;
	}
	
	/**
	 * Ends the game.  
	 * @return winning UserBoard
	 */
	public UserBoard end(User user) {
		GAME_LOG.debug("GameId:({}){} User:({}){} has chosen to end the game!", 
				gameId, gameName, 
				user.id, user.username);
		
		endTime = Instant.now();
		
		UserBoard winner = userBoards.values().stream()
			//Winner is highest Total Score
			.max((a, b) -> Integer.compare(a.score.getTotalScore(),b.score.getTotalScore()))
			.map((userBoard)->{
				userBoard.gameWon=true;
				return userBoard;
				})
			.orElse(userBoards.values().stream()
					//Or fewest moves
					.min((a, b) -> Integer.compare(a.score.getTotalMoves(),b.score.getTotalMoves()))
					.orElse(
							//Or just fall back to the first player in the HashMap
							userBoards.values().iterator().next()));
		inProgress = false;
		gameOver = true;
		GAME_LOG.debug("GameId:({}){} User:({}){} has won! Duration:{}", 
				gameId, gameName, 
				winner.user.id, winner.user.username, Duration.between(startTime, endTime));
		return winner;
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
	
	public boolean isHost() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return createdBy.equalsPrincipal(
				(UserDetails) principal);
	}
		
	public void userBlocked(User user, boolean blocked) {
		if (blocked) {
			this.blocked.add(user.getId());
		} else {
			this.blocked.remove(user.getId());
		}
	}
	
	public boolean isUserBlocked(User user) {
		return this.blocked.contains(user.getId());
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
		if (userBoards.size()>1) {
			for (UserBoard userBoard: userBoards.values()) {
				if (!userBoard.isUserReady()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public Instant getLastMoveTimestamp() {
		return userBoards.values().stream()
			.max((a, b) -> (a.getLastMoveInstant() != null ? a.getLastMoveInstant() : createdOn.toInstant())
					.compareTo((b.getLastMoveInstant() != null ? b.getLastMoveInstant() : createdOn.toInstant())))
			.map((userBoard)->userBoard.getLastMoveInstant())
			.orElse(createdOn.toInstant());
	}
	
	public boolean isExpired() {
		return Duration.between(getLastMoveTimestamp(),Instant.now())
				.toHours() >= 1;
	}

	@JsonIgnore
	public User getCreatedBy() {
		return createdBy;
	}

	@JsonIgnore
	public ZonedDateTime getCreatedOn() {
		return createdOn;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public Integer getGameId() {
		return gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Collection<User> getUsers() {
		return userBoards.keySet();
	}
	
	public UserBoard getUserBoard(User user) {
		return userBoards.get(user);
	}
	
	public Map<Integer, Boolean> getUserReady() {
		return userBoards.entrySet().stream()
			.collect(Collectors.toMap(
					(e)->e.getKey().getId(), (e)->e.getValue().isUserReady()));
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

	public DoubleSolitareDebugProperties getDebugProperties() {
		return debugProperties;
	}

	public void setDebugProperties(DoubleSolitareDebugProperties debugProperties) {
		this.debugProperties = debugProperties;
	}
	
}
