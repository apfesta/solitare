package com.andrewfesta.doublesolitare.model;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class GameBoardTest {

	@Test
	public void test_import() throws IOException {
		GameBoard.GameExport obj = GameBoard.GameExport.readFromFile("game10_user6.json");
		assertNotNull(obj);
		System.out.println("---BACKEND---");
//		for (String line:obj.userBoards.get(obj.generatedByUserId).currentFoundation) {
//			System.out.println(line);
//		}
//		for (String line:obj.userBoards.get(obj.generatedByUserId).currentTableau) {
//			System.out.println(line);
//		}
		System.out.println("---FRONTEND---");
		System.out.println(obj.boardHtml);
		
	}
	
	@Test
	public void test_export() throws IOException {
		User user3 = new User(3);
		GameBoard game = new GameBoard(user3, 3, true);
		game.foundation = new Foundation();
		game.foundation.addPlayer();

		
		/*
		 * SETUP
		 */
		UserBoard.builder(game, user3)
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
		
		game.moveToTableau(user3, card(Card.QUEEN, Suit.DIAMONDS), 6);
		game.discard(user3);
		game.moveToFoundation(user3, card(Card.ACE, Suit.DIAMONDS), 2);
		
		game.export().generatedByUserId(3).build().writeAsFile();
	}
	
	private int card(int value, Suit suit) {
		Card c = new Card(value, suit);
		return c.getUnicodeInt();
	}
	
}
