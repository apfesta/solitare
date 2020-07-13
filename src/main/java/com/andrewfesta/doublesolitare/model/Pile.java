package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

	public void print() {
		List<Card> readOnly = asList();
		StringBuffer buffer = new StringBuffer(prefix()).append(": (").append(readOnly.size()).append(")");
		buffer.append(" ");
		System.out.println(buffer.toString());
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
	
	public static <P> Builder<P> builder(P pileParent, boolean cardReferencesPile, Pile pile) {
		return new Builder<P>(pileParent, cardReferencesPile, pile);
	}
	public static <P> Builder<P> builder(P pileParent, boolean cardReferencesPile) {
		return new Builder<P>(pileParent, cardReferencesPile);
	}
	
	public static class Builder<P> {
		final P pileParent;
		final Pile pile;
		Map<Integer, Card> cards = new HashMap<>();
		boolean cardReferencesPile = false;
		Consumer<Pile> pileConsumer;
		
		public Builder(P pileParent, boolean cardReferencesPile, Pile pile) {
			super();
			this.pileParent = pileParent;
			this.cardReferencesPile = cardReferencesPile;
			this.pile = pile;
		}
		public Builder(P pileParent, boolean setCurrentPile) {
			this(pileParent, setCurrentPile, new Pile());
		}
		public Builder<P> cards(Map<Integer, Card> cards) {
			this.cards = cards;
			return this;
		}
		public Builder<P> consumer(Consumer<Pile> consumer) {
			this.pileConsumer = consumer;
			return this;
		}

		public Builder<P> add(int value, Suit suit) {
			Card c = new Card(value, suit);
			pile.cards.add(c);
			if (cardReferencesPile) {
				c.setCurrentPile(pile);
			}
			if (cards.containsKey(c.getUnicodeInt())) {
				throw new RuntimeException("Card already exists in deck");
			}
			cards.put(c.getUnicodeInt(), c);
			return this;
		}
				
		public P addToParent() {
			if (pileConsumer!=null) {
				pileConsumer.accept(pile);
			}
			return pileParent;
		}
		
	}
	
}
