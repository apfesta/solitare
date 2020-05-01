package com.andrewfesta.doublesolitare.model;

import java.util.ArrayList;
import java.util.List;

import com.andrewfesta.doublesolitare.model.Build.Sequence;

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

	public List<Build> getPile() {
		return pile;
	}

	public void setPile(List<Build> pile) {
		this.pile = pile;
	}
	
}
