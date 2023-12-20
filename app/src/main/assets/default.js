var video = document.getElementsByTagName('video')[0];
if (video) {
    console.log("Video tag found.");
    video.addEventListener('timeupdate', function() {
    	video.muted = false;
    	video.volume = 1;
    });
    video.addEventListener('pause', function() {
        console.log("New state: PAUSE.");
    });
    video.addEventListener('play', function() {
    	console.log("New state: PLAY.")
    	video.muted = false;
    	video.volume = 1
        window.main.schemeEnterFullscreen();
    });
    video.addEventListener('canplay', function(e) {
        console.log("New state: CANPLAY.")
        window.main.schemeEnterFullscreen();
    });
    if (video.paused) {
    	console.log("Video is not playing, waiting for play.")
    	video.autoplay = true;
    	video.play();
    } else {
    	console.log("Video is playing already, enter fullscreen immediately.")
    	video.muted = false;
        video.volume = 1
        window.main.schemeEnterFullscreen();
    }
    document.onkeyup = function(e) {
    	console.log('Key up: ' + e.key);
    	if (e.key == 'f') { video.requestFullscreen() }
    }
} else {
    console.log("No video tag found.");
    window.main.schemeEnterFullscreen();
}