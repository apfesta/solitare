package com.andrewfesta.doublesolitare.model;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.andrewfesta.doublesolitare.exception.PushException;

public class SimulationsTest {
	
	GameBoard game;
	
	private int card(int value, Suit suit) {
		Card c = new Card(value, suit);
		return c.getUnicodeInt();
	}
	
	private Card newCard(int value, Suit suit) {
		Card c = new Card(value, suit);
		return c;
	}
	
	private void pushToBuild(User user, int buildid, Card c) {
		game.userBoards.get(user).cards.put(c.getUnicodeInt(), c);
		Build build = game.userBoards.get(user).tableau.build[buildid];
		build.cards.push(c);
		c.setCurrentBuild(build);
	}
	private void addToPile(User user, int pileid, Card c) {
		game.userBoards.get(user).cards.put(c.getUnicodeInt(), c);
		Pile pile = game.userBoards.get(user).tableau.pile[pileid];
		pile.cards.add(c);
	}
	private void addToStock(User user, Card c) {
		if (game.userBoards.get(user).cards.containsKey(c.getUnicodeInt())) {
			throw new RuntimeException("Card already exists in deck");
		}
		game.userBoards.get(user).cards.put(c.getUnicodeInt(), c);
		Pile pile = game.userBoards.get(user).stockPile;
		pile.cards.add(c);
		c.setCurrentPile(pile);
	}
	
	@Test
	public void simulation1() {
		User user3 = new User(3);
		User user4 = new User(4);
		game = new GameBoard(user3, 3, true);
		game.foundation = new Foundation();
		game.foundation.addPlayer();
		game.foundation.addPlayer();
		UserBoard userBoard4 = new UserBoard(game, user4);
		game.userBoards.put(user4, userBoard4);
		
		/*
		 * SETUP
		 */
		UserBoard userBoard3 = UserBoard.builder(game, user3)
			.tableau()
				.stack(0)
					.topCard(10, Suit.DIAMONDS).addToTableau()
				.stack(1)
					.topCard(Card.KING, Suit.DIAMONDS)
					.pile()
						.add(Card.QUEEN, Suit.HEARTS).addToParent().addToTableau()
				.stack(2)
					.topCard(10, Suit.HEARTS).addToTableau()
				.stack(3)
					.topCard(Card.QUEEN, Suit.DIAMONDS)
					.pile()
						.add(5, Suit.CLUBS)
						.add(6, Suit.DIAMONDS)
						.add(Card.ACE, Suit.SPADES).addToParent().addToTableau()
				.stack(4)
					.topCard(4, Suit.CLUBS).addToTableau()
				.stack(5)
					.topCard(10, Suit.CLUBS).addToTableau()
				.stack(6)
					.topCard(Card.KING, Suit.SPADES).addToTableau()
				.addToBoard()
			.stockPile()
				.add(2, Suit.SPADES)
				.add(9, Suit.CLUBS)
				.add(Card.ACE, Suit.DIAMONDS)
				.add(9, Suit.DIAMONDS)
				.add(8, Suit.SPADES)
				.add(4, Suit.DIAMONDS)
				.add(7, Suit.DIAMONDS)
				.add(7, Suit.CLUBS)
				.add(9, Suit.SPADES)
				.add(7, Suit.HEARTS)
				.add(Card.ACE, Suit.HEARTS)
				.add(5, Suit.DIAMONDS)
				.add(4, Suit.SPADES)
				.add(5, Suit.SPADES)
				.add(5, Suit.HEARTS)
				.add(8, Suit.DIAMONDS)
				.add(Card.JACK, Suit.HEARTS)
				.add(9, Suit.HEARTS)
				.add(Card.JACK, Suit.DIAMONDS)
				.add(Card.KING, Suit.CLUBS)
				.add(4, Suit.HEARTS)
				.add(10, Suit.SPADES)
				.add(2, Suit.DIAMONDS)
				.add(Card.ACE, Suit.CLUBS).addToParent().build();
		
		userBoard4.tableau = new Tableau();
		userBoard4.stockPile = new Pile();
		userBoard4.score = userBoard4.new Score();
		pushToBuild(user4, 0, newCard(Card.QUEEN, Suit.SPADES));
		pushToBuild(user4, 1, newCard(9, Suit.SPADES));
		addToPile(user4, 1, newCard(Card.JACK, Suit.CLUBS));
		pushToBuild(user4, 2, newCard(Card.KING, Suit.HEARTS));
		addToPile(user4, 2, newCard(6, Suit.SPADES));
		addToPile(user4, 2, newCard(6, Suit.DIAMONDS));
		pushToBuild(user4, 3, newCard(8, Suit.DIAMONDS));
		addToPile(user4, 3, newCard(4, Suit.SPADES));
		addToPile(user4, 3, newCard(Card.KING, Suit.CLUBS));
		addToPile(user4, 3, newCard(5, Suit.CLUBS));
		pushToBuild(user4, 4, newCard(4, Suit.HEARTS));
		addToPile(user4, 4, newCard(9, Suit.HEARTS));
		addToPile(user4, 4, newCard(10, Suit.SPADES));
		pushToBuild(user4, 5, newCard(Card.ACE, Suit.SPADES));
		addToPile(user4, 5, newCard(Card.ACE, Suit.DIAMONDS));
		addToPile(user4, 5, newCard(Card.QUEEN, Suit.HEARTS));
		addToPile(user4, 5, newCard(7, Suit.HEARTS));
		addToPile(user4, 5, newCard(4, Suit.CLUBS));
		addToPile(user4, 5, newCard(2, Suit.SPADES));
		pushToBuild(user4, 6, newCard(3, Suit.SPADES));
		addToPile(user4, 6, newCard(Card.KING, Suit.DIAMONDS));
		addToPile(user4, 6, newCard(8, Suit.HEARTS));
		addToStock(user4, newCard(10, Suit.DIAMONDS));
		addToStock(user4, newCard(2, Suit.CLUBS));
		addToStock(user4, newCard(8, Suit.CLUBS));
		addToStock(user4, newCard(4, Suit.DIAMONDS));
		addToStock(user4, newCard(Card.ACE, Suit.CLUBS));
		addToStock(user4, newCard(10, Suit.CLUBS));
		addToStock(user4, newCard(5, Suit.HEARTS));
		addToStock(user4, newCard(Card.ACE, Suit.HEARTS));
		addToStock(user4, newCard(6, Suit.CLUBS));
		addToStock(user4, newCard(Card.QUEEN, Suit.DIAMONDS));
		addToStock(user4, newCard(6, Suit.HEARTS));
		addToStock(user4, newCard(3, Suit.CLUBS));
		addToStock(user4, newCard(10, Suit.HEARTS));
		addToStock(user4, newCard(7, Suit.CLUBS));
		addToStock(user4, newCard(2, Suit.DIAMONDS));
		addToStock(user4, newCard(5, Suit.SPADES));
		addToStock(user4, newCard(9, Suit.DIAMONDS));
		addToStock(user4, newCard(Card.JACK, Suit.DIAMONDS));
		addToStock(user4, newCard(Card.JACK, Suit.SPADES));
		addToStock(user4, newCard(Card.QUEEN, Suit.CLUBS));
		addToStock(user4, newCard(2, Suit.HEARTS));
		addToStock(user4, newCard(5, Suit.DIAMONDS));
		addToStock(user4, newCard(3, Suit.DIAMONDS));
		addToStock(user4, newCard(7, Suit.DIAMONDS));
		
		
		
		
		/*
		 * MOVES
		 */
		game.moveToTableau(user3, card(Card.QUEEN, Suit.DIAMONDS), 6);
		game.moveToFoundation(user4, card(Card.ACE, Suit.SPADES), 6);
		game.moveToFoundation(user4, card(Card.ACE, Suit.DIAMONDS), 7);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.QUEEN, Suit.SPADES), 2);
		game.moveToTableau(user4, card(Card.KING, Suit.HEARTS), 0);
		game.moveToFoundation(user3, card(Card.ACE, Suit.DIAMONDS), 5);
		game.moveToTableau(user4, card(3, Suit.SPADES), 4);
		game.moveToTableau(user3, card(9, Suit.CLUBS), 2);
		game.moveToTableau(user4, card(8, Suit.DIAMONDS), 1);
		game.moveToFoundation(user3, card(2, Suit.SPADES), 6);
		game.discard(user3);
		game.moveToTableau(user3, card(4, Suit.DIAMONDS), 3);
		game.moveToFoundation(user4, card(3, Suit.SPADES), 6);
		game.moveToFoundation(user4, card(4, Suit.SPADES), 6);
		game.moveToTableau(user4, card(Card.QUEEN, Suit.HEARTS), 3);
		game.moveToTableau(user4, card(6, Suit.SPADES), 5);
		game.discard(user3);
		game.discard(user4);
		game.moveToTableau(user3, card(9, Suit.SPADES), 0);
		game.discard(user4);
		game.discard(user4);
		game.discard(user4);
		game.discard(user3);
		game.moveToTableau(user4, card(3, Suit.CLUBS), 4);
		game.discard(user3);
		game.discard(user4);
		game.moveToFoundation(user4, card(2, Suit.DIAMONDS), 5);
		game.discard(user3);
		game.moveToTableau(user4, card(7, Suit.CLUBS), 1);
		game.moveToTableau(user3, card(9, Suit.HEARTS), 5);
		game.moveToTableau(user4, card(6, Suit.DIAMONDS), 1);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.KING, Suit.CLUBS), 2);
		game.moveToTableau(user4, card(5, Suit.CLUBS), 1);
		game.discard(user3);
		game.moveToFoundation(user3, card(Card.ACE, Suit.CLUBS), 4);
		game.moveToTableau(user4, card(Card.KING, Suit.DIAMONDS), 3);
		game.moveToFoundation(user3, card(2, Suit.DIAMONDS), 7);
		game.discard(user3);
		game.discard(user3);
		game.moveToFoundation(user3, card(Card.ACE, Suit.HEARTS), 0);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.JACK, Suit.DIAMONDS), 0);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.moveToTableau(user4, card(4, Suit.HEARTS), 1);
		game.moveToFoundation(user4, card(2, Suit.HEARTS), 0);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.QUEEN, Suit.CLUBS), 3);
		game.moveToTableau(user4, card(Card.JACK, Suit.SPADES), 2);
		game.discard(user3);
		game.discard(user4);
		game.discard(user4);
		game.discard(user3);
		game.moveToTableau(user4, card(8, Suit.CLUBS), 4);
		game.moveToTableau(user4, card(7, Suit.HEARTS), 4);
		game.discard(user3);
		game.discard(user3);
		game.moveToFoundation(user4, card(2, Suit.CLUBS), 4);
		game.moveToFoundation(user4, card(3, Suit.CLUBS), 4);
		game.moveToFoundation(user4, card(4, Suit.CLUBS), 4);
		game.moveToTableau(user4, card(10, Suit.DIAMONDS), 2);
		game.moveToTableau(user4, card(9, Suit.SPADES), 2);
		game.discard(user4);
		game.discard(user3);
		game.moveToTableau(user4, card(10, Suit.CLUBS), 0);
		game.moveToTableau(user4, card(9, Suit.HEARTS), 0);
		game.moveToFoundation(user4, card(Card.ACE, Suit.CLUBS), 1);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user4);
		game.moveToTableau(user4, card(10, Suit.HEARTS), 1);
		game.discard(user4);
		game.moveToTableau(user4, card(5, Suit.DIAMONDS), 0);
		game.moveToTableau(user4, card(9, Suit.DIAMONDS), 4);
		game.discard(user3);
		game.moveToFoundation(user4, card(5, Suit.SPADES), 6);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.moveToFoundation(user4, card(Card.ACE, Suit.HEARTS), 2);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user4);
		game.discard(user4);
		game.moveToFoundation(user4, card(3, Suit.DIAMONDS), 5);
		game.moveToFoundation(user3, card(4, Suit.DIAMONDS), 5);
		game.moveToFoundation(user4, card(5, Suit.DIAMONDS), 5);
		try {
			game.moveToFoundation(user3, card(5, Suit.DIAMONDS), 5);
			fail("Exception expected");
		} catch (PushException e) {}
		game.moveToFoundation(user4, card(6, Suit.SPADES), 6);
		game.moveToFoundation(user3, card(5, Suit.CLUBS), 4);
		game.moveToFoundation(user3, card(6, Suit.DIAMONDS), 5);
		game.moveToFoundation(user3, card(Card.ACE, Suit.SPADES), 3);
		game.moveToFoundation(user4, card(2, Suit.SPADES), 3);
		game.discard(user4);
		game.moveToFoundation(user4, card(7, Suit.DIAMONDS), 5);
		game.moveToTableau(user3, card(Card.KING, Suit.DIAMONDS), 3);
		game.moveToTableau(user4, card(7, Suit.CLUBS), 6);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.moveToFoundation(user4, card(6, Suit.CLUBS), 4);
		try {
			game.moveToTableau(user4, card(6, Suit.DIAMONDS), 2);
			fail("Exception expected");
		} catch (PushException e) {}
				
		//PRINT
		game.getFoundation().prettyPrint();
		userBoard3.getTableau().prettyPrint();
		userBoard3.prettyPrintStockAndDiscardPiles(3);
		userBoard3.getScore().prettyPrint();
		userBoard4.getTableau().prettyPrint();
		userBoard4.prettyPrintStockAndDiscardPiles(3);
		userBoard4.getScore().prettyPrint();
	}

	@Test
	public void simulation2() {
		User user3 = new User(3);
		User user4 = new User(4);
		game = new GameBoard(user3, 8, true);
		game.foundation = new Foundation();
		game.foundation.addPlayer();
		game.foundation.addPlayer();
		
		UserBoard userBoard3 = UserBoard.builder(game, user3)
			.tableau()
				.stack(0)
					.topCard(10, Suit.SPADES).addToTableau()
				.stack(1)
					.topCard(3, Suit.SPADES)
					.pile()
						.add(4, Suit.CLUBS).addToParent()
					.addToTableau()
				.stack(2)
					.topCard(Card.ACE, Suit.SPADES)
					.pile()
						.add(Card.QUEEN, Suit.SPADES)
						.add(8, Suit.CLUBS)
						.addToParent()
					.addToTableau()
				.stack(3)
					.topCard(10, Suit.CLUBS)
					.pile()
						.add(Card.JACK, Suit.CLUBS)
						.add(3, Suit.DIAMONDS)
						.add(6, Suit.CLUBS)
						.addToParent()
					.addToTableau()
				.stack(4)
					.topCard(Card.KING, Suit.DIAMONDS)
					.pile()
						.add(5, Suit.CLUBS)
						.add(Card.KING, Suit.CLUBS)
						.add(Card.ACE, Suit.HEARTS)
						.add(6, Suit.HEARTS)
						.addToParent()
					.addToTableau()
				.stack(5)
					.topCard(7, Suit.SPADES)
					.addToTableau()
				.stack(6)
					.topCard(7, Suit.CLUBS)
					.addToTableau()
				.addToBoard()
			.stockPile()
				.add(Card.ACE, Suit.CLUBS)
				.add(9, Suit.DIAMONDS)
				.add(7, Suit.DIAMONDS)
				.add(6, Suit.SPADES)
				.add(Card.KING, Suit.HEARTS)
				.add(9, Suit.SPADES)
				.add(9, Suit.CLUBS)
				.add(3, Suit.CLUBS)
				.add(Card.QUEEN, Suit.DIAMONDS)
				.add(Card.ACE, Suit.DIAMONDS)
				.add(10, Suit.HEARTS)
				.add(10, Suit.DIAMONDS)
				.add(Card.JACK, Suit.SPADES)
				.add(2, Suit.HEARTS)
				.add(6, Suit.DIAMONDS)
				.add(4, Suit.DIAMONDS)
				.add(8, Suit.SPADES)
				.add(5, Suit.HEARTS)
				.add(Card.JACK, Suit.HEARTS)
				.add(4, Suit.HEARTS)
				.add(5, Suit.DIAMONDS)
				.add(Card.QUEEN, Suit.CLUBS)
				.add(3, Suit.HEARTS)
				.add(7, Suit.HEARTS)
				.addToParent()
			.build();
		
		UserBoard userBoard4 = UserBoard.builder(game, user4)
			.tableau()
				.stack(0)
					.topCard(4, Suit.HEARTS)
					.addToTableau()
				.stack(1)
					.topCard(Card.ACE, Suit.SPADES)
					.pile()
						.add(Card.JACK, Suit.CLUBS).addToParent()
					.addToTableau()
				.stack(2)
					.topCard(Card.KING, Suit.SPADES)
					.pile()
						.add(5, Suit.DIAMONDS).addToParent()
					.addToTableau()
				.stack(3)
					.topCard(8, Suit.HEARTS)
					.pile()
						.add(9, Suit.HEARTS).addToParent()
					.addToTableau()
				.stack(4)
					.topCard(9, Suit.SPADES)
					.addToTableau()
				.stack(5)
					.topCard(4, Suit.SPADES)
					.pile()
						.add(3, Suit.HEARTS)
						.add(2, Suit.SPADES)
						.add(2, Suit.HEARTS)
						.add(Card.QUEEN, Suit.CLUBS)
						.add(Card.JACK, Suit.HEARTS)
						.addToParent()
					.addToTableau()
				.stack(6)
					.topCard(6, Suit.CLUBS)
					.pile()
						.add(2, Suit.CLUBS)
						.add(Card.KING, Suit.DIAMONDS)
						.addToParent()
					.addToTableau()
				.addToBoard()
			.stockPile()
				.add(6, Suit.DIAMONDS)
				.add(7, Suit.HEARTS)
				.add(Card.KING, Suit.CLUBS)
				.add(Card.QUEEN, Suit.SPADES)
				.add(5, Suit.HEARTS)
				.add(Card.ACE, Suit.CLUBS)
				.add(10, Suit.CLUBS)
				.add(Card.QUEEN, Suit.HEARTS)
				.add(Card.KING, Suit.HEARTS)
				.add(Card.JACK, Suit.SPADES)
				.add(4, Suit.DIAMONDS)
				.add(4, Suit.CLUBS)
				.add(3, Suit.CLUBS)
				.add(9, Suit.CLUBS)
				.add(7, Suit.CLUBS)
				.add(5, Suit.CLUBS)
				.add(6, Suit.HEARTS)
				.add(6, Suit.SPADES)
				.add(Card.ACE, Suit.DIAMONDS)
				.add(8, Suit.CLUBS)
				.add(Card.JACK, Suit.DIAMONDS)
				.add(10, Suit.DIAMONDS)
				.add(8, Suit.SPADES)
				.add(Card.ACE, Suit.HEARTS)
				.addToParent()
			.build();
		
		game.moveToFoundation(user3, card(Card.ACE, Suit.SPADES), 4);
		game.discard(user3);
		game.moveToFoundation(user4, card(Card.ACE, Suit.SPADES), 5);
		game.moveToTableau(user3, card(Card.QUEEN, Suit.SPADES), 4);
		game.moveToTableau(user3, card(7, Suit.DIAMONDS), 2);
		game.moveToTableau(user4, card(8, Suit.HEARTS), 4);
		game.moveToTableau(user3, card(9, Suit.DIAMONDS), 3);
		game.discard(user4);
		game.moveToFoundation(user3, card(Card.ACE, Suit.CLUBS), 6);
		game.discard(user3);
		game.discard(user4);
		game.moveToFoundation(user4, card(Card.ACE, Suit.CLUBS), 7);
		game.discard(user3);
		game.moveToTableau(user4, card(5, Suit.HEARTS),6);
		game.discard(user3);
		game.moveToTableau(user4, card(4, Suit.SPADES),6);
		game.discard(user3);
		game.moveToTableau(user4, card(3, Suit.HEARTS),6);
		game.moveToTableau(user3, card(6, Suit.DIAMONDS), 5);
		game.discard(user3);
		game.moveToFoundation(user4, card(2, Suit.SPADES), 4);
		game.moveToFoundation(user3, card(3, Suit.SPADES), 4);
		game.discard(user4);
		game.discard(user4);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.moveToTableau(user4, card(7, Suit.CLUBS),4);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user4);
		game.discard(user3);
		game.moveToFoundation(user4, card(Card.ACE, Suit.HEARTS), 0);
		game.moveToFoundation(user4, card(2, Suit.HEARTS), 0);
		game.discard(user3);
		game.moveToFoundation(user4, card(3, Suit.HEARTS), 0);
		game.discard(user3);
		game.moveToFoundation(user4, card(4, Suit.HEARTS), 0);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.KING, Suit.SPADES),0);
		game.discard(user3);
		game.discard(user3);
		game.moveToTableau(user3, card(Card.JACK, Suit.HEARTS),4);
		game.moveToTableau(user3, card(10, Suit.CLUBS),4);
		game.moveToTableau(user3, card(8, Suit.CLUBS),4);
		game.moveToTableau(user3, card(Card.KING, Suit.DIAMONDS),2);
		game.moveToTableau(user3, card(5, Suit.CLUBS), 5);
		game.moveToFoundation(user3, card(5, Suit.HEARTS), 0);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.moveToTableau(user3, card(Card.QUEEN, Suit.DIAMONDS),4);
		game.moveToTableau(user3, card(Card.JACK, Suit.CLUBS),4);
		game.discard(user3);
		game.moveToTableau(user3, card(10, Suit.DIAMONDS),4);
		game.discard(user3);
		game.moveToTableau(user3, card(4, Suit.DIAMONDS),5);
		game.moveToTableau(user3, card(3, Suit.DIAMONDS),1);
		game.moveToTableau(user3, card(6, Suit.CLUBS),2);
		game.moveToTableau(user3, card(Card.KING, Suit.CLUBS),3);
		game.moveToFoundation(user3, card(Card.ACE, Suit.HEARTS), 1);
		game.moveToFoundation(user3, card(2, Suit.HEARTS), 1);
		game.moveToFoundation(user3, card(6, Suit.HEARTS), 0);
		game.discard(user3);
		game.discard(user4);
		game.discard(user4);
		game.moveToTableau(user4, card(Card.QUEEN, Suit.HEARTS),0);
		game.moveToTableau(user4, card(Card.JACK, Suit.CLUBS),0);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.moveToFoundation(user3, card(Card.ACE, Suit.DIAMONDS), 2);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.moveToTableau(user3, card(9, Suit.SPADES),3);
		game.discard(user3);
		game.discard(user3);
		game.discard(user4);
		game.discard(user3);
		game.moveToFoundation(user3, card(3, Suit.HEARTS), 1);
		game.discard(user4);
		game.discard(user4);
		game.discard(user4);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.JACK, Suit.DIAMONDS),5);
		game.moveToTableau(user4, card(8, Suit.CLUBS),3);
		game.moveToFoundation(user4, card(Card.ACE, Suit.DIAMONDS), 3);
		game.discard(user4);
		game.discard(user4);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.KING, Suit.CLUBS),1);
		game.discard(user3);
		game.discard(user3);
		game.moveToTableau(user4, card(7, Suit.HEARTS),3);
		game.moveToTableau(user4, card(6, Suit.CLUBS),3);
		game.discard(user3);
		game.moveToFoundation(user4, card(2, Suit.CLUBS), 6);
		game.discard(user3);
		game.discard(user3);
		game.discard(user3);
		game.moveToTableau(user4, card(Card.QUEEN, Suit.CLUBS),6);
		
		game.moveToFoundation(user4, card(4, Suit.SPADES), 4);
		
		//PRINT
		game.getFoundation().prettyPrint();
		userBoard3.getTableau().prettyPrint();
		userBoard3.getDiscardPile().print(3);
		userBoard3.getScore().prettyPrint();
		userBoard4.getTableau().prettyPrint();
		userBoard4.getDiscardPile().print(3);
		userBoard4.getScore().prettyPrint();
	}
	
}
