package com.andrewfesta.doublesolitare.model;

import org.junit.Test;

public class DeckTest {

	@Test
	public void shuffle() {
		Deck deck = Deck.getInstance();
		for (int i = 0; i <5; i++) {
			deck.shuffle();
			deck.print(52);
		}
	}
	
}
