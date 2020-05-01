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
