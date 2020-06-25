//Call connect() when you've got a game Id and are ready to listen
var stompClient = null;
var connectStarted = false;

function connect(gameId) {
  if (!connectStarted) {
    connectStarted = true;
    var socket = new SockJS(stompUrl);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame){
      setConnected(true, gameId);
      console.log('Connected: '+frame);
    });
  }
}

function disconnect() {
  if (stompClient!=null) {
    stompClient.disconnect();
  }
  console.log('Disconnected');
}

function setConnected(connected, gameId) {
  if (connected) {
	app.handleUnload();
	app.handleVisibilityChange();
    stompClient.subscribe('/topic/game/'+gameId+'/activity', function(result){ 
    	console.debug(result);
    	var data = JSON.parse(result.body);
    	console.debug(data);
    	if (data.action=='MOVE_TO_FOUNDATION') {
    		for (var i in data.score) {
    			app.gameboard.userScores[data.user.id] = data.score[i]
    			$('#scoreBoard .user[data-user-id='+i+'] .score').text(data.score[i].toFoundation);
    			$('#scoreBoard .user[data-user-id='+i+'] .moves').text(data.score[i].totalMoves);
    		}
    		app.syncFoundation(data.cardId, data.foundation.pile[data.toFoundationId].cards[0], data.toFoundationId);
    	} else if (data.action=='MOVE_TO_TABLEAU' || data.action=='DISCARD') {
    		for (var i in data.score) {
    			app.gameboard.userScores[data.user.id] = data.score[i]
    			$('#scoreBoard .user[data-user-id='+i+'] .score').text(data.score[i].toFoundation);
    			$('#scoreBoard .user[data-user-id='+i+'] .moves').text(data.score[i].totalMoves);
    		}
    	}
    	if (data.action=='PLAYER_JOIN') {
    		app.addPlayer(data.numOfUsers-1);
    		app.addPlayerStatus(data.user);
    		app.gameboard.foundation=data.foundation;
    	}
    	if (data.action=='PLAYER_DROP') {
    		app.removePlayer();
    		$('.users .user[data-user-id='+data.user.id+']').remove();
    		if (app.gameboard.inProgress) {
				$('#gameOverTitle').text(data.user.username+' left game');
				$('#gameOver .modal-body').empty().append(
						$('<table>').append(
							$('<thead>').append(
								$('<tr>').append(
									$('<th>').html('User')).append(
									$('<th>').html('Score')).append(
									$('<th>').html('Moves'))
							)
						).append($('<tbody>')));
				for (i in app.gameboard.users) {	
					var user = app.gameboard.users[i];
					$('#gameOver .modal-body tbody').append(
						$('<tr>').append(
							$('<td>').addClass('username').html(user.username)).append(
							$('<td>').addClass('score').html("")).append(
							$('<td>').addClass('moves').html(app.gameboard.userScores[user.id].totalMoves))
					);
				}					
				$('#gameOver').modal('show');
    		}
    	}
    	if (data.action=='GAME_WON') {
			$('#gameOverTitle').text(data.user.username+' won game');
			$('#gameOver .modal-body').empty().append(
					$('<table>').append(
						$('<thead>').append(
							$('<tr>').append(
								$('<th>').html('User')).append(
								$('<th>').html('Score')).append(
								$('<th>').html('Moves'))
						)
					).append($('<tbody>')));
			for (i in app.gameboard.users) {	
				var user = app.gameboard.users[i];
				$('#gameOver .modal-body tbody').append(
					$('<tr>').append(
						$('<td>').addClass('username').html(user.username)).append(
						$('<td>').addClass('score').html(app.gameboard.userScores[user.id].toFoundation)).append(
						$('<td>').addClass('moves').html(app.gameboard.userScores[user.id].totalMoves))
				);
			}					
			$('#gameOver').modal('show');
    	}
    	if (data.action=='PLAYER_READY') {
    		$('.ready[data-user-id='+data.user.id+']').prop('checked', true);
    		app.checkReadyStatus();
    	} else if (data.action=='PLAYER_NOT_READY') {
    		$('.ready[data-user-id='+data.user.id+']').prop('checked', false);
    		app.checkReadyStatus();
    	}
    	if (data.action=='PLAY_IS_BLOCKED') {
    		$('#scoreBoard.users .user[data-user-id='+data.user.id+']').addClass('blocked');
    		$('#scoreBoard .user[data-user-id='+data.user.id+'] .status').html("I'm Stuck!");
    	} else if (data.action=='PLAY_NOT_BLOCKED') {
    		if (data.user.id == app.user.id) {
    			$('#blockBtn')
    			.prop('checked',false)
    			.closest('label').removeClass('active');
    		}
    		$('#scoreBoard.users .user[data-user-id='+data.user.id+']').removeClass('blocked');
    		$('#scoreBoard .user[data-user-id='+data.user.id+'] .status').html("");
    	}
    	if (data.action=='PLAYER_SLEEP') {
    		$('#scoreBoard.users .user[data-user-id='+data.user.id+']').addClass('sleep');
    		$('#scoreBoard .user[data-user-id='+data.user.id+'] .status').text("Away...");
    		$('#waitForPlayers .user[data-user-id='+data.user.id+'] .status').text('Away...');
    	} else if (data.action=='PLAYER_AWAKE') {
    		$('#scoreBoard.users .user[data-user-id='+data.user.id+']').removeClass('sleep');
    		$('#scoreBoard .user[data-user-id='+data.user.id+'] .status').text("");
    		$('#waitForPlayers .user[data-user-id='+data.user.id+'] .status').text('');
    	}
    	if (data.action=='PLAYER_RENAME'){
    		$('.user[data-user-id='+data.user.id+'] .username').html(data.user.username);
    	}
    	if (data.action=='GAME_RENAME') {
    		$('.gamename').html(data.gameName);
    	}
    });
  }
}
