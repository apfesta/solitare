package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Pile {
	
	protected ArrayDeque<Card> cards;
	
	Pile() {
		super();
		this.cards = new ArrayDeque<>();
	}
	Pile(Pile old) {
		super();
		this.cards = new ArrayDeque<>(old.cards);
	}

	public static final Pile emptyPile() {
		Pile pile = new Pile();
		return pile;
	}

	public Card pop() {
		return cards.pop();
	}

	public void push(Card e) {
		cards.push(e);
	}
	public int size() {
		return cards.size();
	}
	public Card peekFromBottom(int index) {
		return asList().get(cards.size()-1-index);
	}
	List<Card> asList() {
		return new ArrayList<Card>(cards);
	}
}
