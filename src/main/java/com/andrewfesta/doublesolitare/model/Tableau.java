package com.andrewfesta.doublesolitare.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tableau {
	
	Pile[] pile = new Pile[7];
	Build[] build = new Build[7];

	public Tableau() {
		super();
		for (int i =0; i<7; i++) {
			pile[i] = new Pile();
			build[i] = new Build(Build.Sequence.ALTERNATE_COLOR);
		}
	}
	
	
	public Pile setup(Deck d) {	
		//Deal out Tableau
		for (int j =0; j<7; j++) {
			for (int i =j; i<7; i++) {
				Card c = d.pop();
				pile[i].push(c);
			}
		}
		//Turn over top cards for builds
		for (int i=0; i<7; i++) {
			Card c = pile[i].cards.pop();
			build[i].cards.push(c);
			c.setCurrentBuild(build[i]);
		}
		
		//return leftover cards
		Pile p = new Pile(d);
		p.setToStringPrefix("S");
		for (Card c: d.cards) {
			c.setCurrentPile(p);
		}
		return p;
	}
	
		
	public Card flipTopPileCard(int pileId) {
		if (build[pileId].isEmpty()) {
			if (!pile[pileId].isEmpty()) {
				Card c = pile[pileId].cards.pop();
				build[pileId].cards.push(c);
				c.setCurrentBuild(build[pileId]);
				return c;
			}
			throw new RuntimeException("Cannot flip card because there's nothing to flip");
		} else {
			throw new RuntimeException("Cannot flip card because there's a build on top of this pile");
		}
	}
	
	public Pile[] getPile() {
		return pile;
	}


	public void setPile(Pile[] pile) {
		this.pile = pile;
	}


	public Build[] getBuild() {
		return build;
	}


	public void setBuild(Build[] build) {
		this.build = build;
	}


	public void prettyPrint() {
		StringBuffer buffer= new StringBuffer();
		
		List<Integer> heights = new ArrayList<>(7);
		for (int i=0; i<7; i++) {
			heights.add(pile[i].size()+build[i].size());
		}
		int max = Collections.max(heights);
		
		for (int j=0; j<max; j++) {
			buffer.append("|");
			for (int i=0; i<7; i++) {
				if (pile[i].size()>j) {
					buffer.append(" --- ");
//					Card c = pile[i].peekFromBottom(j);
//					buffer.append(" ").append(c.abbrev()).append(" ");
				} else {
					if (build[i].size()+pile[i].size()>j) {
						Card c = build[i].peekFromBottom(j-pile[i].size());
						buffer.append(" ").append(c.abbrev()).append(" ");
					} else {
						buffer.append("     ");
					}
				}
				buffer.append("|");
			}
			buffer.append("\n");
		}
		System.out.println(buffer.toString());
	}

}
