package com.andrewfesta.doublesolitare.model;

import com.andrewfesta.doublesolitare.model.Suit.Color;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Card {
	
	public static final int ACE = 1;
	public static final int JACK = 11;
	public static final int QUEEN = 12;
	public static final int KING = 13;

	final private Suit suit;
	final private int value;
	
	private Build currentBuild;
	private Pile currentPile;
	
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

	public int getUnicodeInt() {
		int code = 0;
		switch (suit) {
		case SPADES:
			code = 0x1F0A0 + value;
			break;
		case HEARTS:
			code = 0x1F0B0 + value;
			break;
		case DIAMONDS:
			code = 0x1F0C0 + value;
			break;
		case CLUBS:
			code = 0x1F0D0 + value;
			break;
		}
		return code;
	}
	
	public char getUnicodeChar() {
		return (char)getUnicodeInt();
	}
	
	public String getUnicodeHtmlEntity() {
		int code=getUnicodeInt();
		return "&#"+code+";";
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
	
	void setCurrentBuild(Build currentLocation) {
		this.currentBuild = currentLocation;
	}
	
	@JsonIgnore
	public Build getCurrentBuild() {
		return this.currentBuild;
	}
	
	@JsonIgnore
	public Pile getCurrentPile() {
		return currentPile;
	}

	void setCurrentPile(Pile currentPile) {
		this.currentPile = currentPile;
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
