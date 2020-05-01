package com.andrewfesta.doublesolitare.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.andrewfesta.doublesolitare.model.Build;
import com.andrewfesta.doublesolitare.model.Card;
import com.andrewfesta.doublesolitare.model.GameBoard;
import com.andrewfesta.doublesolitare.model.Pile;

@Controller
public class MainController {
	
	Map<Integer, GameBoard> games = new HashMap<>();
	AtomicInteger gameIdSequence = new AtomicInteger(0);

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String displayBoard() {
		return "displayboard";
	}
	
	//TODO - totalnonsense.com vectorized playing cards in svg
	
	//Move Card around tablau
	
	//Move card from stockpile
	
	//Move card to foundation
	
	@RequestMapping(value="/api/game", method = RequestMethod.POST)
	public @ResponseBody GameBoard newGame() {
		GameBoard game = new GameBoard(gameIdSequence.incrementAndGet());
		game.setup();
		games.put(game.getGameId(), game);
		return game;
	}
	
	@RequestMapping(value="/api/game/{gameId}", method = RequestMethod.GET)
	public @ResponseBody GameBoard getGame(@PathVariable Integer gameId) {
		return games.get(gameId);
	}
	
	@RequestMapping(value="/api/game/{gameId}/canmove/{cardId}", method = RequestMethod.GET)
	public @ResponseBody CanDrop canMoveCard(
			@PathVariable Integer gameId, 
			@PathVariable Integer cardId) {
		
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		
		CanDrop canDrop = new CanDrop();
		
		Pile[] facedown = game.getTableau().getPile();
		Build[] tableauBuild = game.getTableau().getBuild();
		
		boolean isFacedown = false;
		for (Pile pile: facedown) {
			if (pile.contains(card)) {
				isFacedown = true;
			}
		}
		for (int i=0; i<tableauBuild.length; i++) {
			canDrop.tableauBuild[i] = !isFacedown && tableauBuild[i].canPush(card);
		}
		List<Build> foundationBuld = game.getFoundation().getPile();
		for (int i=0; i<foundationBuld.size(); i++) {
			canDrop.foundationPile[i] = !isFacedown && foundationBuld.get(i).canPush(card);
		}

		return canDrop;
	}
	
	public static class CanDrop {
		private Boolean[] foundationPile = new Boolean[4];
		private Boolean[] tableauBuild = new Boolean[7];
		public Boolean[] getFoundationPile() {
			return foundationPile;
		}
		public void setFoundationPile(Boolean[] foundationPile) {
			this.foundationPile = foundationPile;
		}
		public Boolean[] getTableauBuild() {
			return tableauBuild;
		}
		public void setTableauBuild(Boolean[] tableauBuild) {
			this.tableauBuild = tableauBuild;
		}
	}
	
//	static class ObjectWrapper<T> {
//		private final T value;
//
//		public ObjectWrapper(T value) {
//			super();
//			this.value = value;
//		}
//
//		public T getValue() {
//			return value;
//		}
//		
//		static <E> ObjectWrapper<E> wrap(E value) {
//			return new ObjectWrapper<E>(value);
//		}
//		
//	}
	
	
}
