package com.andrewfesta.doublesolitare.model;

import java.util.ArrayList;
import java.util.List;

import com.andrewfesta.doublesolitare.model.Build.Sequence;

public class Foundation {

	List<Build> pile;

	public Foundation() {
		super();
		pile = new ArrayList<>(4);
		for (int i=0; i<4; i++) {
			pile.add(new Build(Sequence.RANK));
		}
	}
	
	public List<Build> getPile() {
		return pile;
	}

	public void setPile(List<Build> pile) {
		this.pile = pile;
	}
	
}
