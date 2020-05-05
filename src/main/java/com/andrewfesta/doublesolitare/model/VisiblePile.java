package com.andrewfesta.doublesolitare.model;

import java.util.Collection;
import java.util.Collections;

public class VisiblePile extends Pile {

	public VisiblePile() {
		super();
	}

	public Collection<Card> getCards() {
		return Collections.unmodifiableCollection(cards);
	}
		
}
