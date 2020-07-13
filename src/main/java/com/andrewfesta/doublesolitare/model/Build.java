package com.andrewfesta.doublesolitare.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewfesta.doublesolitare.exception.PushException;

public class Build extends VisiblePile {
	
	private static final Logger LOG = LoggerFactory.getLogger(Build.class);
	
	enum Sequence {
		ALTERNATE_COLOR,
		RANK
	}
	
	private final Sequence sequence;

	public Build(Sequence sequence) {
		super();
		this.sequence = sequence;
		this.setToStringPrefix("Build");
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
					LOG.error("Card:{} is not on top of build:{}",
							c.abbrev(),c.getCurrentBuild());
					throw new PushException("Card "+c+" is not on top of its build.  Use push(Build) instead.");
				}
				if (c.getCurrentPile()!=null && !c.equals(c.getCurrentPile().peek())) {
					LOG.error("Card:{} is not on top of pile:{}",
							c.abbrev(),c.getCurrentPile());
					throw new PushException("Card "+c+" is not on top of its pile.");
				}
				
				if (c.getCurrentBuild()!=null) c.getCurrentBuild().pop();
				else if (c.getCurrentPile()!=null) c.getCurrentPile().pop();
				
				super.push(c);
				
				c.setCurrentBuild(this);
				c.setCurrentPile(null);
			} else {
				LOG.error("Card:{} Currentbuild:{} Currentpile:{}",
						c.abbrev(),c.getCurrentBuild(), c.getCurrentPile());
				throw new PushException("Card "+c+" cannot move.");
			}
		} else {
			throw new PushException("Cannot place card "+c+" on top of "+cards.peek()+" for this type of build");
		}
	}
	
	public void push(Build b, Card bottomCard) {
		boolean inBuild = false;
		ArrayList<Card> subBuild = new ArrayList<>();
		for (Iterator<Card> it = b.cards.descendingIterator(); it.hasNext();) {
			//Find bottom card to start a sub-build
			
			Card c = it.next();
			if (!inBuild && !bottomCard.equals(c)) {
				continue;
			}
			if (!inBuild) inBuild = true;
			
			//add to list of cards to move
			subBuild.add(c);
		} 
		for (Card c: subBuild) {
			if (canPush(c)) {
				if (canMove(c)) {
					b.cards.remove(c);
					
					super.push(c);
					
					c.setCurrentBuild(this);
				} else {
					throw new PushException("Card "+c+" cannot move.");
				}
			} else {
				throw new PushException("Cannot place card "+c+" on top of "+cards.peek()+" for this type of build");
			}
		}
	}
	
	public static Builder builder(Build build) {
		return new Builder(build);
	}
	
	public static class Builder {
		Build build;
		Map<Integer, Card> cards = new HashMap<>();
		Tableau.StackBuilder stackBuilder;
		
		Builder(Build build) {
			this.build = build;
		}
		public Builder cards(Map<Integer, Card> cards) {
			this.cards = cards;
			return this;
		}
		Builder push(int value, Suit suit) {
			Card c = new Card(value, suit);
			if (cards.containsKey(c.getUnicodeInt())) {
				throw new RuntimeException("Card already exists in deck");
			}
			cards.put(c.getUnicodeInt(), c);
			build.cards.push(c);
			c.setCurrentBuild(build);
			return this;
		}
		
		public Builder stackBuilder(Tableau.StackBuilder stackBuilder) {
			this.stackBuilder = stackBuilder;
			return this;
		}
		
		public Tableau.StackBuilder addToStack() {
			return this.stackBuilder;
		}
	}
	
}
