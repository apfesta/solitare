package com.andrewfesta.doublesolitare.model;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A user's game board.  Contains their tableau, stock and waste piles.
 * 
 * 
 * @author apfesta
 *
 */
public class UserBoard {
	
	private static final Logger GAME_LOG = LoggerFactory.getLogger("GameLog");
	
	final User user;
	final GameBoard game;
	Tableau tableau;
	Pile stockPile;
	VisiblePile discardPile;
	boolean gameWon = false;
	Score score;
	boolean userReady;
	Instant lastMoveInstant;
	
	boolean shuffle = true; //shuffle by default.  Tests should use false to have a predictable set
	
	Map<Integer, Card> cards = new HashMap<>(); //card by ID
	
	ExportBuilder export;
	
	public UserBoard(GameBoard game, User user) {
		super();
		this.game = game;
		this.user = user;
		this.discardPile = new VisiblePile();
		this.discardPile.setToStringPrefix("D");
	}
	
	/**
	 * For testing only.  Allows us to set up a game board with a set card sequence
	 * 
	 * @param stackedDeck
	 */
	public void setup(Card[] stackedDeck) {
		Deck d = Deck.getStackedDeck(stackedDeck);
		GAME_LOG.debug("GameId:({}){} User:({}){} deck:{}",
				game.gameId, game.gameName, 
				user.id, user.username, d);
		export = new ExportBuilder(this)
				.startingDeck(d.exportState());

		for (Card c: d.cards) {
			cards.put(c.getUnicodeInt(), c);
		}
		
		tableau = new Tableau();
		stockPile = tableau.setup(d);
		score = new Score();
	}
	
	public void setup() {
		Deck d = Deck.getInstance();
		if (shuffle) {
			d.shuffle();
		}
		GAME_LOG.debug("GameId:({}){} User:({}){} deck:{}",
				game.gameId, game.gameName, 
				user.id, user.username, d);
		export = new ExportBuilder(this)
				.startingDeck(d.exportState());
		for (Card c: d.cards) {
			cards.put(c.getUnicodeInt(), c);
		}
		
		tableau = new Tableau();
		stockPile = tableau.setup(d);
		score = new Score();
	}
	
	public Card lookupCard(Integer cardId) {
		return cards.get(cardId);
	}
	
	public CanPush canPush(Card card) {
		
		CanPush canPush = new CanPush(card, game.userBoards.size());
		
		Pile[] facedown = getTableau().getPile();
		Build[] tableauBuild = getTableau().getBuild();
		
		boolean isFacedown = false;
		for (Pile pile: facedown) {
			if (pile.contains(card)) {
				isFacedown = true;
			}
		}
		for (int i=0; i<tableauBuild.length; i++) {
			canPush.tableauBuild[i] = !isFacedown && tableauBuild[i].canPush(card);
		}
		List<Build> foundationBuld = game.getFoundation().getPile();
		for (int i=0; i<foundationBuld.size(); i++) {
			canPush.foundationPile[i] = !isFacedown && foundationBuld.get(i).canPush(card);
		}

		return canPush;
	}
	
	public void discard(int maxNumberOfCards) {
		if (stockPile.isEmpty() && !discardPile.isEmpty()) {
			GAME_LOG.debug("GameId:({}){} User:({}){} putting discard pile back into stock pile",
					game.gameId, game.gameName, 
					user.id, user.username);
			score.deckPassthrough++;
			do {
				Card c = discardPile.pop();
				stockPile.push(c);
				c.setCurrentPile(stockPile);
			} while (!discardPile.isEmpty());
		} 
		if (stockPile.isEmpty()) {
			GAME_LOG.debug("GameId:({}){} User:({}){} no cards left to discard",
					game.gameId, game.gameName, 
					user.id, user.username);
			return;
		}
		GAME_LOG.debug("GameId:({}){} User:({}){} discard",
				game.gameId, game.gameName, 
				user.id, user.username);
		for (int i = 0; i<maxNumberOfCards; i++) {
			if (stockPile.isEmpty()) {
				break;
			}
			Card c = stockPile.pop();
			discardPile.push(c);
			c.setCurrentPile(discardPile);
		}
		score.discard++;
		lastMoveInstant = Instant.now();
	}
	
	public void prettyPrintStockAndDiscardPiles(int maxNumberOfCards) {
		getStockPile().print();
		getDiscardPile().print(maxNumberOfCards);
	}
	
	protected Integer getPileIdToFlip(Card card) {
		Integer pileIdToFlip = null;
		for (int i=0; i<getTableau().getPile().length; i++) {
			if (card.getCurrentBuild()==getTableau().getBuild()[i]) {
				pileIdToFlip = i;
			}
		}
		return pileIdToFlip;
	}
	
	public void moveToFoundation(Integer cardId, Integer toFoundationId) {
		
		Lock lock = game.getFoundation().getLocks().get(toFoundationId);
		lock.lock();
		
		try {
			Card card = lookupCard(cardId);
			Integer pileIdToFlip = getPileIdToFlip(card);
			
			GAME_LOG.debug("GameId:({}){} User:({}){} Move {} to foundation pile {}",
					game.gameId, game.gameName, 
					user.id, user.username,
					card.abbrev(), toFoundationId);
			game.getFoundation().getPile().get(toFoundationId).push(card);
			score.toFoundation++;
			lastMoveInstant = Instant.now();
			
			if (pileIdToFlip!=null && 
					!getTableau().getPile()[pileIdToFlip].isEmpty() &&
					getTableau().getBuild()[pileIdToFlip].isEmpty()) {
				Card revealed = getTableau().flipTopPileCard(pileIdToFlip);
				score.tableauFlip++;
				GAME_LOG.debug("GameId:({}){} User:({}){} Flip pile {} reveals {}",
						game.gameId, game.gameName, 
						user.id, user.username, 
						pileIdToFlip, revealed.abbrev());
			}
			
			boolean gameWon = true;
			if (card.getValue() == Card.KING) {
				//Check tableau and stock pile to see if the game has been won
				for (Build b: getTableau().getBuild()) {
					if (!b.isEmpty()) {
						gameWon = false;
					}
				}
				if (gameWon && getStockPile().isEmpty() && getDiscardPile().isEmpty()) {
					setGameWon(true);
					game.setGameOver(true);
					game.setEndTime(Instant.now());
					game.setInProgress(false);
					GAME_LOG.debug("GameId:({}){} User:({}){} has won! Duration:{}", 
							game.gameId, game.gameName, 
							user.id, user.username,
							Duration.between(game.startTime, game.endTime));
				}
			}
		} finally {
			lock.unlock();
		}
		
	}
	
	/**
	 * Move card from discard pile or another tableau build.
	 * 
	 * @param cardId
	 * @param toBuildId
	 */
	public void moveToTableau(Integer cardId, Integer toBuildId) {
		
		Card card = lookupCard(cardId);
		Integer pileIdToFlip = getPileIdToFlip(card);
		
		if (card.getCurrentBuild()!=null && !card.equals(card.getCurrentBuild().peek())) {
			//Move Build of cards from pile to pile
			GAME_LOG.debug("GameId:({}){} User:({}){} Move build ({}-{}) to tableau pile {}",
					game.gameId, game.gameName, 
					user.id, user.username, 
					card.abbrev(), card.getCurrentBuild().peek().abbrev(), toBuildId);
			getTableau().getBuild()[toBuildId].push(card.getCurrentBuild(), card);
			score.tableauToTableau++;
		} else {
			//Move card from pile or discard pile to tableau pile
			GAME_LOG.debug("GameId:({}){} User:({}){} Move {} to tableau pile {}",
					game.gameId, game.gameName, 
					user.id, user.username,
					card.abbrev(), toBuildId);
			getTableau().getBuild()[toBuildId].push(card);
			score.discardToTableau++;
		}
		lastMoveInstant = Instant.now();
		
		if (pileIdToFlip!=null && 
				!getTableau().getPile()[pileIdToFlip].isEmpty() &&
				getTableau().getBuild()[pileIdToFlip].isEmpty()) {
			Card revealed = getTableau().flipTopPileCard(pileIdToFlip);
			score.tableauFlip++;
			GAME_LOG.debug("GameId:({}){} User:({}){} Flip pile {} reveals {}",
					game.gameId, game.gameName, 
					user.id, user.username, 
					pileIdToFlip, revealed.abbrev());
		}
	}
	
	@JsonIgnore
	public User getUser() {
		return user;
	}

	public GameBoard getGame() {
		return game;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public Tableau getTableau() {
		return tableau;
	}

	public void setTableau(Tableau tableau) {
		this.tableau = tableau;
	}

	public Pile getStockPile() {
		return stockPile;
	}

	public void setStockPile(Pile stockPile) {
		this.stockPile = stockPile;
	}

	public VisiblePile getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(VisiblePile discardPile) {
		this.discardPile = discardPile;
	}
	
	public boolean isGameWon() {
		return gameWon;
	}

	public void setGameWon(boolean gameWon) {
		this.gameWon = gameWon;
	}
	
	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public boolean isUserReady() {
		return userReady;
	}

	public void setUserReady(boolean userReady) {
		this.userReady = userReady;
	}

	public Instant getLastMoveInstant() {
		return lastMoveInstant;
	}

	public void setLastMoveInstant(Instant lastMoveInstant) {
		this.lastMoveInstant = lastMoveInstant;
	}
	
	public static Builder builder(GameBoard game, User user) {
		return new Builder(game, user);
	}
	
	public static class Builder {
		UserBoard userBoard;
		
		Builder(GameBoard game, User user) {
			userBoard = new UserBoard(game, user);
			userBoard.score = userBoard.new Score();
			userBoard.export = new ExportBuilder(userBoard);
			game.userBoards.put(user, userBoard);
		}
		
		public Builder tableau(Tableau tableau) {
			userBoard.tableau = tableau;
			return this;
		}
		public Tableau.Builder tableau() {
			return Tableau.builder().userBoardBuilder(this);
		}
		public Pile.Builder<Builder> stockPile() {
			return Pile.builder(this, true, new Pile())
					.toStringPrefix("S")
					.cards(userBoard.cards)
					.consumer((pile)->userBoard.stockPile=pile);
		}
		
		public UserBoard build() {
			return userBoard;
		}
		
	}

	@JsonInclude(Include.NON_NULL)
	public class CanPush {
		private Card card;
		private Boolean[] foundationPile;
		private Boolean[] tableauBuild = new Boolean[7];
		
		public CanPush(Card card, int numOfPlayers) {
			super();
			this.card = game.getDebugProperties().isAdditionalResponseOutput()?card:null;
			this.foundationPile = new Boolean[4*numOfPlayers];
		}
		public Card getCard() {
			return card;
		}
		public Boolean[] getFoundationPile() {
			return foundationPile;
		}
		
		public Boolean[] getTableauBuild() {
			return tableauBuild;
		}
		public void setTableauBuild(Boolean[] tableauBuild) {
			this.tableauBuild = tableauBuild;
		}
	}
	
	@JsonInclude(Include.NON_NULL)
	public class Score {
		int toFoundation = 0;
		int discardToTableau = 0;
		int tableauToTableau = 0;
		int tableauFlip = 0;
		int discard = 0;
		int deckPassthrough = 0;
		boolean additionalResponseOutput;
		
		Score() {
			additionalResponseOutput = game.getDebugProperties().isAdditionalResponseOutput();
		}
		public int getToFoundation() {
			return toFoundation;
		}
		public Integer getDiscardToTableau() {
			return additionalResponseOutput?discardToTableau:null;
		}
		public Integer getTableauFlip() {
			return additionalResponseOutput?tableauFlip:null;
		}
		public Integer getDiscard() {
			return additionalResponseOutput?discard:null;
		}
		public Integer getDeckPassthrough() {
			return additionalResponseOutput?deckPassthrough:null;
		}
		public int getTotalScore() {
			return (toFoundation*10)+
					(discardToTableau*5)+
//					(tableauToTableau*3)+
					(tableauFlip*5)+
					(deckPassthrough*-20);
		}
		public int getTotalMoves() {
			return toFoundation+discardToTableau+tableauToTableau+discard;
		}
		
		public void prettyPrint() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Moves: "+getTotalMoves())
				.append(" Score: "+getTotalScore());
			System.out.println(buffer.toString());
		}
	}
	
	public static class Export {
		Deck.Export startingDeck;
		UserBoard userBoard;
		String[] currentFoundation;
		String[] currentTableau;
		String currentStockPile;
		String currentDiscardPile;

		public Export() {
		}
		public Export(UserBoard userBoard) {
			this.userBoard = userBoard;
		}

		public Deck.Export getStartingDeck() {
			return startingDeck;
		}

		public void setStartingDeck(Deck.Export startingDeck) {
			this.startingDeck = startingDeck;
		}
		public String[] getCurrentFoundation() {
			return currentFoundation;
		}
		public void setCurrentFoundation(String[] currentFoundation) {
			this.currentFoundation = currentFoundation;
		}
		public String[] getCurrentTableau() {
			return currentTableau;
		}
		public void setCurrentTableau(String[] currentTableau) {
			this.currentTableau = currentTableau;
		}
		public String getCurrentStockPile() {
			return currentStockPile;
		}
		public void setCurrentStockPile(String currentStockPile) {
			this.currentStockPile = currentStockPile;
		}
		public String getCurrentDiscardPile() {
			return currentDiscardPile;
		}
		public void setCurrentDiscardPile(String currentDiscardPile) {
			this.currentDiscardPile = currentDiscardPile;
		}
	}
	
	public static class ExportBuilder {
		Export export;
		
		ExportBuilder(UserBoard userBoard) {
			export = new Export(userBoard);
		}
		ExportBuilder startingDeck(Deck.Export startingDeck) {
			export.setStartingDeck(startingDeck);
			return this;
		}
		Export build() {
			export.currentFoundation = export.userBoard.game.getFoundation().getPrettyPrint().split("\n");
			export.currentTableau = export.userBoard.tableau.getPrettyPrint().split("\n");
			export.currentStockPile = export.userBoard.stockPile.toString();
			export.currentDiscardPile = export.userBoard.discardPile.toString();
			return export;
		}
	}

}
