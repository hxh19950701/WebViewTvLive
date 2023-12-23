var selector = '%1$s'; //类名（需要加.）|| id（需要加 #）||标签名'

document.onkeydown = function(e) {

console.log('Key down: ' + e.key);

if (e.key == 'f') {

var element = document.querySelector(selector);
if (element) {
	element.requestFullscreen();
	console.log("Element tag name is " + element.tagName + ".");

	if (element.tagName == 'VIDEO') {
		window.main.enablePlayCheck();
		element.autoplay = true;
		element.defaultMuted = false;
		element.addEventListener('timeupdate', function() {
			element.muted = false;
			element.volume = 1;
			window.main.onVideoPlayTimeUpdate();
		});
		element.addEventListener('pause', function() {
			console.log("Video state: PAUSE.");
		});
		element.addEventListener('play', function() {
			console.log("Video state: PLAY.")
		});
		element.addEventListener('canplay', function(e) {
			console.log("Video state: CANPLAY.")
		});
		element.addEventListener('canplaythrough', function(e) {
            console.log("Video state: CANPLAYTHROUGH.")
        });
        element.addEventListener('error', function(e) {
            console.log("Video state: ERROR.")
        });
        element.addEventListener('durationchange', function(e) {
            console.log("Video state: DURATIONCHANGE.");
        });
		if (element.paused) {
			console.log("Video paused.")
		} else {
			console.log("Video is playing.")
		}
	}
} else {
	console.log("querySelector returned null, selector is " + selector + ".");
}

}
}

window.main.schemeEnterFullscreen();