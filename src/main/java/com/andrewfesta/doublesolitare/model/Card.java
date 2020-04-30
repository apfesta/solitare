package com.andrewfesta.doublesolitare.model;

import com.andrewfesta.doublesolitare.model.Suit.Color;

public class Card {
	
	public static final int ACE = 1;
	public static final int JACK = 11;
	public static final int QUEEN = 12;
	public static final int KING = 13;

	final private Suit suit;
	final private int value;
	
	public Card(int value, Suit suit) {
		super();
		this.value = value;
		this.suit = suit;
	}
	
	public Suit getSuit() {
		return suit;
	}

	public int getValue() {
		return value;
	}

	public int getUnicode() {
		int x = 0x1F0A1;
		System.out.println((char)x);
		return x;
	}

	public String valueName() {
		switch (value){
		case ACE:
			return "ACE";
		case JACK:
			return "JACK";
		case QUEEN:
			return "QUEEN";
		case KING:
			return "KING";
		default:
			return String.valueOf(value);
		}
	}
	public String symbol() {
		switch (value){
		case ACE:
			return "A";
		case JACK:
			return "J";
		case QUEEN:
			return "Q";
		case KING:
			return "K";
		default:
			return String.valueOf(value);
		}
	}
	
	@Override
	public String toString() {
		return valueName()+" of "+suit.name();
	}
	
	public Color getColor() {
		return suit.getColor();
	}

	public String abbrev() {
		StringBuffer buffer = new StringBuffer();
		String symbol = symbol();
		if (symbol.length()==1) {
			buffer.append(" ");
		}
		buffer.append(symbol);
		buffer.append(suit.getSymbol());
		return buffer.toString();
		
	}
		
	
}
