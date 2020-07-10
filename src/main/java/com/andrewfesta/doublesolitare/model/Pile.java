package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Pile {
	
	protected ArrayDeque<Card> cards;
	String toStringPrefix = "Pile";
	
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
	public int getNumberOfCards() {
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

	public void print(int maxNumberOfCards) {
		StringBuffer buffer = new StringBuffer(toString());
		buffer.append("\n");
		System.out.println(toString());
	}
	
	public void setToStringPrefix(String toStringPrefix) {
		this.toStringPrefix = toStringPrefix;
	}
	
	public String prefix() {
		return toStringPrefix;
	}
	
	@Override
	public String toString() {
		List<Card> readOnly = asList();
		StringBuffer buffer = new StringBuffer(prefix()).append(": (").append(readOnly.size()).append(")");
		for (int i = 0; i<cards.size() && i<readOnly.size(); i++) {
			buffer.append(readOnly.get(i).abbrev()).append(" | ");
		}
		return buffer.toString();
	}
	
}
