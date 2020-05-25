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
    });
  }
}

