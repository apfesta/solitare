package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	public Card peek() {
		return cards.peek();
	}
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	List<Card> asList() {
		return new ArrayList<Card>(cards);
	}
	
	public boolean contains(Object o) {
		return cards.contains(o);
	}
	public Collection<Card> getCards() {
		return Collections.unmodifiableCollection(cards);
	}

	public void print(int maxNumberOfCards) {
		List<Card> readOnly = asList();
		StringBuffer buffer = new StringBuffer("D: (").append(readOnly.size()).append(")");
		for (int i = 0; i<maxNumberOfCards && i<readOnly.size(); i++) {
			buffer.append(readOnly.get(i).abbrev()).append(" | ");
		}
		buffer.append("\n");
		System.out.println(buffer.toString());
	}
}
