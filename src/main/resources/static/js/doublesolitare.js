
var menu = {
		games: []
};


(function() {
	'user strict';
	
	menu.setup = function() {
		menu.getGames();
	}
	
	//---------------
	// AJAX functions
	//---------------
	
	menu.getGames = function() {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game'),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				menu.games = data;
				//TODO display game selection
			}});
	};

})();




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
	// AJAX functions
	//---------------
	
	app.newGame = function() {
		$.ajax({
			type: 'POST', 
			url: getRelativePath('/api/game'),
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
			url: getRelativePath('/api/game/'+app.gameId+"/canmove/"+cardId),
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
			url: getRelativePath('/api/game/'+app.gameId+"/move/"+cardId+"/toFoundation/"+foundationId),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				var cardDiv = $("#tableau [data-card-id='"+cardId+"'], #discardPile [data-card-id='"+cardId+"']");
				var pileDiv = cardDiv.parents('.pile');
				var pileId = pileDiv.attr('data-pile-id');
				var nextBuildDiv = cardDiv.next('.build');
				
				if ($("#foundationPile"+foundationId+" .pokercard").length > 0) {
					$("#foundationPile"+foundationId+" .pokercard").replaceWith(cardDiv);
				} else {
					$("#foundationPile"+foundationId).append(cardDiv);
				}
				nextBuildDiv.remove();
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
			url: getRelativePath('/api/game/'+app.gameId+"/move/"+cardId+"/toTableau/"+buildId),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				var cardDiv = $("#tableau [data-card-id='"+cardId+"'], #discardPile [data-card-id='"+cardId+"']");
				var fromPileDiv = cardDiv.parents('.pile');
				var fromPileId = fromPileDiv.attr('data-pile-id');
				var nextBuildDiv = cardDiv.next('.build');
				$("#pile"+buildId+" .build:last").append(cardDiv).append(nextBuildDiv);
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
					//sub-build for consistency with other piles
					var subBuildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
					$('#discardPile .build').append($('#discardPile .pokercard:last')).append(subBuildDiv);
				}
			}});
	};
	
	app.discard = function() {
		$.ajax({
			type: 'GET', 
			url: getRelativePath('/api/game/'+app.gameId+"/discard"),
			contentType: "application/json",
			dataType: "json",
			success: function(data){
				console.debug(data);
				app.gameboard = data;
				$('#discard-pile').empty();
				for (var c=0; c<3; c++) {
					var card = app.gameboard.discardPile.cards[2-c];
					var cardDiv = $('<div>')
						.addClass('pokercard').addClass('front')
						.attr('data-card-id',card.unicodeInt)
						.append($('<img>')
							.attr('src',getRelativePath('/img/1'+card.unicodeHex+'.png'))
							.attr('title',card.unicodeHtmlEntity));
					if (card.color=='RED') cardDiv.addClass('red');
					cardDiv.addClass('fan-right');
					if (c==2) {
						var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
						buildDiv.append(cardDiv);
						$('#discard-pile').append(buildDiv);
						
						//sub-build for consistency with other piles
						var subBuildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
						buildDiv.append(subBuildDiv);
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
		var subBuildDiv = $('<div>').addClass('build').attr('draggable',true)
			.on('dragstart', app.drag);
		$('#tableau #pile'+pileId+' .build:first')
					.append(cardDiv
						.removeClass('back')
						.addClass('front')
						.attr('data-card-id',card.unicodeInt)
						.empty()
						.append($('<img>')
							.attr('src',getRelativePath('/img/1'+card.unicodeHex+'.png'))
							.attr('title',card.unicodeHtmlEntity)))
					.append(subBuildDiv);
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
				var cardDiv = $('<div>')
					.addClass('pokercard').addClass('back')
					.append($('<img>')
						.attr('src',getRelativePath('/img/back1.png')));
				if (c>0) cardDiv.addClass('fan-down');
				pileDiv.append(cardDiv);
			}
			
			var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
			
			pileDiv.append(buildDiv);
			
			for (c in build.cards) {
				var card = build.cards[c];
				var cardDiv = $('<div>')
					.addClass('pokercard').addClass('front')
					.attr('data-card-id',card.unicodeInt)
					.append($('<img>')
						.attr('src',getRelativePath('/img/1'+card.unicodeHex+'.png'))
						.attr('title',card.unicodeHtmlEntity));
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
		var pileDiv = $('<div>').attr('id','stock-pile').addClass('pile')
			.on('click', app.discard);
		$('#stockPile').append(pileDiv);
		var targetDiv = $('<div>').addClass('target');
		pileDiv.append(targetDiv);
		
		var cardDiv = $('<div>')
			.addClass('pokercard').addClass('back')
			.append($('<img>')
				.attr('src',getRelativePath('/img/back1.png')));
		pileDiv.append(cardDiv);
		
		var pileDiv = $('<div>').attr('id','discard-pile').addClass('pile')
		$('#discardPile').append(pileDiv);
		
		var buildDiv = $('<div>').addClass('build').attr('draggable',true).on('dragstart', app.drag);
		pileDiv.append(buildDiv);
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
		var cardDiv = $(ev.currentTarget.getElementsByClassName('pokercard')[0]);
				
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
    if ('serviceWorker' in navigator && 'PushManager' in window) {
    	navigator.serviceWorker
    		.register('./service-worker.js?ctxPath='+ctx)
    		.then(function(registration) {
    			console.log('Service Worker Registered with scope: ', registration.scope);   			
    		}).catch(function(error){console.log('Service worker failed: ', error)});
    } else {
    	console.warn('ServiceWorkers are not supported');
    }
	
})();

menu.setup();
