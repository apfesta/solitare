package com.andrewfesta.doublesolitare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.andrewfesta.doublesolitare.model.Build;
import com.andrewfesta.doublesolitare.model.Card;
import com.andrewfesta.doublesolitare.model.Foundation;
import com.andrewfesta.doublesolitare.model.GameBoard;
import com.andrewfesta.doublesolitare.model.GameBoard.CanPush;
import com.andrewfesta.doublesolitare.model.Suit;
import com.andrewfesta.doublesolitare.model.Tableau;

public class SamplePlayTest {

	@Test
	public void sampleWin() {
		GameBoard game = new GameBoard(1);
		game.setup(new Card[] {
				//STOCK PILE (in reverse order)
				new Card(Card.KING, Suit.HEARTS),
				new Card(Card.KING, Suit.CLUBS),
				new Card(Card.KING, Suit.DIAMONDS),
				
				new Card(Card.QUEEN, Suit.CLUBS),
				new Card(Card.QUEEN, Suit.DIAMONDS),
				new Card(Card.KING, Suit.SPADES),
				
				new Card(Card.JACK, Suit.DIAMONDS),
				new Card(Card.QUEEN, Suit.SPADES),
				new Card(Card.QUEEN, Suit.HEARTS),
				
				new Card(Card.JACK, Suit.SPADES),
				new Card(Card.JACK, Suit.HEARTS),
				new Card(Card.JACK, Suit.CLUBS),
				
				new Card(10, Suit.HEARTS),
				new Card(10, Suit.CLUBS),
				new Card(10, Suit.DIAMONDS),
				
				new Card(9, Suit.CLUBS),
				new Card(9, Suit.DIAMONDS),
				new Card(10, Suit.SPADES),
				
				new Card(8, Suit.DIAMONDS),
				new Card(9, Suit.SPADES),
				new Card(9, Suit.HEARTS),
				
				new Card(8, Suit.SPADES),
				new Card(8, Suit.HEARTS),
				new Card(8, Suit.CLUBS),
				
				//TABLEAU (in reverse order)
				new Card(6, Suit.HEARTS),
				
				new Card(6, Suit.CLUBS),
				new Card(4, Suit.DIAMONDS),
				
				new Card(6, Suit.DIAMONDS),
				new Card(5, Suit.SPADES),
				new Card(3, Suit.CLUBS),
				
				new Card(7, Suit.SPADES),
				new Card(5, Suit.HEARTS),
				new Card(3, Suit.DIAMONDS),
				new Card(2, Suit.CLUBS),
				
				new Card(7, Suit.HEARTS),
				new Card(5, Suit.CLUBS),
				new Card(4, Suit.SPADES),
				new Card(2, Suit.DIAMONDS),
				new Card(Card.ACE, Suit.CLUBS),
				
				new Card(7, Suit.CLUBS),
				new Card(5, Suit.DIAMONDS),
				new Card(4, Suit.HEARTS),
				new Card(3, Suit.SPADES),
				new Card(2, Suit.SPADES),
				new Card(Card.ACE, Suit.HEARTS),
				
				new Card(7, Suit.DIAMONDS),
				new Card(6, Suit.SPADES),
				new Card(4, Suit.CLUBS),
				new Card(3, Suit.HEARTS),
				new Card(2, Suit.HEARTS),
				new Card(Card.ACE, Suit.DIAMONDS),
				new Card(Card.ACE, Suit.SPADES),
		});
		
		Map<Suit, Integer> foundationIds = new HashMap<>();
		int f = 0;
		for (int v=Card.ACE; v<=7; v++) {
			for (Suit s: Suit.values()) {
				if (!foundationIds.containsKey(s)) {
					foundationIds.put(s, f++);
				}
				game.moveToFoundation(cardId(v,s), foundationIds.get(s));
			}
		}
		
		int i=0;
		for (int v=8; v<=Card.KING; v++) {
			for (Suit s: Suit.values()) {
				if (i==0) {
					game.discard(3);
					game.getDiscardPile().print(3);
				}
				game.moveToFoundation(cardId(v,s), foundationIds.get(s));
				i++;
				if (i==3) {
					i=0;
				}
			}
		}
				
	}
	
	private int cardId(int value, Suit suit) {
		return Card.unicodeInt(value, suit);
	}
		
	@Test
	public void samplePlay() {
		
		GameBoard game = new GameBoard(1);
		game.setShuffle(false);
		game.setup();
		
		Tableau tableau = game.getTableau();
		Foundation foundation = game.getFoundation();
		
		int fromBuildId;
		int toFoundationId;
		int toBuildId;
		
				
		//MOVE #1 - Move ACE of SPADES
		fromBuildId = 0;
		toFoundationId = 0;
		Build build = tableau.getBuild()[fromBuildId];
		Card card = build.peek();
		assertEquals(Suit.SPADES,card.getSuit());
		assertEquals(Card.ACE, card.getValue());
		CanPush canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		assertFalse(canPush.getTableauBuild()[1]);
		foundation.getPile().get(toFoundationId).push(card);
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE #2 - Move ACE of HEARTS
		fromBuildId = 2;
		toFoundationId = 1;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(Card.ACE, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		assertFalse(canPush.getTableauBuild()[1]);
		assertFalse(canPush.getTableauBuild()[0]);
		foundation.getPile().get(toFoundationId).push(card);
		//MOVE #2B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
				
		//Move #3 - Move KING of HEARTS
		fromBuildId = 5;
		toBuildId = 0;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(Card.KING, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		assertFalse(canPush.getTableauBuild()[1]);
		assertFalse(canPush.getFoundationPile()[0]);
		assertFalse(canPush.getFoundationPile()[2]);		
		tableau.getBuild()[toBuildId].push(card);
		//MOVE #3B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE 4 - Move NINE of SPADES
		fromBuildId = 2;
		toBuildId = 4;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.SPADES,card.getSuit());
		assertEquals(9, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		assertFalse(canPush.getTableauBuild()[1]);
		assertFalse(canPush.getFoundationPile()[0]);
		assertFalse(canPush.getFoundationPile()[2]);		
		tableau.getBuild()[toBuildId].push(card);
		//MOVE #4B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
				
		//MOVE #5 - Discard
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #6 - Move FIVE of CLUBS to 6 of HEARTS
		toBuildId = 3;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.CLUBS,card.getSuit());
		assertEquals(5, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		assertFalse(canPush.getTableauBuild()[1]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//MOVE #7 - Discard 3 times til we get to the ACE
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #8 - Move ACE of DIAMONDS
		toFoundationId = 2;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(Card.ACE, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		foundation.getPile().get(toFoundationId).push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//MOVE #9 - Discard
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #10 - Move FOUR DIAMONDS
		toBuildId = 3;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(4, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//MOVE #11 - Discard
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #12 - Move SEVEN DIAMONDS
		toBuildId = 1;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(7, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//MOVE #13 - Discard
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #14 - Move SIX CLUBS
		toBuildId = 1;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.CLUBS,card.getSuit());
		assertEquals(6, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//DIscard
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #14 - Move QUEEN CLUBS
		toBuildId = 0;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.CLUBS,card.getSuit());
		assertEquals(Card.QUEEN, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//MOVE 15 - Move JACK of HEARTS
		fromBuildId = 5;
		toBuildId = 0;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(Card.JACK, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);		
		tableau.getBuild()[toBuildId].push(card);
		//MOVE #15B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE 16 - Move EIGHT of HEARTS
		fromBuildId = 5;
		toBuildId = 4;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(8, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);		
		tableau.getBuild()[toBuildId].push(card);
		//MOVE #16B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//DIscard
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #17 - Move SEVEN CLUBS
		toBuildId = 4;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.CLUBS,card.getSuit());
		assertEquals(7, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//DIscard
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #18 - Move TEN CLUBS
		toBuildId = 0;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.CLUBS,card.getSuit());
		assertEquals(10, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
				
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #8 - Move TWO of DIAMONDS
		toFoundationId = 2;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(2, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		foundation.getPile().get(toFoundationId).push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//MOVE #19 - Move SIX of DIAMONDS
		toBuildId = 4;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(6, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//MOVE #20 - Move FIVE of DIAMONDS
		toBuildId = 1;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(5, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);
		tableau.getBuild()[toBuildId].push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
			
		
		//MOVE #21 - Move THREE of DIAMONDS
		toFoundationId = 2;
		card = game.getDiscardPile().peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(3, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		foundation.getPile().get(toFoundationId).push(card);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		game.getDiscardPile().print(3);
		
		//MOVE #22 - Move FOUR of DIAMONDS
		fromBuildId = 3;
		toFoundationId = 2;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(4, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		foundation.getPile().get(toFoundationId).push(card);
				
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE #23 - Move FIVE of DIAMONDS
		fromBuildId = 1;
		toFoundationId = 2;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(5, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		foundation.getPile().get(toFoundationId).push(card);
				
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE #24 - Move SIX of DIAMONDS
		fromBuildId = 4;
		toFoundationId = 2;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.DIAMONDS,card.getSuit());
		assertEquals(6, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		foundation.getPile().get(toFoundationId).push(card);
				
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE #23 - Move FOUR of HEARTS
		fromBuildId = 5;
		toBuildId = 3;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(4, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);		
		tableau.getBuild()[toBuildId].push(card);
		//MOVE #23B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE #24 - Move SIX of HEARTS Build
		fromBuildId = 3;
		toBuildId = 4;
		build = tableau.getBuild()[fromBuildId];
		card = build.peekFromBottom(0);
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(6, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getTableauBuild()[toBuildId]);	
		tableau.getBuild()[toBuildId].push(build, card);
		//MOVE #24B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE #25 - Move TWO of HEARTS
		fromBuildId = 3;
		toFoundationId = 1;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(2, card.getValue());
		canPush = game.canPush(card);
		assertTrue(canPush.getFoundationPile()[toFoundationId]);
		foundation.getPile().get(toFoundationId).push(card);
		//MOVE #25B - Flip card
		tableau.flipTopPileCard(fromBuildId);
				
		foundation.prettyPrint();
		tableau.prettyPrint();

		
				
		game.discard(3);
		game.getDiscardPile().print(3);
		game.discard(3);
		game.getDiscardPile().print(3);
		
		//Game over because we've run out of moves
	}
	
}
