var selector = '%selector%'; //类名（需要加.）|| id（需要加 #）||标签名
var element = document.querySelector(selector);
if (element) {
	console.log("Element tag name is " + element.tagName + ".");

	document.onkeydown = function(e) {
    	console.log('Key down: ' + e.key);
    	if (e.key == 'f') { element.requestFullscreen(); }
    }

	if (element.tagName == 'VIDEO') {
		window.main.enablePlayCheck();
		console.log("video.paused = " + element.paused)
		element.autoplay = true;
		element.defaultMuted = false;
		element.addEventListener('timeupdate', function() {
			element.muted = false;
			element.volume = 1;
			window.main.notifyVideoPlaying();
		});
		element.addEventListener('pause', function() { console.log("Video state: PAUSE."); });
		element.addEventListener('play', function() {
			console.log("Video state: PLAY.");
			window.main.notifyVideoPlaying();
		});
		element.addEventListener('canplay', function(e) { console.log("Video state: CANPLAY."); });
		element.addEventListener('canplaythrough', function(e) { console.log("Video state: CANPLAYTHROUGH."); });
        element.addEventListener('error', function(e) { console.log("Video state: ERROR."); });
        element.addEventListener('durationchange', function(e) { console.log("Video state: DURATIONCHANGE."); });
	} else {
		console.log("Element tag name is " + element.tagName + ".");
	}
} else {
	console.log("querySelector return null, selector is " + selector + ".");
}
window.main.schemeEnterFullscreen();