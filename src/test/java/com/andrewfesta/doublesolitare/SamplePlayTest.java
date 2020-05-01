package com.andrewfesta.doublesolitare;

import static org.junit.Assert.assertEquals;

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
	public void samplePlay() {
		
		GameBoard game = new GameBoard(1);
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
		assertEquals(true, canPush.getFoundationPile()[toFoundationId]);
		assertEquals(false, canPush.getTableauBuild()[1]);
		foundation.getPile().get(toFoundationId).push(card);
		foundation.prettyPrint();
		tableau.prettyPrint();
		
		//MOVE #2 - Move ACE of SPADES
		fromBuildId = 2;
		toFoundationId = 1;
		build = tableau.getBuild()[fromBuildId];
		card = build.peek();
		assertEquals(Suit.HEARTS,card.getSuit());
		assertEquals(Card.ACE, card.getValue());
		canPush = game.canPush(card);
		assertEquals(true, canPush.getFoundationPile()[toFoundationId]);
		assertEquals(false, canPush.getTableauBuild()[1]);
		assertEquals(false, canPush.getTableauBuild()[0]);
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
		assertEquals(true, canPush.getTableauBuild()[toBuildId]);
		assertEquals(false, canPush.getTableauBuild()[1]);
		assertEquals(false, canPush.getFoundationPile()[0]);
		assertEquals(false, canPush.getFoundationPile()[2]);		
		tableau.getBuild()[toBuildId].push(card);
		//MOVE #2B - Flip card
		tableau.flipTopPileCard(fromBuildId);
		
		foundation.prettyPrint();
		tableau.prettyPrint();
				

	}
	
}
