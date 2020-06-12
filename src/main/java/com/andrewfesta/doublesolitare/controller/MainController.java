package com.andrewfesta.doublesolitare.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.andrewfesta.doublesolitare.DoubleSolitareConfig.DoubleSolitareDebugProperties;
import com.andrewfesta.doublesolitare.model.Card;
import com.andrewfesta.doublesolitare.model.GameBoard;
import com.andrewfesta.doublesolitare.model.Suit;
import com.andrewfesta.doublesolitare.model.User;
import com.andrewfesta.doublesolitare.model.UserBoard;
import com.andrewfesta.doublesolitare.model.UserBoard.CanPush;
import com.andrewfesta.doublesolitare.service.impl.SyncService;

@Controller
public class MainController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	
	Map<Integer, GameBoard> games = new HashMap<>();
	AtomicInteger gameIdSequence = new AtomicInteger(0);
	Map<Integer, User> users = new HashMap<>();
	AtomicInteger userIdSequence = new AtomicInteger(0);
	
	@Autowired
	SyncService syncService;
	
	@Autowired
	DoubleSolitareDebugProperties debugProperties;

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String displayBoard() {
		return "displayboard";
	}
		
	@RequestMapping(value="/api/game", method = RequestMethod.POST)
	public @ResponseBody UserBoard newGame(
			@RequestParam("multiplayer") boolean multiplayer,
			@RequestParam("userId") Integer userId) {
		LOG.trace("POST /api/game?multiplayer={}",multiplayer);
		User user = users.get(userId);
		GameBoard game = new GameBoard(user, gameIdSequence.incrementAndGet(), multiplayer);
		if (debugProperties!=null) {
			game.setDebugProperties(debugProperties);
		}
		game.setup(user);
		games.put(game.getGameId(), game);
		
		return game.getUserBoard(user);
	}
	
	private int cardId(int value, Suit suit) {
		return Card.unicodeInt(value, suit);
	}
	
	/**
	 * Testing mode to fast forward to the end.
	 * 
	 * @param multiplayer
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/api/game/test", method = RequestMethod.POST)
	public @ResponseBody UserBoard newTestGame(
			@RequestParam("multiplayer") boolean multiplayer,
			@RequestParam("userId") Integer userId) {
		LOG.trace("POST /api/game?multiplayer={}",multiplayer);
		User user = users.get(userId);
		GameBoard game = new GameBoard(user, gameIdSequence.incrementAndGet(), multiplayer);
		if (debugProperties!=null) {
			game.setDebugProperties(debugProperties);
		}
		game.setupTest(user);
		games.put(game.getGameId(), game);
		
		Map<Suit, Integer> foundationIds = new HashMap<>();
		int f = 0;
		for (int v=Card.ACE; v<=7; v++) {
			for (Suit s: Suit.values()) {
				if (!foundationIds.containsKey(s)) {
					foundationIds.put(s, f++);
				}
				moveToFoundation(game.getGameId(), cardId(v,s), 
						foundationIds.get(s), userId);
			}
		}
		
		int i=0;
		//STOP SHORT of winning
		for (int v=8; v<=10; v++) {
			for (Suit s: Suit.values()) {
				if (i==0) {
					discard(game.getGameId(), userId);
				}
				moveToFoundation(game.getGameId(), cardId(v,s), 
						foundationIds.get(s), userId);
				i++;
				if (i==3) {
					i=0;
				}
			}
		}
		
		return game.getUserBoard(user);
	}
	
	@RequestMapping(value="/api/user", method = RequestMethod.POST)
	public @ResponseBody User newTestUser() {
		LOG.trace("POST /api/user");
		User user = new User(userIdSequence.incrementAndGet());
		users.put(user.getId(), user);
		return user;
	}
	
	@RequestMapping(value="/api/game", method = RequestMethod.GET)
	public @ResponseBody List<Game> getGamesToJoin() {
		return games.entrySet().stream()
			.filter((e)->e.getValue().isMultiPlayer())
			.filter((e)->!e.getValue().isInProgress())
			.filter((e)->!e.getValue().isGameOver())
			.map((entry)->new Game(entry.getKey(), entry.getValue().getCreatedBy(), entry.getValue().getUsers()))
			.collect(Collectors.toList());
	}
	
	@RequestMapping(value="/api/game/{gameId}", method = RequestMethod.GET)
	public @ResponseBody UserBoard getGame(@PathVariable Integer gameId,
			@RequestParam("userId") Integer userId) {
		LOG.trace("GET /api/game/{}", gameId);
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		return game.getUserBoard(user);
	}
	
	@RequestMapping(value="/api/game/{gameId}/join", method = RequestMethod.POST)
	public @ResponseBody UserBoard joinGame(@PathVariable Integer gameId,
			@RequestParam("userId") Integer userId) {
		LOG.trace("POST /api/game/{}/join", gameId);
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		game.join(user);
		syncService.notifyPlayerJoin(game, user);
		
		return game.getUserBoard(user);
	}
	
	@RequestMapping(value="/api/game/{gameId}/ready", method = RequestMethod.GET)
	public @ResponseBody void readyStatus(@PathVariable Integer gameId,
			@RequestParam("userId") Integer userId,
			@RequestParam("ready") boolean ready) {
		LOG.trace("GET /api/game/{}/ready?ready=", gameId, ready);
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		game.getUserBoard(user).setUserReady(ready);
		syncService.notifyPlayerStatus(game, user, ready);
		if (game.isReady()) {
			game.setInProgress(true);
		}
	}
	
	@RequestMapping(value="/api/game/{gameId}/leave", method = RequestMethod.GET)
	public @ResponseBody void leaveGame(@PathVariable Integer gameId,
			@RequestParam("userId") Integer userId) {
		LOG.trace("POST /api/game/{}/leave", gameId);
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		game.leave(user);
		syncService.notifyPlayerDrop(game, user);
		if (game.getUsers().isEmpty()) {
			games.remove(game.getGameId());
		}
	}
	
	@RequestMapping(value="/api/game/{gameId}/canmove/{cardId}", method = RequestMethod.GET)
	public @ResponseBody CanPush canMoveCard(
			@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@RequestParam("userId") Integer userId) {
		LOG.trace("GET /api/game/{}/canmove/{}", gameId, cardId);
		
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		Card card = game.lookupCard(user, cardId);
		
		return game.canPush(user, card);
	}
	
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toFoundation/{toFoundationId}", 
			method = RequestMethod.GET)
	public @ResponseBody UserBoard moveToFoundation(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toFoundationId, 
			@RequestParam("userId") Integer userId) {
		LOG.trace("GET /api/game/{}/move/{}/toFoundation/{}", gameId, cardId, toFoundationId);
		
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		
		game.moveToFoundation(user, cardId, toFoundationId);
		syncService.notifyMoveToFoundation(game, user, cardId, toFoundationId);
		
		if (game.isUserBlocked(user)) {
			game.userBlocked(user, false);
			syncService.notifyBlocked(game, user, false);
		}
		
		game.getFoundation().prettyPrint();
		game.getTableau(user).prettyPrint();
		game.getDiscardPile(user).print(3);
		game.getUserBoard(user).getScore().prettyPrint();
		
		return game.getUserBoard(user);
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
	public @ResponseBody UserBoard moveToTableau(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toBuildId, 
			@RequestParam("userId") Integer userId) {
		LOG.trace("GET /api/game/{}/move/{}/toTableau/{}", gameId, cardId, toBuildId);
		
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		
		game.moveToTableau(user, cardId, toBuildId);
		syncService.notifyMoveToTableau(game, user, cardId, toBuildId);
		
		if (game.isUserBlocked(user)) {
			game.userBlocked(user, false);
			syncService.notifyBlocked(game, user, false);
		}
						
		game.getFoundation().prettyPrint();
		game.getTableau(user).prettyPrint();
		game.getDiscardPile(user).print(3);
		game.getUserBoard(user).getScore().prettyPrint();
		
		return game.getUserBoard(user);
	}
	
	@RequestMapping(value="/api/game/{gameId}/discard", method = RequestMethod.GET)
	public @ResponseBody UserBoard discard(
			@PathVariable Integer gameId, 
			@RequestParam("userId") Integer userId) {
		LOG.trace("GET /api/game/{}/discard", gameId);
		
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		
		game.discard(user);
		syncService.notifyDiscard(game, user);
		
		game.getDiscardPile(user).print(3);
		game.getUserBoard(user).getScore().prettyPrint();
		
		return game.getUserBoard(user);
	}
	
	@RequestMapping(value="/api/game/{gameId}/toggle", method = RequestMethod.GET)
	public @ResponseBody void toggleBlocked(@PathVariable Integer gameId, 
			@RequestParam("userId") Integer userId,
			@RequestParam("blocked") boolean blocked) {
		LOG.trace("GET /api/game/{}/toggle?blocked={}", gameId, blocked);
		
		GameBoard game = games.get(gameId);
		User user = users.get(userId);
		
		game.userBlocked(user, blocked);
		syncService.notifyBlocked(game, user, blocked);
	}
	
	public static class Game {
		Integer gameId;
		Collection<User> users;
		User startedBy;

		public Game(Integer gameId, User startedBy, Collection<User> users) {
			super();
			this.gameId = gameId;
			this.startedBy = startedBy;
			this.users = users;
		}

		public Integer getGameId() {
			return gameId;
		}

		public void setGameId(Integer gameId) {
			this.gameId = gameId;
		}

		public Collection<User> getUsers() {
			return users;
		}

		public void setUsers(List<User> users) {
			this.users = users;
		}

		public User getStartedBy() {
			return startedBy;
		}
	}
			
}
