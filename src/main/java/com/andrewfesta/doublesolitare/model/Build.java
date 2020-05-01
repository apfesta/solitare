package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.Iterator;

public class Build extends Pile {

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
	
	public boolean canMove(Card card) {
		return card.getCurrentLocation()!=null && //Is in a build
				card.getCurrentLocation().contains(card); 
	}
	
	public boolean canPush(Build build) {
		if (Sequence.ALTERNATE_COLOR.equals(sequence)) {
			//can only move builds onto alt-color builds
			return canPush(build.peekFromBottom(0));
		} else {
			return false;
		}
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
				if (!c.equals(c.getCurrentLocation().peek())) {
					throw new RuntimeException("Card "+c+" is not on top of its build.  Use push(Build) instead.");
				}
				c.getCurrentLocation().pop();
				super.push(c);
				c.setCurrentLocation(this);
			} else {
				throw new RuntimeException("Card "+c+" cannot move.");
			}
		} else {
			throw new RuntimeException("Cannot place card "+c+"on top of "+cards.peek()+" for this type of build");
		}
	}
	
	public void push(Build b) {
		for (Iterator<Card> it= b.cards.descendingIterator(); it.hasNext();) {
			push(it.next());
		}
	}
	
	
}
