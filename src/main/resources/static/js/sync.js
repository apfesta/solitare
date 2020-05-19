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
