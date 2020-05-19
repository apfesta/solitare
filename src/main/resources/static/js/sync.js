//Call connect() when you've got a game Id and are ready to listen
function connect() {
  if (!connectStarted) {
    connectStarted = true;
    var socket  new SockJS(stompUrl);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame){
      setConnected(true);
      console.log('Connected: '+frame);
    });
  }
}

function disconnect() {
  if (stopClient!=null) {
    stompClient.disconnect();
  }
  console.log('Disconnected');
}

function setConnected(connected) {
  if (connected) {
    //stomp.subscribe('/topic/..', function(result){ // on payload });
  }
}
