package com.andrewfesta.doublesolitare.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.andrewfesta.doublesolitare.model.Build;
import com.andrewfesta.doublesolitare.model.Card;
import com.andrewfesta.doublesolitare.model.Suit;
import com.andrewfesta.doublesolitare.model.Build.Sequence;

public class BuildTest {

	@Test
	public void testCanPushAlternateColor() {
		Card c1 = new Card(10, Suit.HEARTS);
		Build build = new Build(Sequence.ALTERNATE_COLOR);
		build.push(c1);
		
		Card c2 = new Card(9, Suit.CLUBS);
		Card c3 = new Card(9, Suit.HEARTS); //wrong color
		Card c4 = new Card(Card.JACK, Suit.HEARTS); //wrong value
		Card c5 = new Card(8, Suit.HEARTS); //wrong value
		
		assertTrue(build.canPush(c2));
		assertFalse(build.canPush(c3));
		assertFalse(build.canPush(c4));
		assertFalse(build.canPush(c5));
		
		build.push(c2);
	}
	
	@Test
	public void testCanPushBuilds() {
		Build build1 = new Build(Sequence.ALTERNATE_COLOR);
		build1.push(new Card(10, Suit.HEARTS));
		build1.push(new Card(9, Suit.CLUBS));
		
		Build build2 = new Build(Sequence.ALTERNATE_COLOR);
		build2.push(new Card(Card.JACK, Suit.SPADES));
		
		Build build3 = new Build(Sequence.ALTERNATE_COLOR);
		build3.push(new Card(8, Suit.DIAMONDS));
		
		assertTrue(build2.canPush(build1));
		assertFalse(build1.canPush(build2));
		assertTrue(build1.canPush(build3));
	}
	
	@Test
	public void testCanPushRank() {
		Card c1 = new Card(10, Suit.HEARTS);
		Build build = new Build(Sequence.RANK);
		build.push(c1);
		
		Card c2 = new Card(Card.JACK, Suit.HEARTS); 
		Card c3= new Card(Card.JACK, Suit.CLUBS); //wrong suit
		Card c4 = new Card(Card.QUEEN, Suit.HEARTS); //wrong value
		
		assertTrue(build.canPush(c2));
		assertFalse(build.canPush(c3));
		assertFalse(build.canPush(c4));
		
		build.push(c2);
	}
	
}
