package com.andrewfesta.doublesolitare.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.andrewfesta.doublesolitare.model.Build.Sequence;
import com.andrewfesta.doublesolitare.model.UserBoard.CanPush;

public class UserBoardTest {
	
	Map<Integer, Card> cards = new HashMap<>(); //card by ID
	
	private Card newCard(int value, Suit suit) {
		Card c = new Card(value, suit);
		cards.put(c.getUnicodeInt(), c);
		return c;
	}

	@Test
	public void canPush() {
		User user = new User(1);
		GameBoard game = new GameBoard(user, 1, false);
		game.foundation = new Foundation();
		
		UserBoard userBoard = new UserBoard(game, user);
		userBoard.tableau = new Tableau();
		
		//We're going to check to see if we can move the 10 from build1 to build2
		
		Build build1 = new Build(Sequence.ALTERNATE_COLOR);
		build1.cards.push(newCard(Card.JACK, Suit.CLUBS));
		build1.cards.push(newCard(10, Suit.HEARTS));
		build1.cards.push(newCard(9, Suit.CLUBS));
		userBoard.tableau.build[1] = build1;
		
		Build build2 = new Build(Sequence.ALTERNATE_COLOR);
		build2.cards.push(newCard(Card.QUEEN, Suit.DIAMONDS));
		build2.cards.push(newCard(Card.JACK, Suit.SPADES));
		userBoard.tableau.build[2] = build2;
		
		System.out.println(cards);
		
		CanPush canPush = userBoard.canPush(cards.get(127162));
		assertFalse(canPush.getTableauBuild()[1]);
		assertTrue(canPush.getTableauBuild()[2]);
	}
	
	@Test
	public void moveToTableau() {
		User user = new User(1);
		GameBoard game = new GameBoard(user, 1, false);
		game.foundation = new Foundation();
		
		UserBoard userBoard = new UserBoard(game, user);
		userBoard.tableau = new Tableau();
		
		//We're going to move the 10 from build1 to build2
		
		Build build1 = new Build(Sequence.ALTERNATE_COLOR);
		build1.cards.push(newCard(Card.JACK, Suit.CLUBS));
		build1.cards.push(newCard(10, Suit.HEARTS));
		build1.cards.push(newCard(9, Suit.CLUBS));
		userBoard.tableau.build[1] = build1;
		for (Card c: build1.cards) {
			c.setCurrentBuild(userBoard.tableau.build[1]);
		}
		
		Build build2 = new Build(Sequence.ALTERNATE_COLOR);
		build2.cards.push(newCard(Card.QUEEN, Suit.DIAMONDS));
		build2.cards.push(newCard(Card.JACK, Suit.SPADES));
		userBoard.tableau.build[2] = build2;
		for (Card c: build2.cards) {
			c.setCurrentBuild(userBoard.tableau.build[2]);
		}
		
		userBoard.cards = cards;
		
		userBoard.getTableau().prettyPrint();
		
		userBoard.moveToTableau(127162, 2);
		userBoard.getTableau().prettyPrint();
		
		assertEquals(1, userBoard.tableau.getBuild()[1].size());
		assertEquals(4, userBoard.tableau.getBuild()[2].size());
	}
	
}
