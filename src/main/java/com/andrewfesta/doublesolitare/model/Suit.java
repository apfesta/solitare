package com.andrewfesta.doublesolitare.model;

public enum Suit {

	SPADES(0x2660, Color.BLACK),
	HEARTS(0x2665, Color.RED),
	CLUBS(0x2663, Color.BLACK),
	DIAMONDS(0x2666, Color.RED);
	
	final private char symbol;
	final private Color color;

	private Suit(int symbol, Color color) {
		this.symbol = (char)symbol;
		this.color = color;
	}
	
	public char getSymbol() {
		return symbol;
	}

	public Color getColor() {
		return color;
	}

	public enum Color {
		BLACK,
		RED
	}
	
}
