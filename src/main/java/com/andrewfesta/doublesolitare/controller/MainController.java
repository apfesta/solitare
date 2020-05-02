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
	
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toFoundation/{toFoundationId}", method = RequestMethod.GET)
	public @ResponseBody void moveToFoundation(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toFoundationId) {
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		game.getFoundation().getPile().get(toFoundationId).push(card);
		
		game.getFoundation().prettyPrint();
		game.getTableau().prettyPrint();
		game.getDiscardPile().print(3);
	}
	
	/**
	 * Move card from discard pile or another tableau build.
	 * 
	 * @param gameId
	 * @param cardId
	 * @param toBuildId
	 */
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toTableau/{toBuildId}", method = RequestMethod.GET)
	public @ResponseBody void moveToTableau(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toBuildId) {
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		game.getTableau().getBuild()[toBuildId].push(card);
		
		game.getFoundation().prettyPrint();
		game.getTableau().prettyPrint();
		game.getDiscardPile().print(3);
	}
	
	@RequestMapping(value="/api/game/{gameId}/flip/tableau/{pileId}", method = RequestMethod.GET)
	public @ResponseBody void flipCard(@PathVariable Integer gameId, 
			@PathVariable Integer pileId) {
		GameBoard game = getGame(gameId);
		game.getTableau().flipTopPileCard(pileId);
		
		game.getFoundation().prettyPrint();
		game.getTableau().prettyPrint();
		game.getDiscardPile().print(3);
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
