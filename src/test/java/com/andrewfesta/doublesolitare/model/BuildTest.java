package com.andrewfesta.doublesolitare.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.andrewfesta.doublesolitare.exception.MoveException;
import com.andrewfesta.doublesolitare.model.Build.Sequence;

public class BuildTest {

	@Test
	public void testCanPushAlternateColor() {
		Card c1 = new Card(10, Suit.HEARTS);
		Build build = new Build(Sequence.ALTERNATE_COLOR);
		build.cards.push(c1);
				
		Card c2 = new Card(9, Suit.CLUBS);
		Card c3 = new Card(9, Suit.HEARTS); //wrong color
		Card c4 = new Card(Card.JACK, Suit.SPADES); //out of sequence
		Card c5 = new Card(8, Suit.HEARTS); //wrong value
		Card c6 = new Card(8, Suit.CLUBS); //out of sequence
		
		assertTrue(build.canPush(c2));
		assertFalse(build.canPush(c3));
		assertFalse(build.canPush(c4));
		assertFalse(build.canPush(c5));
		assertFalse(build.canPush(c6));
		
		//In order to actually push the card must already be on a build or pile
		Pile pile = new Pile();
		c2.setCurrentPile(pile);
		pile.push(c2); 
		
		build.push(c2);
	}
		
	@Test
	public void testCanPushRank() {
		
		//Empty build
		Build build = new Build(Sequence.RANK);
		Card c1 = new Card(Card.ACE, Suit.HEARTS); 
		Card c2 = new Card(2, Suit.HEARTS); 
		assertTrue(build.canPush(c1));
		assertFalse(build.canPush(c2)); //Not ACE
		
		//In order to actually push the card must already be on a build or pile
		Pile pile = new Pile();
		c1.setCurrentPile(pile);
		pile.push(c1); 
		
		build.push(c1);
				
		//Stack next		
		Card c3= new Card(Card.JACK, Suit.CLUBS); //wrong suit
		Card c4 = new Card(Card.QUEEN, Suit.HEARTS); //wrong value
		
		assertTrue(build.canPush(c2));
		assertFalse(build.canPush(c3));
		assertFalse(build.canPush(c4));
		
	}
	
	@Test
	public void testCanMoveRank() {
		Card c1 = new Card(Card.ACE, Suit.HEARTS); 
		
		//In order to actually push the card must already be on a build or pile
		Pile pile = new Pile();
		c1.setCurrentPile(pile);
		pile.push(c1); 
				
		//Push ACE onto foundation build
		Build build = new Build(Sequence.RANK);
		build.push(c1);
		
		//Try to move it off
		Build build2 = new Build(Sequence.RANK);
		assertTrue(build2.canPush(c1));
		try {
			build2.push(c1);
			fail("Expected MoveException");
		} catch (MoveException e) {}
	}
	
	public void testCanPushSubBuilds() {
		Build build1 = new Build(Sequence.ALTERNATE_COLOR);
		build1.cards.push(new Card(10, Suit.HEARTS));
		build1.cards.push(new Card(9, Suit.CLUBS));
		
		Build build2 = new Build(Sequence.ALTERNATE_COLOR);
		build2.cards.push(new Card(Card.JACK, Suit.SPADES));
	}
	
}
