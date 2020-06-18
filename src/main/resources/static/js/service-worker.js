
console.log('service worker hello world');

function fixContextPath(ctxPath, uriArray) {
	if (ctxPath=='/') {
		/*relative urls cant start with a double slash*/
		return uriArray;
	} 
	for (uriIdx in uriArray) {
		uriArray[uriIdx] = ctxPath+uriArray[uriIdx];
	}
	return uriArray;
}

var cacheName = 'double-solitare-v1';
var filesToCache = [
	//HTML
	//'/',
	//CSS
	'/webjars/bootstrap/4.1.3/css/bootstrap.min.css',
	'/webjars/fontawesome/4.7.0/css/font-awesome.min.css',
	//'/css/doublesolitare.css',
	//JAVASCRIPT
	'/webjars/jquery/3.2.1/jquery.min.js',
	'/webjars/bootstrap/4.1.3/js/bootstrap.min.js',
	//'/js/doublesolitare.js',
	//'/js/sync.js',
	//'/js/DragDropTouch.js',
	//IMAGES
	'/img/1F0A1.png',
	'/img/1F0A2.png',
	'/img/1F0A3.png',
	'/img/1F0A4.png',
	'/img/1F0A5.png',
	'/img/1F0A6.png',
	'/img/1F0A7.png',
	'/img/1F0A8.png',
	'/img/1F0A9.png',
	'/img/1F0AA.png',
	'/img/1F0AB.png',
	'/img/1F0AD.png',
	'/img/1F0AE.png',
	'/img/1F0B1.png',
	'/img/1F0B2.png',
	'/img/1F0B3.png',
	'/img/1F0B4.png',
	'/img/1F0B5.png',
	'/img/1F0B6.png',
	'/img/1F0B7.png',
	'/img/1F0B8.png',
	'/img/1F0B9.png',
	'/img/1F0BA.png',
	'/img/1F0BB.png',
	'/img/1F0BD.png',
	'/img/1F0BE.png',
	'/img/1F0C1.png',
	'/img/1F0C2.png',
	'/img/1F0C3.png',
	'/img/1F0C4.png',
	'/img/1F0C5.png',
	'/img/1F0C6.png',
	'/img/1F0C7.png',
	'/img/1F0C8.png',
	'/img/1F0C9.png',
	'/img/1F0CA.png',
	'/img/1F0CB.png',
	'/img/1F0CD.png',
	'/img/1F0CE.png',
	'/img/1F0D1.png',
	'/img/1F0D2.png',
	'/img/1F0D3.png',
	'/img/1F0D4.png',
	'/img/1F0D5.png',
	'/img/1F0D6.png',
	'/img/1F0D7.png',
	'/img/1F0D8.png',
	'/img/1F0D9.png',
	'/img/1F0DA.png',
	'/img/1F0DB.png',
	'/img/1F0DD.png',
	'/img/1F0DE.png',
	'/img/back1.png'
];

self.addEventListener('install', function(e) {
	console.log('[ServiceWorker] Install');
	const ctxPath = new URL(location).searchParams.get('ctxPath');
	e.waitUntil(precache(ctxPath));
});


function precache(ctxPath) {
	// Open the cache
	caches.open(cacheName).then(function(cache){
		
		// Add all the default files to the cache
		console.log('[ServiceWorker] Caching app shell');
		fixContextPath(ctxPath, filesToCache);
		return cache.addAll(filesToCache);
	}).catch(function(error){console.log('[ServiceWorker] Error trying to pre-fetch files: ', error)})
}

function cacheFallbackToNetwork(e) {
	return caches.match(e.request).then(function(response){
		return response || fetch(e.request).catch(function(error){console.log('[ServiceWorker] Error trying to fetch file: '+e.request.url, error)});
	});
}

function cacheOnNetwork(e) {
	return caches.open(cacheName).then(function(cache) {
		
		//Check if the url is already in the cache
		return cache.match(e.request).then(function(response){
			return response || fetch(e.request).then(function(newResponse){
				// This really wasnt in the cache, lets cache it.
				//TODO only cache our stuff!
				cache.put(e.request, newResponse.clone());
				return newResponse;
			});
		});
	});
} 

self.addEventListener('activate', function(e) {
	console.log('[ServiceWorker] Activate');
	e.waitUntil(
			// Get all the cache keys (cacheName)
			caches.keys().then(function(keyList){
				return Promise.all(keyList.map(function(key){
					
					// If a cached item is saved under a previous cacheName
					if (key !== cacheName) {
						
						// Delete that cacahed file
						console.log('[ServiceWorker] Removing old cache', key);
						return caches.delete(key);
					}
				}))
			})
	);
	//return self.clients.claim();
});

self.addEventListener('fetch', function(e) {
	console.log('[ServiceWorker] Fetch', e.request.url);
	
	e.respondWith(
			cacheFallbackToNetwork(e)
	);
	
});
