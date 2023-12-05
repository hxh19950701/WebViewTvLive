package com.hxh19950701.webviewtvlive.adapter

class GdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("gdtv.cn")

    override fun javascript() = """javascript:
        document.onkeyup = function(e) {
            console.log(e.key);
            if (e.key == 'Enter') {
                var v = document.getElementsByTagName('video')[0];
            }
        }
    """.trimIndent()
}