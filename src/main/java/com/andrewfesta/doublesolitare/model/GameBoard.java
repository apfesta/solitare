package com.andrewfesta.doublesolitare.model;

public class GameBoard {

	Tableau tableau;
	Pile stockPile;
	Foundation foundation;
	
	public void setup() {
		tableau = new Tableau();
		stockPile = tableau.setup();
		foundation = new Foundation();
		
		tableau.prettyPrint();
	}
	
}
