package com.andrewfesta.doublesolitare.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	private static final Logger GAME_LOG = LoggerFactory.getLogger("GameLog");
	
	Map<Integer, GameBoard> games = new HashMap<>();
	AtomicInteger gameIdSequence = new AtomicInteger(0);

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String displayBoard() {
		return "displayboard";
	}
		
	@RequestMapping(value="/api/game", method = RequestMethod.POST)
	public @ResponseBody GameBoard newGame() {
		LOG.trace("POST /api/game");
		GameBoard game = new GameBoard(gameIdSequence.incrementAndGet());
		game.setShuffle(false);
		game.setup();
		games.put(game.getGameId(), game);
		return game;
	}
	
	@RequestMapping(value="/api/game/{gameId}", method = RequestMethod.GET)
	public @ResponseBody GameBoard getGame(@PathVariable Integer gameId) {
		LOG.trace("GET /api/game/{}", gameId);
		return games.get(gameId);
	}
	
	@RequestMapping(value="/api/game/{gameId}/canmove/{cardId}", method = RequestMethod.GET)
	public @ResponseBody CanPush canMoveCard(
			@PathVariable Integer gameId, 
			@PathVariable Integer cardId) {
		LOG.trace("GET /api/game/{}/canmove/{}", gameId, cardId);
		
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
	
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toFoundation/{toFoundationId}", 
			method = RequestMethod.GET)
	public @ResponseBody GameBoard moveToFoundation(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toFoundationId) {
		LOG.trace("GET /api/game/{}/move/{}/toFoundation/{}", gameId, cardId, toFoundationId);
		
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		Integer pileIdToFlip = getPileIdToFlip(game, card);
		
		GAME_LOG.debug("GameId:{} Move {} to foundation pile {}",
				gameId, card.abbrev(), toFoundationId);
		game.getFoundation().getPile().get(toFoundationId).push(card);
		
		if (pileIdToFlip!=null && !game.getTableau().getPile()[pileIdToFlip].isEmpty()) {
			game.getTableau().flipTopPileCard(pileIdToFlip);
			GAME_LOG.debug("GameId:{} Flip pile {} reveals",
					gameId, pileIdToFlip, card.abbrev());
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
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toTableau/{toBuildId}", 
			method = RequestMethod.GET)
	public @ResponseBody GameBoard moveToTableau(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toBuildId) {
		LOG.trace("GET /api/game/{}/move/{}/toTableau/{}", gameId, cardId, toBuildId);
		
		GameBoard game = getGame(gameId);
		Card card = game.lookupCard(cardId);
		Integer pileIdToFlip = getPileIdToFlip(game, card);
		
		if (card.getCurrentBuild()!=null && !card.equals(card.getCurrentBuild().peek())) {
			//Move Build of cards from pile to pile
			GAME_LOG.debug("GameId:{} Move build ({}-{}) to tableau pile {}",
					gameId, card.abbrev(), card.getCurrentBuild().peek().abbrev(), toBuildId);
			game.getTableau().getBuild()[toBuildId].push(card.getCurrentBuild(), card);
		} else {
			//Move card from pile or discard pile to tableau pile
			GAME_LOG.debug("GameId:{} Move {} to tableau pile {}",
					gameId, card.abbrev(), toBuildId);
			game.getTableau().getBuild()[toBuildId].push(card);
		}
		
		if (pileIdToFlip!=null && !game.getTableau().getPile()[pileIdToFlip].isEmpty()) {
			game.getTableau().flipTopPileCard(pileIdToFlip);
			GAME_LOG.debug("GameId:{} Flip pile {} reveals {}",
					gameId, pileIdToFlip, card.abbrev());
		}
				
		game.getFoundation().prettyPrint();
		game.getTableau().prettyPrint();
		game.getDiscardPile().print(3);
		
		return game;
	}
	
	@RequestMapping(value="/api/game/{gameId}/discard", method = RequestMethod.GET)
	public @ResponseBody GameBoard discard(@PathVariable Integer gameId) {
		LOG.trace("GET /api/game/{}/discard", gameId);
		
		GameBoard game = getGame(gameId);
		
		GAME_LOG.debug("GameId:{} discard",
				gameId);
		game.discard(3);
		
		game.getDiscardPile().print(3);
		
		return game;
	}
			
}
