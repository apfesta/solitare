package com.andrewfesta.doublesolitare.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.andrewfesta.doublesolitare.DoubleSolitareConfig.DoubleSolitareDebugProperties;
import com.andrewfesta.doublesolitare.exception.GameInProgressException;
import com.andrewfesta.doublesolitare.exception.GameNotFoundException;
import com.andrewfesta.doublesolitare.model.Card;
import com.andrewfesta.doublesolitare.model.GameBoard;
import com.andrewfesta.doublesolitare.model.Suit;
import com.andrewfesta.doublesolitare.model.User;
import com.andrewfesta.doublesolitare.model.UserBoard;
import com.andrewfesta.doublesolitare.model.UserBoard.CanPush;
import com.andrewfesta.doublesolitare.service.impl.SyncService;
import com.andrewfesta.doublesolitare.service.impl.UserService;

@Controller
public class MainController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	
	Map<Integer, GameBoard> games = new HashMap<>();
	AtomicInteger gameIdSequence = new AtomicInteger(0);
	
	@Autowired
	UserService userService;
	
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
			@RequestParam("multiplayer") boolean multiplayer) {
		LOG.trace("POST /api/game?multiplayer={}",multiplayer);
		User user = userService.getUser();
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
			@RequestParam("multiplayer") boolean multiplayer) {
		LOG.trace("POST /api/game?multiplayer={}",multiplayer);
		User user = userService.getUser();
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
						foundationIds.get(s));
			}
		}
		
		int i=0;
		//STOP SHORT of winning
		for (int v=8; v<=10; v++) {
			for (Suit s: Suit.values()) {
				if (i==0) {
					discard(game.getGameId());
				}
				moveToFoundation(game.getGameId(), cardId(v,s), 
						foundationIds.get(s));
				i++;
				if (i==3) {
					i=0;
				}
			}
		}
		
		return game.getUserBoard(user);
	}
	
	@RequestMapping(value="/api/user", method = RequestMethod.POST)
	public @ResponseBody User getUser() {
		LOG.trace("POST /api/user");
		return userService.getUser();
	}
	
	@RequestMapping(value="/api/user/{userId}", method = RequestMethod.PUT)
	public @ResponseBody User putUser(@PathVariable Integer userId, 
			@RequestBody User userUpdate) {
		LOG.trace("PUT /api/user/{}", userId);
		User user = userService.getUser();
		
		//Can only change the User Name
		user.setUsername(userUpdate.getUsername());
		syncService.notifyPlayerRename(getUsersActiveGames(user), user);
		
		return userService.getUser();
	}
		
	@RequestMapping(value="/api/game", method = RequestMethod.GET)
	public @ResponseBody List<Game> getGamesToJoin() {
		return games.entrySet().stream()
			.filter((e)->e.getValue().isMultiPlayer())
			.filter((e)->!e.getValue().isInProgress())
			.filter((e)->!e.getValue().isGameOver())
			.filter((e)->!e.getValue().isExpired())
			.map((entry)->new Game(entry.getKey(), 
					entry.getValue().getCreatedBy(), 
					entry.getValue().getGameName(),
					entry.getValue().getUsers()))
			.collect(Collectors.toList());
	}
	
	private List<GameBoard> getUsersActiveGames(User user) {
		return games.entrySet().stream()
			.filter((e)->e.getValue().isMultiPlayer())
			.filter((e)->!e.getValue().isInProgress())
			.filter((e)->!e.getValue().isGameOver())
			.filter((e)->e.getValue().getUsers().contains(user))
			.map(Map.Entry::getValue)
			.collect(Collectors.toList());
	}
	
	@RequestMapping(value="/api/game/{gameId}", method = RequestMethod.GET)
	public @ResponseBody Game getGame(@PathVariable Integer gameId) {
		LOG.trace("GET /api/game/{}", gameId);
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		return new Game(game.getGameId(), 
				game.getCreatedBy(), 
				game.getGameName(), 
				game.getUsers());
	}
	
	@RequestMapping(value="/api/game/{gameId}", method = RequestMethod.PUT)
	public @ResponseBody Game putGame(@PathVariable Integer gameId, 
			@RequestBody Game game) {
		LOG.trace("PUT /api/game/{}", gameId);
		GameBoard existingGame = games.get(gameId);
		assertGameNotNull(gameId, existingGame);
		User user = userService.getUser();
		
		//Can only change the Game Name
		existingGame.setGameName(game.getGameName());
		syncService.notifyGameRename(existingGame, user);
		
		return new Game(existingGame);
	}
	
	@RequestMapping(value="/api/game/{gameId}/join", method = RequestMethod.POST)
	public @ResponseBody UserBoard joinGame(@PathVariable Integer gameId) {
		LOG.trace("POST /api/game/{}/join", gameId);
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		
		if (game.isInProgress()) {
			throw new GameInProgressException("Game "+gameId+" is already in progress");
		}
		if (game.isGameOver() || game.isExpired()) {
			throw new GameNotFoundException("Game "+gameId+" is over");
		}
		if (!game.isMultiPlayer()) {
			throw new GameInProgressException("Game "+gameId+" is not a multi-player game");
		}
		
		User user = userService.getUser();
		if (!game.getUsers().contains(user)) {
			game.join(user);
			syncService.notifyPlayerJoin(game, user);
		}
		
		return game.getUserBoard(user);		
	}
	
	@RequestMapping(value="/api/game/{gameId}/ready", method = RequestMethod.GET)
	public @ResponseBody void readyStatus(@PathVariable Integer gameId,
			@RequestParam("ready") boolean ready) {
		LOG.trace("GET /api/game/{}/ready?ready=", gameId, ready);
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		game.getUserBoard(user).setUserReady(ready);
		syncService.notifyPlayerStatus(game, user, ready);
		if (game.isReady()) {
			game.start();
		}
	}
	
	@RequestMapping(value="/api/game/{gameId}/leave", method = RequestMethod.GET)
	public @ResponseBody void leaveGame(@PathVariable Integer gameId) {
		LOG.trace("POST /api/game/{}/leave", gameId);
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		game.leave(user);
		if (game.getCreatedBy().equals(user) && game.getUsers().isEmpty()) {
			games.remove(game.getGameId());
		}
		syncService.notifyPlayerDrop(game, user);
	}
	
	@RequestMapping(value="/api/game/{gameId}/end", method = RequestMethod.GET)
	public @ResponseBody void endGame(@PathVariable Integer gameId) {
		LOG.trace("POST /api/game/{}/end", gameId);
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		UserBoard winner = game.end(user);
		
		syncService.notifyGameWon(game, winner.getUser());
	}
	
	@RequestMapping(value="/api/game/{gameId}/canmove/{cardId}", method = RequestMethod.GET)
	public @ResponseBody CanPush canMoveCard(
			@PathVariable Integer gameId, 
			@PathVariable Integer cardId) {
		LOG.trace("GET /api/game/{}/canmove/{}", gameId, cardId);
		
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		Card card = game.lookupCard(user, cardId);
		
		return game.canPush(user, card);
	}
	
	@RequestMapping(value="/api/game/{gameId}/move/{cardId}/toFoundation/{toFoundationId}", 
			method = RequestMethod.GET)
	public @ResponseBody UserBoard moveToFoundation(@PathVariable Integer gameId, 
			@PathVariable Integer cardId,
			@PathVariable Integer toFoundationId) {
		LOG.trace("GET /api/game/{}/move/{}/toFoundation/{}", gameId, cardId, toFoundationId);
		
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		
		game.moveToFoundation(user, cardId, toFoundationId);
		syncService.notifyMoveToFoundation(game, user, cardId, toFoundationId);
			
		if (game.isUserBlocked(user)) {
			game.userBlocked(user, false);
			syncService.notifyBlocked(game, user, false);
		}
		
		if (game.getUserBoard(user).isGameWon()) {
			syncService.notifyGameWon(game, user);
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
			@PathVariable Integer toBuildId) {
		LOG.trace("GET /api/game/{}/move/{}/toTableau/{}", gameId, cardId, toBuildId);
		
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		
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
			@PathVariable Integer gameId) {
		LOG.trace("GET /api/game/{}/discard", gameId);
		
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		
		game.discard(user);
		syncService.notifyDiscard(game, user);
		
		game.getDiscardPile(user).print(3);
		game.getUserBoard(user).getScore().prettyPrint();
		
		return game.getUserBoard(user);
	}
	
	@RequestMapping(value="/api/game/{gameId}/toggle", method = RequestMethod.GET,
			params= {"blocked"})
	public @ResponseBody void toggleBlocked(@PathVariable Integer gameId, 
			@RequestParam("blocked") boolean blocked) {
		LOG.trace("GET /api/game/{}/toggle?blocked={}", gameId, blocked);
		
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		
		game.userBlocked(user, blocked);
		syncService.notifyBlocked(game, user, blocked);
	}
	
	@RequestMapping(value="/api/game/{gameId}/toggle", method = RequestMethod.GET,
			params= {"sleep"})
	public @ResponseBody void toggleSleep(@PathVariable Integer gameId, 
			@RequestParam("sleep") boolean sleep) {
		LOG.trace("GET /api/game/{}/toggle?sleep={}", gameId, sleep);
		
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		
		syncService.notifyPlayerSleep(game, user, sleep);
	}
	
	@RequestMapping(value="/api/game/{gameId}/chat", method = RequestMethod.POST)
	public @ResponseBody void chat(@PathVariable Integer gameId,
			@RequestBody Message message) {
		LOG.trace("POST /api/game/{}/chat", gameId);
		
		GameBoard game = games.get(gameId);
		assertGameNotNull(gameId, game);
		User user = userService.getUser();
		
		syncService.notifyGameChat(game, user, message.message);
	}
	
	@RequestMapping(value="/api/log", method = RequestMethod.POST)
	public @ResponseBody void clientEventLog(@RequestBody ClientEventLog event) {
		LOG.trace("POST /api/log");
		LOG.debug(event.toString());
	}
	
	private void assertGameNotNull(Integer gameId, GameBoard game) {
		if (game==null) {
			throw new GameNotFoundException("Game "+gameId+" not found");
		}
	}
		
	public static class Message {
		String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	public static class Game {
		Integer gameId;
		String gameName;
		Collection<User> users;
		User startedBy;

		public Game() {
			super();
		}
		public Game(GameBoard gameBoard) {
			this(gameBoard.getGameId(), gameBoard.getCreatedBy(), 
					gameBoard.getGameName(), gameBoard.getUsers());
		}
		public Game(Integer gameId, User startedBy, String gameName, Collection<User> users) {
			super();
			this.gameId = gameId;
			this.startedBy = startedBy;
			this.gameName = gameName;
			this.users = users;
		}

		public Integer getGameId() {
			return gameId;
		}

		public void setGameId(Integer gameId) {
			this.gameId = gameId;
		}

		public String getGameName() {
			return gameName;
		}

		public void setGameName(String gameName) {
			this.gameName = gameName;
		}

		public void setUsers(Collection<User> users) {
			this.users = users;
		}

		public void setStartedBy(User startedBy) {
			this.startedBy = startedBy;
		}

		public Collection<User> getUsers() {
			return users;
		}

		public User getStartedBy() {
			return startedBy;
		}
	}
	
	public static class ClientEventLog {
		String version;
		String event;
		Integer userId;
		Integer gameId;
		Map<String,String> state;
		
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getEvent() {
			return event;
		}
		public void setEvent(String event) {
			this.event = event;
		}
		public Integer getUserId() {
			return userId;
		}
		public void setUserId(Integer userId) {
			this.userId = userId;
		}
		public Integer getGameId() {
			return gameId;
		}
		public void setGameId(Integer gameId) {
			this.gameId = gameId;
		}
		public Map<String, String> getState() {
			return state;
		}
		public void setState(Map<String, String> state) {
			this.state = state;
		}
		public Map<String,Object> toMap() {
			Map<String,Object> map = new LinkedHashMap<>();
			if (version!=null) map.put("version", version);
			if (event!=null) map.put("event", event);
			if (userId!=null) map.put("userId", userId);
			if (gameId!=null) map.put("gameId", gameId);
			if (state!=null && !state.isEmpty()) map.put("state", state);
			return map;
		}
		@Override
		public String toString() {
			return toMap().toString();
		}
		
	}
			
}
