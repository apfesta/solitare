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
    stompClient.subscribe('/topic/game/'+gameId+'/activity', function(result){ 
    	console.log(result);
    	var data = JSON.parse(result.body);
    	console.log(data);
    	if (data.action=='MOVE_TO_FOUNDATION') {
    		console.log(data.foundation.pile[data.toFoundationId]);
    		app.syncFoundation(data.cardId, data.foundation.pile[data.toFoundationId].cards[0], data.toFoundationId);
    	}
    	
    });
  }
}

