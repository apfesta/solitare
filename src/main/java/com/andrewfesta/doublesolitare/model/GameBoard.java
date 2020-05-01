package com.andrewfesta.doublesolitare.model;

import java.util.HashMap;
import java.util.Map;

public class GameBoard {

	final Integer gameId;
	Tableau tableau;
	Pile stockPile;
	Foundation foundation;
	
	private Map<Integer, Card> cards = new HashMap<>(); //card by ID
	
	public GameBoard(Integer gameId) {
		super();
		this.gameId = gameId;
	}

	public void setup() {
		Deck d = Deck.getInstance();
//		d.shuffle();
		for (Card c: d.cards) {
			cards.put(c.getUnicodeInt(), c);
		}
		
		tableau = new Tableau();
		stockPile = tableau.setup(d);
		foundation = new Foundation();
		
		tableau.prettyPrint();
	}
	
	public Card lookupCard(Integer cardId) {
		return cards.get(cardId);
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

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}
	
}
