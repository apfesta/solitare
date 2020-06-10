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
	app.handleBeforeUnload
	app.handleUnload();
    stompClient.subscribe('/topic/game/'+gameId+'/activity', function(result){ 
    	console.log(result);
    	var data = JSON.parse(result.body);
    	if (data.action=='MOVE_TO_FOUNDATION') {
    		console.log(data.foundation.pile[data.toFoundationId]);
    		console.log(data.score);
    		for (var i in data.score) {
    			console.log(i);
    			$('#scoreBoard .user[data-user-id='+i+'] .score').text(data.score[i].toFoundation);
    			$('#scoreBoard .user[data-user-id='+i+'] .moves').text(data.score[i].totalMoves);
    		}
    		app.syncFoundation(data.cardId, data.foundation.pile[data.toFoundationId].cards[0], data.toFoundationId);
    	} else if (data.action=='MOVE_TO_TABLEAU' || data.action=='DISCARD') {
    		for (var i in data.score) {
    			console.log(i);
    			$('#scoreBoard .user[data-user-id='+i+'] .score').text(data.score[i].toFoundation);
    			$('#scoreBoard .user[data-user-id='+i+'] .moves').text(data.score[i].totalMoves);
    		}
    	}
    	if (data.action=='PLAYER_JOIN') {
    		app.addPlayer(data.numOfUsers-1);
    		app.addPlayerStatus(data.user);
    	}
    	if (data.action=='PLAYER_DROP') {
    		app.removePlayer();
    		$('.users .user[data-user-id='+data.user.id+']').remove();
    	}
    	if (data.action=='PLAYER_READY') {
    		$('.ready[data-user-id='+data.user.id+']').prop('checked', true);
    		app.checkReadyStatus();
    	} else if (data.action=='PLAYER_NOT_READY') {
    		$('.ready[data-user-id='+data.user.id+']').prop('checked', false);
    		app.checkReadyStatus();
    	}
    });
  }
}
