var selector = '%selector%'; //类名（需要加.）|| id（需要加 #）||标签名

function wvt_onTimeUpdate(video) {
	var now = Date.now();
    if (now - window.wvt_lastNotifyVideoPlaying >= 1000) {
        if (video.muted) video.muted = false;
    	if (video.volume != 1) video.volume = 1;
        window.main.notifyVideoPlaying();
        window.wvt_lastNotifyVideoPlaying = now;
    }
}

function wvt_reportVideoSize(video) {
	window.main.setVideoSize(video.videoWidth, video.videoHeight);
}

function wvt_setupVideo(video) {
	if (video.wvt_setup) { return; }
	if (document.fullscreenElement) { document.exitFullscreen(); }
	window.main.enablePlayCheck();
	window.wvt_lastNotifyVideoPlaying = 0;
    video.autoplay = true;
    video.defaultMuted = false;
    video.style['object-fit'] = 'fill';
    if (!video.paused) {
    	console.log("Video is playing, enter fullscreen.");
    	window.main.schemeEnterFullscreen();
    	wvt_reportVideoSize(video);
    }
    video.addEventListener('play', function() {
    	console.log("Video state: PLAY.");
		window.main.schemeEnterFullscreen();
    	window.main.notifyVideoPlaying();
    	window.wvt_lastNotifyVideoPlaying = Date.now();
    });
    video.addEventListener('pause', function() { console.log("Video state: PAUSE."); });
    video.addEventListener('timeupdate', function() { wvt_onTimeUpdate(video); });
    video.addEventListener('error', function(e) { console.log("Video state: ERROR."); });
    video.addEventListener('canplay', function(e) {
		console.log("Video state: CANPLAY.");
		wvt_reportVideoSize(video);
	});
    //video.addEventListener('canplaythrough', function(e) { console.log("Video state: CANPLAYTHROUGH."); });
    //video.addEventListener('durationchange', function(e) { console.log("Video state: DURATIONCHANGE."); });
	video.wvt_setup = true;
}

function wvt_loop(counter) {
	if (window.wvt_video) {
		if (!document.documentElement.contains(window.wvt_video)) {
			console.warn("Loop " + counter + ", wvt_video is not on the document, set it to null.");
			window.main.disablePlayCheck();
			if (document.fullscreenElement) { document.exitFullscreen(); }
			window.wvt_video = null;
		} else {
			//console.log("Loop " + counter + ", nothing to do.");
		}
	} else {
		var element = document.querySelector(selector);
		if (element) {
			console.log("Loop " + counter + ", element [" + selector + "] found, tag name is " + element.tagName + ".");
			if (element.tagName == 'VIDEO') { wvt_setupVideo(element) }
			window.wvt_video = element;
		} else {
			console.error("Loop " + counter + ", element [" + selector + "] not found.");
		}
	}
	setTimeout(() => { wvt_loop(counter + 1) }, document.fullscreenElement ? 5000 : 500);
}

function wvt_fullscreenVideo() {
	if (document.fullscreenElement == null) {
		if (window.wvt_video && document.documentElement.contains(window.wvt_video)) {
			window.wvt_video.requestFullscreen(); 
		} else {
			console.error("wvt_video is null."); 
		}
	} else {
		console.error("already in fullscreen.");
	}
}

function wvt_main() {
	if (selector === "null" || selector === "%selector%") {
		console.error("selector [" + selector + "] is null.");
		return;
	}
	document.onkeydown = function(e) {
		console.log('Key down: ' + e.key);
		if (e.key == 'f') { wvt_fullscreenVideo(); }
	}
	setTimeout(() => { wvt_loop(0) }, 500);
}

if (!window.wvt_javascriptInjected) {
	console.log("WebViewTv javascript injected successfully.");
	window.wvt_javascriptInjected = true
	wvt_main();
} else {
	console.error("WebViewTv javascript already injected.");
}