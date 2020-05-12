package com.andrewfesta.doublesolitare.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameBoard {

	final Integer gameId;
	Tableau tableau;
	Pile stockPile;
	VisiblePile discardPile = new VisiblePile();
	Foundation foundation;
	boolean gameWon = false;
	
	boolean shuffle = true; //shuffle by default.  Tests should use false to have a predictable set
	
	private Map<Integer, Card> cards = new HashMap<>(); //card by ID
	
	public GameBoard(Integer gameId) {
		super();
		this.gameId = gameId;
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
		foundation = new Foundation();
		
		foundation.prettyPrint();
		tableau.prettyPrint();
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
		foundation = new Foundation();
		
		foundation.prettyPrint();
		tableau.prettyPrint();
	}
	
	public Card lookupCard(Integer cardId) {
		return cards.get(cardId);
	}
	
	public CanPush canPush(Card card) {
				
		CanPush canPush = new CanPush();
		
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
		List<Build> foundationBuld = getFoundation().getPile();
		for (int i=0; i<foundationBuld.size(); i++) {
			canPush.foundationPile[i] = !isFacedown && foundationBuld.get(i).canPush(card);
		}

		return canPush;
	}
	
	public void discard(int maxNumberOfCards) {
		if (stockPile.isEmpty()) {
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
	}
	
	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public Integer getGameId() {
		return gameId;
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

	public Pile getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(VisiblePile discardPile) {
		this.discardPile = discardPile;
	}

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}
	
	public boolean isGameWon() {
		return gameWon;
	}

	public void setGameWon(boolean gameWon) {
		this.gameWon = gameWon;
	}

	public static class CanPush {
		private Boolean[] foundationPile = new Boolean[4];
		private Boolean[] tableauBuild = new Boolean[7];
		public Boolean[] getFoundationPile() {
			return foundationPile;
		}
		public void setFoundationPile(Boolean[] foundationPile) {
			this.foundationPile = foundationPile;
		}
		public Boolean[] getTableauBuild() {
			return tableauBuild;
		}
		public void setTableauBuild(Boolean[] tableauBuild) {
			this.tableauBuild = tableauBuild;
		}
	}
	
}
