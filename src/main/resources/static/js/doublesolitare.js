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
	};
	
	app.moveToFoundation = function(cardId, foundationId) {
		console.log('moveToFoundation');
		$.ajax({
			type: 'GET', 
			url: '/api/game/'+app.gameId+"/move/"+cardId+"/toFoundation/"+foundationId,
			contentType: "application/json",
			success: function(data){
				$("#foundationPile"+foundationId).append($("#tableau [data-card-id='"+cardId+"']").removeClass('overlap'));
			}});
	}
	
	app.dragover = function(ev) {
		ev.preventDefault();
		ev.originalEvent.dataTransfer.dropEffect = "move";
	};
	
	app.drag = function(ev) {
		//what info gets dragged
		var cardDiv = $(ev.target.getElementsByClassName('pokercard')[0]);
		console.log(cardDiv.attr('data-card-id'));
		
		app.canMove(cardDiv.attr('data-card-id'));
		
		ev.originalEvent.dataTransfer.setData("text", cardDiv.attr('data-card-id'));
	};
	
	app.drop = function(ev) {
		ev.preventDefault();
		var foundationId = $(ev.currentTarget).attr('data-pile-id');
		var cardId = ev.originalEvent.dataTransfer.getData('text');
		app.moveToFoundation(cardId, foundationId);
	};
	
	app.setupFoundation = function() {
		$('#foundation').addClass('row');
		for (var i=0; i<4; i++) {
			//create pileDiv
			var pileDiv = $('<div>').attr('id','foundationPile'+i)
				.addClass('col').addClass('pile')
				.attr('data-pile-id',i)
				.on('drop', app.drop).on('dragover', app.dragover);
			$('#foundation').append(pileDiv);
			
			var targetDiv = $('<div>').addClass('target');
			pileDiv.append(targetDiv);
		}
	};
	
	app.setupTableau = function() {
		$('#tableau').addClass('row');
		for (var i=0; i<7; i++) {
			var pile = app.gameboard.tableau.pile[i];
			var build = app.gameboard.tableau.build[i];
			
			//create pileDiv
			var pileDiv = $('<div>').attr('id','pile'+i).addClass('col').addClass('pile');
			$('#tableau').append(pileDiv);
			
			var targetDiv = $('<div>').addClass('target');
			pileDiv.append(targetDiv);
			
			for (c in pile.cards) {
				var card = pile.cards[c];
				cardDiv = $('<div>').addClass('pokercard').addClass('back').html("&#x1F0A0;");
				if (c>0) cardDiv.addClass('overlap');
				pileDiv.append(cardDiv);
			}
			
			var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
			
			pileDiv.append(buildDiv);
			
			for (c in build.cards) {
				var card = build.cards[c];
				cardDiv = $('<div>').addClass('pokercard').addClass('front').attr('data-card-id',card.unicodeInt).html(card.unicodeHtmlEntity);
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