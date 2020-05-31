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
    		app.syncFoundation(data.cardId, data.foundation.pile[data.toFoundationId].cards[0], data.toFoundationId);
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

