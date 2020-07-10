package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

public class Deck extends Pile {

	static Random rand = new Random(); 
	
	private Deck() {
		super();
		this.setToStringPrefix("Deck");
	}

	public static Deck getInstance() {
		Deck deck = new Deck();
		for (Suit suit : Suit.values()) {
			for (int val=1; val<=13; val++) {
				deck.cards.add(new Card(val, suit));
			}
		}
		return deck;
	}
	
	public static Deck getStackedDeck(Card[] cards) {
		if (cards.length!=52) {
			throw new IllegalArgumentException("Not enough cards.  Expected 52.  Got "+cards.length);
		}
		Deck deck = new Deck();
		for (Card card: cards) {
			deck.cards.push(card);
		
		}
		return deck;
	}
	
	public static ArrayDeque<Card> shuffle(ArrayDeque<Card> cards, int n) {
		for (int i=0; i<n; i++) {
			cards = shuffle(cards);
		}
		return cards;
	}
	
	public static ArrayDeque<Card> shuffle(ArrayDeque<Card> cards) {
		ArrayList<Card> oldOrder = new ArrayList<>(cards);
		ArrayDeque<Card> newOrder = new ArrayDeque<>();
		for (int i = 0; i < 52; i++) {
			int index = rand.nextInt(oldOrder.size());
			Card c = oldOrder.remove(index);
			newOrder.add(c);
		}
		return newOrder;
	}
	
	public void shuffle() {
		cards = shuffle(cards, rand.nextInt(4)+1);
	}
	
}
