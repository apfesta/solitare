package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.Iterator;

public class Build extends VisiblePile {

	enum Sequence {
		ALTERNATE_COLOR,
		RANK
	}
	
	private final Sequence sequence;

	public Build(Sequence sequence) {
		super();
		this.sequence = sequence;
	}

	public Sequence getSequence() {
		return sequence;
	}
	
	public boolean canPush(Card card) {
		if (!cards.isEmpty()) {
			Card top = cards.peek();
			if (Sequence.ALTERNATE_COLOR.equals(sequence)) {
				if (!top.getColor().equals(card.getColor())) {
					return (top.getValue()-1 == card.getValue());
				}
			} else {
				if (top.getSuit().equals(card.getSuit())) {
					return (top.getValue()+1 == card.getValue());
				}
			}
			return false;
		}
		return Sequence.ALTERNATE_COLOR.equals(sequence) && card.getValue()==Card.KING || 
				Sequence.RANK.equals(sequence) && card.getValue()==Card.ACE;
	}
	
	private boolean canMove(Card card) {
		return (card.getCurrentBuild()!=null && //Is in a build
				card.getCurrentBuild().contains(card)) ||
				(card.getCurrentPile()!=null &&
				card.getCurrentPile().contains(card));  //Is top of the discard pile
	}
		
	public Build popBuild(Card card) {
		//pop all cards until you get to c
		ArrayDeque<Card> temp = new ArrayDeque<>();
		Card c;
		do {
			c = this.cards.pop();
			temp.push(c);
		} while (!c.equals(card));
		Build b = new Build(this.sequence);
		for (Iterator<Card> it= temp.descendingIterator(); it.hasNext();) {
			b.cards.push(it.next());
		}
		return b;
	}

	@Override
	public void push(Card c) {
		if (canPush(c)) {
			if (canMove(c)) {
				if (c.getCurrentBuild()!=null && !c.equals(c.getCurrentBuild().peek())) {
					throw new RuntimeException("Card "+c+" is not on top of its build.  Use push(Build) instead.");
				}
				if (c.getCurrentPile()!=null && !c.equals(c.getCurrentPile().peek())) {
					throw new RuntimeException("Card "+c+" is not on top of its pile.");
				}
				
				if (c.getCurrentBuild()!=null) c.getCurrentBuild().pop();
				else if (c.getCurrentPile()!=null) c.getCurrentPile().pop();
				
				super.push(c);
				
				c.setCurrentBuild(this);
				c.setCurrentPile(null);
			} else {
				throw new RuntimeException("Card "+c+" cannot move.");
			}
		} else {
			throw new RuntimeException("Cannot place card "+c+"on top of "+cards.peek()+" for this type of build");
		}
	}
	
	public void push(Build b, Card bottomCard) {
		boolean inBuild = false;
		for (Iterator<Card> it = b.cards.descendingIterator(); it.hasNext();) {
			//Find bottom card to start a sub-build
			
			Card c = it.next();
			if (!inBuild && !bottomCard.equals(c)) {
				continue;
			}
			if (!inBuild) inBuild = true;
			
			//start pushing cards
			if (canPush(c)) {
				if (canMove(c)) {
					b.cards.remove(c);
					
					super.push(c);
					
					c.setCurrentBuild(this);
				} else {
					throw new RuntimeException("Card "+c+" cannot move.");
				}
			}
		} 
	}
	
}
