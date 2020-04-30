package com.andrewfesta.doublesolitare;

import java.util.ArrayList;
import java.util.List;

import com.andrewfesta.doublesolitare.Build.Sequence;

public class Foundation {

	List<Build> pile;

	public Foundation() {
		super();
		pile = new ArrayList<>();
	}
	
	public void newFoundationPile(Card c) {
		if (c.getValue()==Card.ACE) {
			Build b = new Build(Sequence.RANK);
			b.push(c);
		}
	}
	
}
