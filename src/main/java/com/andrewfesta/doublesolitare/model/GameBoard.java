package com.andrewfesta.doublesolitare.model;

public class GameBoard {

	final Integer gameId;
	Tableau tableau;
	Pile stockPile;
	Foundation foundation;
	
	public GameBoard(Integer gameId) {
		super();
		this.gameId = gameId;
	}

	public void setup() {
		tableau = new Tableau();
		stockPile = tableau.setup();
		foundation = new Foundation();
		
		tableau.prettyPrint();
	}

	public Integer getGameId() {
		return gameId;
	}
	
}
