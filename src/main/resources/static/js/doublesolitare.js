console.log('hello world!');

var app = {
		gameboard: {},
		gameId: null
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
		console.log('new game query');
		$.ajax({
			type: 'POST', 
			url: '/api/game',
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.log(data);
				app.gameboard = data;
				app.gameId = data.gameId;
				app.setupFoundation();
				app.setupTableau();
			}});
	};
	
	app.canMove = function(cardId) {
		console.log('can move query');
		$.ajax({
			type: 'GET', 
			url: '/api/game/'+app.gameId+"/canmove/"+cardId,
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.log(data);
			}});
	}
	
	app.drag = function(ev) {
		//what info gets dragged
		var cardDiv = $(ev.target.getElementsByClassName('card')[0]);
		console.log(cardDiv.attr('data-card-id'));
		
		app.canMove(cardDiv.attr('data-card-id'));
		
		ev.originalEvent.dataTransfer.setData("text", cardDiv.attr('data-card-id'));
	}
	
	app.setupFoundation = function() {
		$('#foundation').addClass('row');
		for (var i=0; i<4; i++) {
			//create pileDiv
			var pileDiv = $('<div>').attr('id','foundationPile'+i).addClass('col');
			$('#foundation').append(pileDiv);
		}
	};
	
	app.setupTableau = function() {
		$('#tableau').addClass('row');
		for (var i=0; i<7; i++) {
			var pile = app.gameboard.tableau.pile[i];
			var build = app.gameboard.tableau.build[i];
			
			//create pileDiv
			var pileDiv = $('<div>').attr('id','pile'+i).addClass('col');
			$('#tableau').append(pileDiv);
			
			
			
			for (c in pile.cards) {
				var card = pile.cards[c];
				cardDiv = $('<div>').addClass('card').addClass('back').html("&#x1F0A0;");
				if (c>0) cardDiv.addClass('overlap');
				pileDiv.append(cardDiv)
			}
			
			var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
			
			pileDiv.append(buildDiv);
			
			for (c in build.cards) {
				var card = build.cards[c];
				cardDiv = $('<div>').addClass('card').addClass('front').attr('data-card-id',card.unicodeInt).html(card.unicodeHtmlEntity);
				if (card.color=='RED') cardDiv.addClass('red');
				if (c+pile.cards.length>0) cardDiv.addClass('overlap');
				
				buildDiv.append(cardDiv);
				
				//sub-build to drag a card ontop of this, but not this card
				var subBuildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
				buildDiv.append(subBuildDiv);
				buildDiv = subBuildDiv;
				
			}
			
			
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