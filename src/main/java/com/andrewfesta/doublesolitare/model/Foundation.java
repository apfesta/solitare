package com.andrewfesta.doublesolitare.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.andrewfesta.doublesolitare.model.Build.Sequence;

public class Foundation {

	List<Build> pile;

	public Foundation() {
		super();
		pile = new ArrayList<>(4);
	}
	
	public void addPlayer() {
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
	
	public void prettyPrint() {
		StringBuffer buffer= new StringBuffer("F: ");
		
		List<Integer> heights = new ArrayList<>(7);
		for (int i=0; i<4; i++) {
			heights.add(pile.get(i).size());
		}
		int max = Collections.max(heights);
		
		if (max==0) {
			//Empty
			buffer.append("|");
			for (int i=0; i<4; i++) {
				buffer.append("     ")
					.append("|");
			}
			buffer.append("\n");
		} else {
			//Not empty
			for (int j=0; j<max; j++) {
				buffer.append("|");
				for (int i=0; i<4; i++) {
					if (pile.get(i).size()>j) {
						Card c = pile.get(i).peekFromBottom(j);
						buffer.append(" ").append(c.abbrev()).append(" ");
					} else {
						buffer.append("     ");
					}
					buffer.append("|");
				}
				buffer.append("\n");
				if (j+1<max) {
					buffer.append("   ");
				}
			}
		}
		
		System.out.println(buffer.toString());
	}
	
}
