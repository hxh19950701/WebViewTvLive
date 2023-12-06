var video = document.getElementsByTagName('video')[0];
if (video) {
    console.log("Video tag found.");
    video.addEventListener('timeupdate', function() { console.log(video.currentTime); video.volume = 1 });
    video.addEventListener('pause', function() { console.log("New state: PAUSE.") });
    video.addEventListener('play', function() { console.log("New state: PLAY.") });
    if (video.paused) {
    	console.log("Video is not playing, waiting for play.")
        video.addEventListener('canplay', function(e) { window.main.schemeEnterFullscreen() });
    } else {
    	console.log("Video is playing already, enter fullscreen immediately.")
        window.main.schemeEnterFullscreen();
    }
    document.onkeyup = function(e) {
    	console.log(e.key);
    	if (e.key == 'Enter') { video.requestFullscreen() }
    }
} else {
    console.log("No video tag found.");
    window.main.schemeEnterFullscreen();
}