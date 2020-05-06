
var app = {
		gameboard: {},
		gameId: null,
		canMoveData: null
};


(function() {
	'user strict';
	
	
	app.setup = function() {
		app.newGame();
	};
	
	//---------------
	// REST functions
	//---------------
	
	app.newGame = function() {
		$.ajax({
			type: 'POST', 
			url: '/api/game',
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.gameboard = data;
				app.gameId = data.gameId;
				app.setupStockAndDiscardPiles();
				app.setupFoundation();
				app.setupTableau();
			}});
	};
	
	app.canMove = function(cardId) {
		$.ajax({
			type: 'GET', 
			url: '/api/game/'+app.gameId+"/canmove/"+cardId,
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.canMoveData = data;
			}});
	};
	
	app.moveToFoundation = function(cardId, foundationId) {
		
		$.ajax({
			type: 'GET', 
			url: '/api/game/'+app.gameId+"/move/"+cardId+"/toFoundation/"+foundationId,
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				var cardDiv = $("#tableau [data-card-id='"+cardId+"'], #discardPile [data-card-id='"+cardId+"']");
				var pileDiv = cardDiv.parents('.pile');
				var pileId = pileDiv.attr('data-pile-id');
				
				if ($("#foundationPile"+foundationId+" .pokercard").length > 0) {
					$("#foundationPile"+foundationId+" .pokercard").replaceWith(cardDiv);
				} else {
					$("#foundationPile"+foundationId).append(cardDiv);
				}
				cardDiv.removeClass('fan-down')
				cardDiv.removeClass('fan-right');
				app.gameboard = data;
				if (pileId!=null) {
					if (app.gameboard.tableau.pile[pileId].numberOfCards>0) {
						app.flip(pileId);
					}
				} else {
					$('#discardPile .build').append($('#discardPile .pokercard:last'));
				}
			}});
	};
	
	app.moveToTableau = function(cardId, buildId) {
		$.ajax({
			type: 'GET', 
			url: '/api/game/'+app.gameId+"/move/"+cardId+"/toTableau/"+buildId,
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				var cardDiv = $("#tableau [data-card-id='"+cardId+"'], #discardPile [data-card-id='"+cardId+"']");
				var fromPileDiv = cardDiv.parents('.pile');
				var fromPileId = fromPileDiv.attr('data-pile-id');
				$("#pile"+buildId+" .build:last").append(cardDiv);
				cardDiv.removeClass('fan-right');
				if (app.gameboard.tableau.build[buildId].numberOfCards+app.gameboard.tableau.pile[buildId].numberOfCards==0) 
					cardDiv.removeClass('fan-down');
				else
					cardDiv.addClass('fan-down')
				app.gameboard = data;
				if (fromPileId!=null) {
					if (app.gameboard.tableau.build[fromPileId].numberOfCards>0) {
						app.flip(fromPileId);
					}
				} else {
					$('#discardPile .build').append($('#discardPile .pokercard:last'));
				}
			}});
	};
	
	app.discard = function() {
		$.ajax({
			type: 'GET', 
			url: '/api/game/'+app.gameId+"/discard",
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.gameboard = data;
				$('#discard-pile').empty();
				for (var c=0; c<3; c++) {
					var card = app.gameboard.discardPile.cards[2-c];
					cardDiv = $('<div>').addClass('pokercard').addClass('front').attr('data-card-id',card.unicodeInt).html(card.unicodeHtmlEntity);
					if (card.color=='RED') cardDiv.addClass('red');
					cardDiv.addClass('fan-right');
					if (c==2) {
						var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
						buildDiv.append(cardDiv);
						$('#discard-pile').append(buildDiv);
					} else {
						$('#discard-pile').append(cardDiv);
					}
				}
			}});
	};
	
	//---------------
	// Display functions
	//---------------
	
	app.flip = function(pileId) {
		var cardDiv = $('#tableau #pile'+pileId+' .pokercard:last');
		var build = app.gameboard.tableau.build[pileId];
		var card = build.cards[build.cards.length-1];
		$('#tableau #pile'+pileId+' .build:first').append(cardDiv
					.removeClass('back')
					.addClass('front')
					.attr('data-card-id',card.unicodeInt)
					.html(card.unicodeHtmlEntity));
		if (card.color=='RED') cardDiv.addClass('red');
	}
	
	app.setupFoundation = function() {
		$('#foundation').addClass('row');
		for (var i=0; i<4; i++) {
			//create pileDiv
			var colDiv = $('<div>').addClass('col');
			$('#foundation').append(colDiv);
			var pileDiv = $('<div>').attr('id','foundationPile'+i)
				.addClass('pile')
				.attr('data-pile-id',i)
				.on('drop', app.drop)
				.on('dragover', app.dragover)
				.on('dragenter', app.dragenter)
				.on('dragleave', app.dragleave);
			colDiv.append(pileDiv);
			
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
			var colDiv = $('<div>').addClass('col');
			$('#tableau').append(colDiv);
			var pileDiv = $('<div>').attr('id','pile'+i).addClass('col').addClass('pile')
				.attr('data-pile-id',i)
				.on('drop', app.drop)
				.on('dragover', app.dragover)
				.on('dragenter', app.dragenter)
				.on('dragleave', app.dragleave);
			colDiv.append(pileDiv);
			
			var targetDiv = $('<div>').addClass('target');
			pileDiv.append(targetDiv);
			
			for (var c=0; c<pile.numberOfCards; c++) {
				var cardDiv = $('<div>').addClass('pokercard').addClass('back').html("&#x1F0A0;");
				if (c>0) cardDiv.addClass('fan-down');
				pileDiv.append(cardDiv);
			}
			
			var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
			
			pileDiv.append(buildDiv);
			
			for (c in build.cards) {
				var card = build.cards[c];
				cardDiv = $('<div>').addClass('pokercard').addClass('front').attr('data-card-id',card.unicodeInt).html(card.unicodeHtmlEntity);
				if (card.color=='RED') cardDiv.addClass('red');
				if (c+pile.numberOfCards>0) cardDiv.addClass('fan-down');
				
				buildDiv.append(cardDiv);
				
				//sub-build to drag a card ontop of this, but not this card
				var subBuildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
				buildDiv.append(subBuildDiv);
				buildDiv = subBuildDiv;
			}
		};
	}
	
	app.setupStockAndDiscardPiles = function() {
		//create pileDiv
		var pileDiv = $('<div>').attr('id','stock-pile').addClass('col').addClass('pile')
			.on('click', app.discard);
		$('#stockPile').append(pileDiv);
		var targetDiv = $('<div>').addClass('target');
		pileDiv.append(targetDiv);
		var cardDiv = $('<div>').addClass('pokercard').addClass('back').html("&#x1F0A0;");
		pileDiv.append(cardDiv);
		
		var pileDiv = $('<div>').attr('id','discard-pile').addClass('col').addClass('pile')
		$('#discardPile').append(pileDiv);
	};
	
	//---------------
	// Drag & Drop functions
	//---------------
	
	app.dragenter = function(ev) {
		if (app.canMoveData) {
			var curTarget = $(ev.currentTarget);
			var dataPileId = curTarget.attr('data-pile-id')
			if ($(ev.currentTarget).parents('#foundation').length>0) {
				if (app.canMoveData.foundationPile[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
			if ($(ev.currentTarget).parents('#tableau').length>0) {
				if (app.canMoveData.tableauBuild[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
		}
		
	};
	app.dragover = function(ev) {
		if (app.canMoveData) {
			var curTarget = $(ev.currentTarget);
			var dataPileId = curTarget.attr('data-pile-id')
			if ($(ev.currentTarget).parents('#foundation').length>0) {
				if (app.canMoveData.foundationPile[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
			if ($(ev.currentTarget).parents('#tableau').length>0) {
				if (app.canMoveData.tableauBuild[dataPileId]) {
					ev.preventDefault();
					ev.originalEvent.dataTransfer.dropEffect = "move";
					curTarget.addClass('canDrop');
				}
			}
		}
	}
	app.dragleave = function(ev) {
		var curTarget = $(ev.currentTarget);
		curTarget.removeClass('canDrop');
	};
	
	app.drag = function(ev) {
		//what info gets dragged
		var cardDiv = $(ev.target.getElementsByClassName('pokercard')[0]);
				
		app.canMove(cardDiv.attr('data-card-id'));
		
		ev.originalEvent.dataTransfer.setData("text", cardDiv.attr('data-card-id'));
	};
	
	app.drop = function(ev) {
		ev.preventDefault();
		var pileId = $(ev.currentTarget).attr('data-pile-id');
		var cardId = ev.originalEvent.dataTransfer.getData('text');
		if ($(ev.currentTarget).parents('#foundation').length>0) {
			app.moveToFoundation(cardId, pileId);
		}
		if ($(ev.currentTarget).parents('#tableau').length>0) {
			app.moveToTableau(cardId, pileId);
		}
		var curTarget = $(ev.currentTarget);
		curTarget.removeClass('canDrop');
		app.canMoveData = null;
	};
		
	
	
	
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