package com.andrewfesta.doublesolitare.model;

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
		return Sequence.ALTERNATE_COLOR.equals(sequence) || 
				Sequence.RANK.equals(sequence) && card.getValue()==Card.ACE;
	}
	
	public boolean canPush(Build build) {
		if (Sequence.ALTERNATE_COLOR.equals(sequence)) {
			//can only move builds onto alt-color builds
			return canPush(build.peekFromBottom(0));
		} else {
			return false;
		}
	}

	@Override
	public void push(Card c) {
		if (canPush(c)) {
			super.push(c);
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
