
$('#board').hide();
$('#scoreBar').hide();
$('#scoreBoard').hide();
$('#blockToggleButton').hide();
$('.editUsername').hide();
$('.editGamename').hide();
$('.editGamenameBtn').hide();
$('#endGameBtn').hide();
$('.chatbar').hide();

var menu = {
		games: []
};


(function() {
	'user strict';
	
	menu.setup = function() {
		menu.getUser();
	}
	
	//---------------
	// AJAX functions
	//---------------
	
	menu.setUser = function(data) {
		app.user = data;
		$('.username').html(app.user.username);
		$('.editUsername [name=usernameInput]').val(app.user.username);
		$('#waitForPlayers .users .me')
			.html(app.user.username+ " <label>I'm Ready: <span class='status'></span><input class='ready checkbox-2x' type='checkbox' data-user-id='"+app.user.id+"' /></label>");
		$('.ready').on('change',app.readyStatusOnChange);
		$('#scoreBoard .user.me')
					.attr('data-user-id',app.user.id)
						.append(
							$('<td>').addClass('username').text(app.user.username))
						.append(
							$('<td>').addClass('status').text(''))
						.append(
							$('<td>').addClass('moves').text('0'));
	};
	menu.getUser = function() {
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/user'),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				menu.setUser(data);
				menu.getGames();
			}});
	};
	menu.updateUser = function(username) {
		var data = {'username':username};
		$.ajax({
			type: 'PUT', 
			url: getRelativePath('/api/user/'+app.user.id),
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(data),
			success: function(data){
				console.debug(data);
				menu.setUser(data);
			}});
	};
	menu.updateGame = function(gamename) {
		var data = {'gameName':gamename};
		$.ajax({
			type: 'PUT', 
			url: getRelativePath('/api/game/'+app.gameId),
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(data),
			success: function(data){
				console.debug(data);
				app.gameName = data.gameName
			}});
	};
	
	menu.getGames = function() {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game'),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				menu.games = data;
				var hash = $(location).attr('hash')
				if (hash.length>0) {
					var gameId = hash.substring(1)
					$('#waitForPlayers').modal({
						  backdrop: 'static',
						  show: true
						});
					app.setup(gameId, true);
				} else {
					menu.showGames();
				}
			}});
	};
	
	//---------------
	// Display functions
	//---------------
	
	menu.showGames = function() {
		$('#board').hide();
		$('#menu').addClass("container");
		
		var newGameAction = function() {
			app.setup(null, false);
		};
		var newMultiplayerGameAction = function() {
			app.setup(null, true);
		};
		var joinGameAction = function() {
			app.setup(game.gameId, true);
		};
		var newTestAction = function() {
			app.setupTest(null, false);
		};
		var newMultiplayerTestAction = function() {
			app.setupTest(null, true);
		};
		
		$('#newSinglePlayerTestAction').on('click', newTestAction);
		$('#newSinglePlayerGameAction').on('click', newGameAction);
		$('#newMultiPlayerTestAction')
			.attr('data-toggle','modal')
			.attr('data-target','#waitForPlayers')
			.attr('data-backdrop',"static")
			.on('click', newMultiplayerTestAction);
		$('#newMultiPlayerGameAction')
			.attr('data-toggle','modal')
			.attr('data-target','#waitForPlayers')
			.attr('data-backdrop',"static")
			.on('click', newMultiplayerGameAction);
					
		$("#availableGamesToJoin").empty();
		for (gameIdx in menu.games) {
			var game = menu.games[gameIdx];
			$("#availableGamesToJoin").append(
					$("<a href='#'>")
						.addClass("list-group-item")
						.addClass("list-group-item-action")
						 .attr('data-toggle','modal')
						 .attr('data-target','#waitForPlayers')
						 .attr('data-backdrop',"static")
						.text(game.gameName+" - started by "+game.startedBy.username)
						.on('click', joinGameAction));
		}
		
	}

})();




var app = {
		user: {},
		userboard: {},
		gameboard: {},
		gameId: null,
		gameName: null,
		canMoveData: null,
		countdownTimer: null
};


(function() {
	'user strict';
	
	
	app.setup = function(gameId, multiplayer) {
		$('#board').show();
		$('#menu').hide();
		if (multiplayer && gameId!=null) {
			app.joinGame(gameId);
		} else if (multiplayer){
			app.newMultiplayerGame();
		} else {
			app.newGame();
		}
	};
	app.setupTest = function(gameId, multiplayer) {
		$('#board').show();
		$('#menu').hide();
		if (multiplayer && gameId!=null) {
			app.joinGame(gameId);
		} else if (multiplayer){
			app.newMultiplayerTest();
		} else {
			app.newTest();
		}
	};
	
	//---------------
	// AJAX functions
	//---------------
	
	app.log = function(eventName,state={}) {
		var data = {'version':version,'event':eventName,'userId':app.user.id,'state':state};
		if (app.gameId!=null) data['gameId'] = app.gameId;
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/log'),
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(data)});	
	}
	
	app.newTest = function() {
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/game/test?multiplayer=false'
					+'&userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.userboard = data;
				app.gameboard = app.userboard.game;
				app.gameId = app.gameboard.gameId;
				app.gameName = app.gameboard.gameName;
				$('#scoreBar').show();
				$('#scoreBoard').hide();
				$('#blockToggleButton').hide();
				app.setupStockAndDiscardPiles();
				app.setupFoundation();
				app.setupTableau();
			}});		
	};
	
	app.newGame = function() {
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/game?multiplayer=false'
					+'&userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.userboard = data;
				app.gameboard = app.userboard.game;
				app.gameId = app.gameboard.gameId;
				app.gameName = app.gameboard.gameName;
				$('#scoreBar').show();
				$('#scoreBoard').hide();
				$('#blockToggleButton').hide();
				$('.chatbar').hide();
				app.setupStockAndDiscardPiles();
				app.setupFoundation();
				app.setupTableau();
			}});		
	};
	
	app.updateInviteLink = function() {
		$('#inviteLink .url')
			.val($(location).attr('href').split('#')[0]+'#'+this.gameId);
		$('.gamename').html(app.gameName);
		if (app.gameboard.host) {
			$('.editGamenameBtn').show();
		}
		$('.editGamename [name=gamenameInput]').val(app.gameName);
		if ('serviceWorker' in navigator && navigator.share) {
			$('#inviteLink').append(
					$('<div class="input-group-append">').append(
							$('<button class="btn btn-outline-primary" type="button" title="Share URL..."><i class="fa fa-share-alt"></i> Share</button>')
							.on('click',function(){
								navigator.share({
									title: 'Double Solitare',
									text: 'Join me in a game of Double Solitare',
									url: $(location).attr('href').split('#')[0]+'#'+app.gameId
								}).then(()=> console.log('Successful share'));
							})));
		} else {
			$('#inviteLink').append(
					$('<div class="input-group-append">').append(
							$('<button class="btn btn-outline-primary" type="button" title="Copy to clipboard"><i class="fa fa-clipboard"></i> Copy</button>')
							.on('click',function(){
								var copyText = $('#inviteLink .url').get(0);
								copyText.select();
								copyText.setSelectionRange(0,99999);
								document.execCommand("copy")
							})));
		}
	}
	
	app.newMultiplayerGame = function() {
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/game?multiplayer=true'
					+'&userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.userboard = data;
				app.gameboard = app.userboard.game;
				app.gameId = app.gameboard.gameId;
				app.gameName = app.gameboard.gameName;
				app.updateInviteLink();
				connect(app.gameId);
				$('#scoreBar').hide();
				$('#scoreBoard').show();
				$('#blockToggleButton').show();
				$('.chatbar').show();
				app.setupStockAndDiscardPiles();
				app.setupFoundation();
				app.setupTableau();
			}});		
	};
	
	app.newMultiplayerTest = function() {
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/game/test?multiplayer=true'
					+'&userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.userboard = data;
				app.gameboard = app.userboard.game;
				app.gameId = app.gameboard.gameId;
				app.gameName = app.gameboard.gameName;
				app.updateInviteLink();
				connect(app.gameId);
				$('#scoreBar').hide();
				$('#scoreBoard').show();
				$('#blockToggleButton').show();
				$('.chatbar').show();
				app.setupStockAndDiscardPiles();
				app.setupFoundation();
				app.setupTableau();
			}});		
	};
	
	app.joinGame = function(gameId) {
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/game/'+gameId+'/join'
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				connect(gameId);
				$('#scoreBar').hide();
				$('#scoreBoard').show();
				$('#blockToggleButton').show();
				$('.chatbar').show();
				app.userboard = data;
				app.gameboard = app.userboard.game;
				app.gameId = app.gameboard.gameId;
				app.gameName = app.gameboard.gameName;
				app.updateInviteLink();
				app.setupStockAndDiscardPiles();
				app.setupFoundation();
				app.setupTableau();
			},
			error: function(jqXHR, textStatus, errorThrown){
				alert('Unable to join game');
				$('.cancelBtn').click();
			}});		
	};
	
	app.readyStatus = function(gameId, ready) {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+gameId+'/ready'
					+'?ready='+ready
					+'&userId='+app.user.id),
			contentType: "application/json",
			dataType: "json"});		
	};
	
	app.toggleBlock = function(gameId, blocked) {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+gameId+'/toggle'
					+'?blocked='+blocked
					+'&userId='+app.user.id),
			contentType: "application/json",
			dataType: "json"});		
	};
	
	app.syncGame = function(gameId) {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+gameId
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.userboard = data;
				app.gameboard = app.userboard.game;
				app.gameId = app.gameboard.gameId;
			}});		
	};
	
	app.leaveGame = function() {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+app.gameId+'/leave'
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json"});	
	};
	
	app.endGame = function() {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+app.gameId+'/end'
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json"});	
	};
	
	
	app.toggleSleep = function(gameId, sleep) {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+gameId+'/toggle'
					+'?sleep='+sleep
					+'&userId='+app.user.id),
			contentType: "application/json",
			dataType: "json"});		
	}
	
	app.chat = function(message) {
		var data = {'message':message};
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/game/'+app.gameId+'/chat'
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(data)});	
	};
	
	
	app.handleUnload = function() {
		$(window).on('unload', function(){
			app.log('unload');
	    	app.leaveGame();
	    });
	};
	app.handleBeforeUnload = function() {
		$(window).on('beforeunload', function(){
			app.log('beforeunload');
			return confirm("Do you really want to leave the game?"); 
		});
	}
	
	app.handleVisibilityChange = function() {
		
		document.addEventListener("visibilitychange", function() {
			app.log('visibilitychange',
				{
					'document.hidden':document.hidden,
					'document.visibilityState':document.visibilityState
				});
			if (document.hidden) {
				app.toggleSleep(app.gameId, true);
			} else {
				app.toggleSleep(app.gameId, false);
			}
		}, false);
	};
		
	
	app.canMove = function(cardId) {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+app.gameId+"/canmove/"+cardId
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.canMoveData = data;
			}});
	};
	
	app.syncFoundation = function(cardId, card, foundationId) {
		console.log('syncFoundation('+cardId+","+foundationId+")");
		var cardDiv = $("#foundationPile"+foundationId+" .pokercard");
		var newCardDiv = $('<div>')
			.addClass('pokercard').addClass('front')
			.attr('data-card-id',card.unicodeInt)
			.append($('<img>')
				.attr('src',getRelativePath('/img/1'+card.unicodeHex+'.png'))
				.attr('title',card.unicodeHtmlEntity));
		if (card.color=='RED') newCardDiv.addClass('red');
		
		if (cardDiv.length==0) {
			if (cardDiv.attr('data-card-id')!=cardId) {
				$("#foundationPile"+foundationId).append(newCardDiv);
			}
		} else {
			cardDiv.replaceWith(newCardDiv);
		}
	}
	
	app.moveToFoundation = function(cardId, foundationId) {
		
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+app.gameId+"/move/"+cardId+"/toFoundation/"+foundationId
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				var cardDiv = $("#tableau [data-card-id='"+cardId+"'], #discardPile [data-card-id='"+cardId+"']");
				var pileDiv = cardDiv.parents('.pile');
				var pileId = pileDiv.attr('data-pile-id');
				var nextBuildDiv = cardDiv.next('.build');
				
				if ($("#foundationPile"+foundationId+" .pokercard").length > 0) {
					$("#foundationPile"+foundationId+" .pokercard").replaceWith(cardDiv);
				} else {
					$("#foundationPile"+foundationId).append(cardDiv);
				}
				nextBuildDiv.remove();
				cardDiv.removeClass('fan-down')
				cardDiv.removeClass('fan-right');
				if (app.gameboard.multiPlayer && app.gameboard.foundation.pile[foundationId].numberOfCards==12) {
					cardDiv.addClass('back');
					cardDiv.find("img").attr('src',getRelativePath('/img/back1.png'));
				}
				app.userboard = data;
				app.gameboard = app.userboard.game;
				if (pileId!=null) {
					if (app.userboard.tableau.pile[pileId].numberOfCards>=0 && 
							app.userboard.tableau.build[pileId].numberOfCards==1) {
						app.flip(pileId);
					}
				} else {
					$('#discardPile .build').append($('#discardPile .pokercard:last'));
				}
				$('#scoreBar .score').html('Score: '+app.userboard.score.totalScore);
				$('#scoreBar .moves').html('Moves: '+app.userboard.score.totalMoves);
				if (app.gameboard.multiPlayer && app.gameboard.gameOver) {
					console.log('game over');
					$('#gameOverTitle').text('You Win!');
					$('#gameOver .modal-body').html('Score: '+app.userboard.score.totalScore)
					
					$('#gameOver').modal('show');
				}
			}});
	};
	
	app.moveToTableau = function(cardId, buildId) {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+app.gameId+"/move/"+cardId+"/toTableau/"+buildId
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				var cardDiv = $("#tableau [data-card-id='"+cardId+"'], #discardPile [data-card-id='"+cardId+"']");
				var fromPileDiv = cardDiv.parents('.pile');
				var fromPileId = fromPileDiv.attr('data-pile-id');
				var nextBuildDiv = cardDiv.next('.build');
				$("#pile"+buildId+" .build:last").append(cardDiv).append(nextBuildDiv);
				cardDiv.removeClass('fan-right');
				if (app.userboard.tableau.build[buildId].numberOfCards+app.userboard.tableau.pile[buildId].numberOfCards==0) 
					cardDiv.removeClass('fan-down');
				else
					cardDiv.addClass('fan-down')
				app.userboard = data;
				app.gameboard = app.userboard.game;
				if (fromPileId!=null) {
					if (app.userboard.tableau.pile[fromPileId].numberOfCards>=0 && 
							app.userboard.tableau.build[fromPileId].numberOfCards==1) {
						app.flip(fromPileId);
					}
				} else {
					//sub-build for consistency with other piles
					var subBuildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
					$('#discardPile .build').append($('#discardPile .pokercard:last')).append(subBuildDiv);
				}
				$('#score').html('Score: '+app.userboard.score.totalScore);
				$('#moves').html('Moves: '+app.userboard.score.totalMoves);
			}});
	};
	
	app.discard = function() {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+app.gameId+"/discard"
					+'?userId='+app.user.id),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.userboard = data;
				app.gameboard = app.userboard.game;
				$('#discard-pile').empty();
				var maxcards = app.userboard.discardPile.cards.length>=3 ? 3 : app.userboard.discardPile.cards.length
				for (var c=0; c<maxcards; c++) {
					var card = app.userboard.discardPile.cards[maxcards-1-c];
					var cardDiv = $('<div>')
						.addClass('pokercard').addClass('front')
						.attr('data-card-id',card.unicodeInt)
						.append($('<img>')
							.attr('src',getRelativePath('/img/1'+card.unicodeHex+'.png'))
							.attr('title',card.unicodeHtmlEntity));
					if (card.color=='RED') cardDiv.addClass('red');
					cardDiv.addClass('fan-right');
					if (c==2) {
						var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
						buildDiv.append(cardDiv);
						$('#discard-pile').append(buildDiv);
						
						//sub-build for consistency with other piles
						var subBuildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
						buildDiv.append(subBuildDiv);
					} else {
						$('#discard-pile').append(cardDiv);
					}
				}
				if (app.userboard.stockPile.empty) {
					$('#stock-pile .pokercard').hide();
				} else {
					$('#stock-pile .pokercard').show();
				}
				$('#score').html('Score: '+app.userboard.score.totalScore);
				$('#moves').html('Moves: '+app.userboard.score.totalMoves);
			}});
	};
	
	//---------------
	// Display functions
	//---------------
	
	
	app.flip = function(pileId) {
		var cardDiv = $('#tableau #pile'+pileId+' .pokercard:last');
		var build = app.userboard.tableau.build[pileId];
		var card = build.cards[build.cards.length-1];
		var subBuildDiv = $('<div>').addClass('build').attr('draggable',true)
			.on('dragstart', app.drag);
		$('#tableau #pile'+pileId+' .build:first')
					.append(cardDiv
						.removeClass('back')
						.addClass('front')
						.attr('data-card-id',card.unicodeInt)
						.empty()
						.append($('<img>')
							.attr('src',getRelativePath('/img/1'+card.unicodeHex+'.png'))
							.attr('title',card.unicodeHtmlEntity)))
					.append(subBuildDiv);
		if (card.color=='RED') cardDiv.addClass('red');
	}
	
	app.addPlayer = function(rowNum) {
		var rowDiv = $('<div>').addClass('row')
		$('#foundation').append(rowDiv);
		for (var i=0; i<4; i++) {
			//create pileDiv
			var colDiv = $('<div>').addClass('col');
			rowDiv.append(colDiv);
			var pileDiv = $('<div>').attr('id','foundationPile'+((rowNum*4)+i))
				.addClass('pile')
				.attr('data-pile-id',((rowNum*4)+i))
				.on('drop', app.drop)
				.on('dragover', app.dragover)
				.on('dragenter', app.dragenter)
				.on('dragleave', app.dragleave);
			colDiv.append(pileDiv);
			
			var targetDiv = $('<div>').addClass('target');
			pileDiv.append(targetDiv);
		}
	}
	
	app.removePlayer = function() {
		$('#foundation .row').get(0).remove();
	}
	
	app.setupFoundation = function() {
		for (i in app.gameboard.users) {
			app.addPlayer(i);
			if (app.gameboard.users[i].id!=app.user.id) {
				app.addPlayerStatus(app.gameboard.users[i]);
			}
			
		}
	};
	
	app.checkReadyStatus = function() {
		var isready = true;
		if ($('.user .ready').length < 2) {
			isready = false;
			console.log('Not enough players');
			$('#startgame_countdown_text')
				.text('Waiting...');
			if (app.coundownTimer!=null) clearInterval(app.countdownTimer);
			$("#startgame_countdown").val(0);
			return;
		}
		$('.user .ready').each(function(i){
			console.log($(this))
			if (!$(this).is(':checked')) {
				isready = false;
				console.log('user '+$(this).attr('data-user-id')+' is not ready');
				$('#startgame_countdown_text')
					.text('Waiting...');
				if (app.coundownTimer!=null) clearInterval(app.countdownTimer);
				$("#startgame_countdown").val(0);
			}
		});
		if (isready) {
			$('#startgame_countdown_text')
				.text('Starting game in 5...');
			
			var timeleft = 5;
			app.countdownTimer = setInterval(function(){
			  if(timeleft <= 0){
			    clearInterval(app.countdownTimer);
			    $('#waitForPlayers').modal('hide');
			  }
			  $("#startgame_countdown").val(5 - timeleft);
			  $('#startgame_countdown_text')
				.text('Starting game in '+timeleft+'...');
			  timeleft -= 1;
			}, 1000);
		}
	}
	
	
	app.readyStatusOnChange = function() {
//		console.log($(this).is(':checked'));
		app.readyStatus(app.gameId, $(this).is(':checked'));
	}
	
	app.addPlayerStatus = function(user) {
		var userDiv = $('<div>')
			.addClass('list-group-item')
			.addClass('user')
			.attr('data-user-id',user.id)
			.html('<span class="username">'+user.username+"</span> <small class='status'></small><label>I'm Ready: <input class='ready checkbox-2x' data-user-id='"+user.id+"' type='checkbox' disabled='disabled'/></label>")
		userDiv.find('.ready').on('change',app.readyStatusOnChange);
		$('#waitForPlayers .users').append(userDiv);
		
		$('#scoreBoard.users tbody').append(
				$('<tr>')
					.addClass('user')
					.attr('data-user-id',user.id)
						.append(
							$('<td>').addClass('username').text(user.username))
						.append(
							$('<td>').addClass('status').text(''))
						.append(
							$('<td>').addClass('moves').text('0')));
	}
	
	app.setupTableau = function() {
		$('#tableau').addClass('row');
		for (var i=0; i<7; i++) {
			var pile = app.userboard.tableau.pile[i];
			var build = app.userboard.tableau.build[i];
			
			//create pileDiv
			var colDiv = $('<div>').addClass('col');
			$('#tableau').append(colDiv);
			var pileDiv = $('<div>').attr('id','pile'+i).addClass('col').addClass('pile')
				.attr('data-pile-id',i)
				.on('drop', app.drop)
				.on('dragover', app.dragover)
				.on('dragenter', app.dragenter)
				.on('dragleave', app.dragleave);
			colDiv.append(pileDiv);
			
			var targetDiv = $('<div>').addClass('target');
			pileDiv.append(targetDiv);
			
			for (var c=0; c<pile.numberOfCards; c++) {
				var cardDiv = $('<div>')
					.addClass('pokercard').addClass('back')
					.append($('<img>')
						.attr('src',getRelativePath('/img/back1.png')));
				if (c>0) cardDiv.addClass('fan-down');
				pileDiv.append(cardDiv);
			}
			
			var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
			
			pileDiv.append(buildDiv);
			
			for (c in build.cards) {
				var card = build.cards[c];
				var cardDiv = $('<div>')
					.addClass('pokercard').addClass('front')
					.attr('data-card-id',card.unicodeInt)
					.append($('<img>')
						.attr('src',getRelativePath('/img/1'+card.unicodeHex+'.png'))
						.attr('title',card.unicodeHtmlEntity));
				if (card.color=='RED') cardDiv.addClass('red');
				if (c+pile.numberOfCards>0) cardDiv.addClass('fan-down');
				
				buildDiv.append(cardDiv);
				
				//sub-build to drag a card ontop of this, but not this card
				var subBuildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
				buildDiv.append(subBuildDiv);
				buildDiv = subBuildDiv;
			}
		};
	}
	
	app.setupStockAndDiscardPiles = function() {
		//create pileDiv
		var pileDiv = $('<div>').attr('id','stock-pile').addClass('pile')
			.on('click', app.discard);
		$('#stockPile').append(pileDiv);
		var targetDiv = $('<div>').addClass('target');
		pileDiv.append(targetDiv);
		
		var cardDiv = $('<div>')
			.addClass('pokercard').addClass('back')
			.append($('<img>')
				.attr('src',getRelativePath('/img/back1.png')));
		pileDiv.append(cardDiv);
		
		var pileDiv = $('<div>').attr('id','discard-pile').addClass('pile')
		$('#discardPile').append(pileDiv);
		
		var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
		pileDiv.append(buildDiv);
	};
	
	//---------------
	// Drag & Drop functions
	//---------------
	
	app.dragenter = function(ev) {
		if (app.canMoveData) {
			var curTarget = $(ev.currentTarget);
			var dataPileId = curTarget.attr('data-pile-id')
			if ($(ev.currentTarget).parents('#foundation').length>0) {
				if (app.canMoveData.foundationPile[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
			if ($(ev.currentTarget).parents('#tableau').length>0) {
				if (app.canMoveData.tableauBuild[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
		}
		
	};
	app.dragover = function(ev) {
		// We'll handle this event so first stop bubbling up
		ev.stopPropagation();
		
		if (app.canMoveData) {
			var curTarget = $(ev.currentTarget);
			var dataPileId = curTarget.attr('data-pile-id')
			if ($(ev.currentTarget).parents('#foundation').length>0) {
				if (app.canMoveData.foundationPile[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
			if ($(ev.currentTarget).parents('#tableau').length>0) {
				if (app.canMoveData.tableauBuild[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
		}
	}
	app.dragleave = function(ev) {
		var curTarget = $(ev.currentTarget);
		curTarget.removeClass('canDrop');
	};
	
	app.drag = function(ev) {
		// We'll handle this event so first stop bubbling up
		ev.stopPropagation();
		
		var cardDiv = $(ev.currentTarget.getElementsByClassName('pokercard')[0]);
				
		app.canMove(cardDiv.attr('data-card-id'));
		
		ev.originalEvent.dataTransfer.setData("text", cardDiv.attr('data-card-id'));
	};
	
	app.drop = function(ev) {
		ev.preventDefault();
		var pileId = $(ev.currentTarget).attr('data-pile-id');
		var cardId = ev.originalEvent.dataTransfer.getData('text');
		if ($(ev.currentTarget).parents('#foundation').length>0) {
			app.moveToFoundation(cardId, pileId);
		}
		if ($(ev.currentTarget).parents('#tableau').length>0) {
			app.moveToTableau(cardId, pileId);
		}
		var curTarget = $(ev.currentTarget);
		curTarget.removeClass('canDrop');
		app.canMoveData = null;
	};
		
	$('.sendMessageBtn').on('click', function(){
		var messageInput = $(this).closest('.chatbar').find('[name=messageInput]');
		app.chat(messageInput.val());
		messageInput.val("");
	});
	$('[name=messageInput]').on('keypress',function(ev){
		if(ev.which == 13) {
			app.chat($(this).val());
			$(this).val("");
	    }
	});
	
	$('#gameOver').on('hidden.bs.modal', function(){
		app.log('#gameOver.hidden.bs.modal');
		window.location.replace("/");
	});
	
	$('.cancelBtn').on('click', function(){
		app.log('.cancelBtn.click');
		app.leaveGame();
		window.location.replace("/");
	});
	
	$('#quitBtn').on('click', function(){
		app.log('#quitBtn.click');
		app.leaveGame();
		if (!app.gameboard.multiPlayer) {
			window.location.replace("/");
		}
	});
	
	$('#endGameBtn').on('click', app.endGame);
	
	$('#blockBtn').on('change',function(){
		app.toggleBlock(app.gameId, $(this).prop("checked") == true);
		$(this).blur();
		$('#quitBtn').focus();
	});
	
	$('.editUsernameBtn').on('click', function(){
		$('.editUsername').show();
		$('.staticUsername').hide();
	});
	$('.saveUsernameBtn').on('click', function(){
		$('.editUsername').hide();
		$('.staticUsername').show();
		menu.updateUser($('.editUsername [name=usernameInput]').val());
	});
	$('.editGamenameBtn').on('click', function(){
		$('.editGamename').show();
		$('.editGamenameBtn').hide();
	});
	$('.saveGamenameBtn').on('click', function(){
		$('.editGamename').hide();
		$('.editGamenameBtn').show();
		menu.updateGame($('.editGamename [name=gamenameInput]').val());
	});
	
	
	//Service worker
    if ('serviceWorker' in navigator && 'PushManager' in window) {
    	navigator.serviceWorker
    		.register('./service-worker.js?ctxPath='+ctx)
    		.then(function(registration) {
    			console.log('Service Worker Registered with scope: ', registration.scope);   			
    		}).catch(function(error){console.log('Service worker failed: ', error)});
    } else {
    	console.warn('ServiceWorkers are not supported');
    }
	
})();

menu.setup();
