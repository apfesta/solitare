package com.andrewfesta.doublesolitare.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.andrewfesta.doublesolitare.model.Card;
import com.andrewfesta.doublesolitare.model.GameBoard;
import com.andrewfesta.doublesolitare.model.GameBoard.CanPush;

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
	public @ResponseBody CanPush canMoveCard(
			@PathVariable Integer gameId, 
			@PathVariable Integer cardId) {
		
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		
		return game.canPush(card);
	}
	
	protected Integer getPileIdToFlip(GameBoard game, Card card) {
		Integer pileIdToFlip = null;
		for (int i=0; i<game.getTableau().getPile().length; i++) {
			if (card.getCurrentBuild()==game.getTableau().getBuild()[i]) {
				pileIdToFlip = i;
			}
		}
		return pileIdToFlip;
	}
	
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toFoundation/{toFoundationId}", method = RequestMethod.GET)
	public @ResponseBody GameBoard moveToFoundation(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toFoundationId) {
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		Integer pileIdToFlip = getPileIdToFlip(game, card);
		game.getFoundation().getPile().get(toFoundationId).push(card);
		
		if (pileIdToFlip!=null && !game.getTableau().getPile()[pileIdToFlip].isEmpty()) {
			game.getTableau().flipTopPileCard(pileIdToFlip);
		}
		
		game.getFoundation().prettyPrint();
		game.getTableau().prettyPrint();
		game.getDiscardPile().print(3);
		
		return game;
	}
	
	/**
	 * Move card from discard pile or another tableau build.
	 * 
	 * @param gameId
	 * @param cardId
	 * @param toBuildId
	 */
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toTableau/{toBuildId}", method = RequestMethod.GET)
	public @ResponseBody GameBoard moveToTableau(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toBuildId) {
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		Integer pileIdToFlip = getPileIdToFlip(game, card);
		game.getTableau().getBuild()[toBuildId].push(card);
		
		if (pileIdToFlip!=null) {
			game.getTableau().flipTopPileCard(pileIdToFlip);
		}
				
		game.getFoundation().prettyPrint();
		game.getTableau().prettyPrint();
		game.getDiscardPile().print(3);
		
		return game;
	}
	
	@RequestMapping(value="/api/game/{gameId}/discard", method = RequestMethod.GET)
	public @ResponseBody GameBoard discard(@PathVariable Integer gameId) {
		GameBoard game = getGame(gameId);
		game.discard(3);
		
		game.getDiscardPile().print(3);
		
		return game;
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
