package com.hxh19950701.webviewtvlive.adapter

class GdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("m.gdtv.cn")

    override fun javascript() = """javascript:
    var videoPlayerDiv = document.querySelector('.video-player');
      if (videoPlayerDiv) {
        videoPlayerDiv.style.width = '100%';
        videoPlayerDiv.style.height = '100%';
        videoPlayerDiv.style.position = 'absolute';
        videoPlayerDiv.style.top = '0';
        videoPlayerDiv.style.left = '0';
        videoPlayerDiv.style.zIndex = '9999';
      }
      var v = document.getElementsByTagName('video')[0];
      if (v == null) {
          console.log("No video tag found.");
      } else {
          console.log("Video tag found.");
          //v.addEventListener('pause', function() { setTimeout(() => { v.play(); }, 1000); });
      }
    """.trimIndent()

    override fun userAgent() = null
}