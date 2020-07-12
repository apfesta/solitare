package com.andrewfesta.doublesolitare.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class VisiblePile extends Pile {

	public VisiblePile() {
		super();
	}

	public Collection<Card> getCards() {
		return Collections.unmodifiableCollection(cards);
	}
	
	public void print(int maxNumberOfCards) {
		List<Card> readOnly = asList();
		StringBuffer buffer = new StringBuffer(prefix()).append(": (").append(readOnly.size()).append(")");
		for (int i = 0; i<maxNumberOfCards && i<readOnly.size(); i++) {
			buffer.append(readOnly.get(i).abbrev()).append(" | ");
		}
		buffer.append("\n");
		System.out.println(buffer.toString());
	}
		
}
