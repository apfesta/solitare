package com.andrewfesta.doublesolitare.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.andrewfesta.doublesolitare.model.Build.Sequence;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Foundation {

	List<Build> pile;
	List<Lock> locks;

	public Foundation() {
		super();
		pile = new ArrayList<>(4);
		locks = new ArrayList<>(4);
	}
	
	public void addPlayer() {
		for (int i=0; i<4; i++) {
			pile.add(new Build(Sequence.RANK));
			locks.add(new ReentrantLock());
		}
	}
	
	public List<Build> getPile() {
		return pile;
	}

	public void setPile(List<Build> pile) {
		this.pile = pile;
	}
	
	@JsonIgnore
	public List<Lock> getLocks() {
		return locks;
	}

	public void setLocks(List<Lock> locks) {
		this.locks = locks;
	}

	public String getPrettyPrint() {
		StringBuffer buffer= new StringBuffer("F: ");
		
		List<Integer> heights = new ArrayList<>(7);
		for (int i=0; i<pile.size(); i++) {
			heights.add(pile.get(i).size());
		}
		int max = Collections.max(heights);
		
		if (max==0) {
			//Empty
			buffer.append("|");
			for (int i=0; i<pile.size(); i++) {
				buffer.append("     ")
					.append("|");
			}
			buffer.append("\n");
		} else {
			//Not empty
			for (int j=0; j<max; j++) {
				buffer.append("|");
				for (int i=0; i<pile.size(); i++) {
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
		return buffer.toString();
	}
	
	public void prettyPrint() {
		System.out.println(getPrettyPrint());
	}
	
}
