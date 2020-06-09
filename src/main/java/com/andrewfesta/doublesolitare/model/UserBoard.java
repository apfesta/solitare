package com.andrewfesta.doublesolitare.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	VisiblePile discardPile = new VisiblePile();
	boolean gameWon = false;
	Score score = new Score();
	boolean userReady;
	
	boolean shuffle = true; //shuffle by default.  Tests should use false to have a predictable set
	
	private Map<Integer, Card> cards = new HashMap<>(); //card by ID
	
	public UserBoard(GameBoard game, User user) {
		super();
		this.game = game;
		this.user = user;
	}
	
	/**
	 * For testing only.  Allows us to set up a game board with a set card sequence
	 * 
	 * @param stackedDeck
	 */
	public void setup(Card[] stackedDeck) {
		Deck d = Deck.getStackedDeck(stackedDeck);
		for (Card c: d.cards) {
			cards.put(c.getUnicodeInt(), c);
		}
		
		tableau = new Tableau();
		stockPile = tableau.setup(d);
	}
	
	public void setup() {
		Deck d = Deck.getInstance();
		if (shuffle) {
			d.shuffle();
		}
		for (Card c: d.cards) {
			cards.put(c.getUnicodeInt(), c);
		}
		
		tableau = new Tableau();
		stockPile = tableau.setup(d);
	}
	
	public Card lookupCard(Integer cardId) {
		return cards.get(cardId);
	}
	
	public CanPush canPush(Card card) {
		
		CanPush canPush = new CanPush(game.userBoards.size());
		
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
		GAME_LOG.debug("GameId:{} User:{} discard",
				game.gameId, user);
		if (stockPile.isEmpty()) {
			score.deckPassthrough++;
			do {
				Card c = discardPile.pop();
				stockPile.push(c);
				c.setCurrentPile(stockPile);
			} while (!discardPile.isEmpty());
		}
		for (int i = 0; i<maxNumberOfCards; i++) {
			if (stockPile.isEmpty()) {
				break;
			}
			Card c = stockPile.pop();
			discardPile.push(c);
			c.setCurrentPile(discardPile);
		}
		score.discard++;
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
		
		Card card = lookupCard(cardId);
		Integer pileIdToFlip = getPileIdToFlip(card);
		
		GAME_LOG.debug("GameId:{} User:{} Move {} to foundation pile {}",
				game.gameId, user, card.abbrev(), toFoundationId);
		game.getFoundation().getPile().get(toFoundationId).push(card);
		score.toFoundation++;
		
		if (pileIdToFlip!=null && !getTableau().getPile()[pileIdToFlip].isEmpty()) {
			getTableau().flipTopPileCard(pileIdToFlip);
			score.tableauFlip++;
			GAME_LOG.debug("GameId:{} User:{} Flip pile {} reveals {}",
					game.gameId, user, pileIdToFlip, card.abbrev());
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
				GAME_LOG.debug("GameId:{} User:{} has won!", game.gameId);
			}
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
			GAME_LOG.debug("GameId:{} User:{} Move build ({}-{}) to tableau pile {}",
					game.gameId, user, card.abbrev(), card.getCurrentBuild().peek().abbrev(), toBuildId);
			getTableau().getBuild()[toBuildId].push(card.getCurrentBuild(), card);
		} else {
			//Move card from pile or discard pile to tableau pile
			GAME_LOG.debug("GameId:{} User:{} Move {} to tableau pile {}",
					game.gameId, user, card.abbrev(), toBuildId);
			getTableau().getBuild()[toBuildId].push(card);
			score.discardToTableau++;
		}
		
		if (pileIdToFlip!=null && !getTableau().getPile()[pileIdToFlip].isEmpty()) {
			getTableau().flipTopPileCard(pileIdToFlip);
			score.tableauFlip++;
			GAME_LOG.debug("GameId:{} User:{} Flip pile {} reveals {}",
					game.gameId, user, pileIdToFlip, card.abbrev());
		}
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

	public static class CanPush {
		private Boolean[] foundationPile = new Boolean[4];
		private Boolean[] tableauBuild = new Boolean[7];
		
		public CanPush(int numOfPlayers) {
			super();
			foundationPile = new Boolean[4*numOfPlayers];
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
	
	public class Score {
		int toFoundation = 0;
		int discardToTableau = 0;
		int tableauFlip = 0;
		int discard = 0;
		int deckPassthrough = 0;
		
		public int getToFoundation() {
			return toFoundation;
		}
		public int getDiscardToTableau() {
			return discardToTableau;
		}
		public int getTableauFlip() {
			return tableauFlip;
		}
		public int getDiscard() {
			return discard;
		}
		public int getDeckPassthrough() {
			return deckPassthrough;
		}
		public int getTotalScore() {
			return (toFoundation*10)+
					(discardToTableau*5)+
					(tableauFlip*5)+
					(deckPassthrough*-20);
		}
		public int getTotalMoves() {
			return toFoundation+discardToTableau+discard;
		}
		
		public void prettyPrint() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Moves: "+getTotalMoves())
				.append(" Score: "+getTotalScore());
			System.out.println(buffer.toString());
		}
	}

}
