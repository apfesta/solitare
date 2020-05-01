console.log('hello world!');

var app = {
		gameboard: {}
};

//game class
//get intial board from API
//print it out using unicode

(function() {
	'user strict';
	
	
	app.setup = function() {
		app.newGame();
	};
	
	app.newGame = function() {
		console.log('new game');
		$.ajax({
			type: 'POST', 
			url: '/api/game',
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.log(data);
				app.gameboard = data;
				app.setupTableau();
			}});
	};
	
	app.setupTableau = function() {
		$('#tableau').addClass('row');
		for (var i=0; i<7; i++) {
			var pile = app.gameboard.tableau.pile[i];
			var build = app.gameboard.tableau.build[i];
			
			//create pileDiv
			var pileDiv = $('<div>').attr('id','pile'+i).addClass('col');
			$('#tableau').append(pileDiv);
			
			var buildDiv = $('<div>').addClass('build');
			
			for (c in pile.cards) {
				var card = pile.cards[c];
				console.log(card);
				cardDiv = $('<div>').addClass('card').addClass('back').html("&#x1F0A0;");
				if (c>0) cardDiv.addClass('overlap');
				pileDiv.append(cardDiv)
			}
			for (c in build.cards) {
				var card = build.cards[c];
				console.log(card);
				cardDiv = $('<div>').addClass('card').addClass('front').html(card.unicodeHtmlEntity);
				if (card.color=='RED') cardDiv.addClass('red');
				if (c+pile.cards.length>0) cardDiv.addClass('overlap');
				buildDiv.append(cardDiv)
			}
			pileDiv.append(buildDiv);
			
		}
		
		
		//$('#tableau')
	}
	
	
	//Service worker
//    if ('serviceWorker' in navigator && 'PushManager' in window) {
//    	navigator.serviceWorker
//    		.register('./service-worker.js')
//    		.then(function(registration) {
//    			console.log('Service Worker Registered with scope: ', registration.scope); 
//    			
//    			
//    			    			
//    		}).catch(function(error){console.log('Service worker failed: ', error)});
//    } else {
//    	console.warn('ServiceWorkers and Push messaging are not supported');
//    }
	
})();

app.setup();