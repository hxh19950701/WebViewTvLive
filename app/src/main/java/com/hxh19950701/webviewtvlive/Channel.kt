package com.hxh19950701.webviewtvlive

data class Channel(
    val name: String,
    val url: String,
    val x: Float = -1F,
    val y: Float = -1F,
    var groupName: String = "",
)

private fun MutableCollection<Channel>.addChannels(name: String, vararg channels: Channel): MutableCollection<Channel> {
    channels.forEach { it.groupName = name }
    addAll(channels)
    return this
}

val channels = mutableListOf<Channel>()
    .addChannels(
        "央视",
        Channel("CCTV-1", "https://tv.cctv.com/live/cctv1/"),
        Channel("CCTV-2", "https://tv.cctv.com/live/cctv2/"),
        Channel("CCTV-3", "https://tv.cctv.com/live/cctv3/"),
        Channel("CCTV-4", "https://tv.cctv.com/live/cctv4/"),
        Channel("CCTV-5", "https://tv.cctv.com/live/cctv5/"),
        Channel("CCTV-5+", "https://tv.cctv.com/live/cctv5plus/"),
        Channel("CCTV-6", "https://tv.cctv.com/live/cctv6/"),
        Channel("CCTV-7", "https://tv.cctv.com/live/cctv7/"),
        Channel("CCTV-8", "https://tv.cctv.com/live/cctv8/"),
        Channel("CCTV-9", "https://tv.cctv.com/live/cctvjilu/"),
        Channel("CCTV-10", "https://tv.cctv.com/live/cctv10/"),
        Channel("CCTV-11", "https://tv.cctv.com/live/cctv11/"),
        Channel("CCTV-12", "https://tv.cctv.com/live/cctv12/"),
        Channel("CCTV-13", "https://tv.cctv.com/live/cctv13/"),
        Channel("CCTV-14", "https://tv.cctv.com/live/cctv14/"),
        Channel("CCTV-15", "https://tv.cctv.com/live/cctv15/"),
        Channel("CCTV-16", "https://tv.cctv.com/live/cctv16/"),
        Channel("CCTV-17", "https://tv.cctv.com/live/cctv17/"),
        Channel("CCTV-4 欧洲", "https://tv.cctv.com/live/cctveurope/"),
        Channel("CCTV-4 美洲", "https://tv.cctv.com/live/cctvamerica/"),
    )
    .addChannels(
        "湖南",
        Channel("湖南经视", "https://live.mgtv.com/?channelId=280"),
        Channel("湖南电影", "https://live.mgtv.com/?channelId=221"),
        Channel("湖南娱乐", "https://live.mgtv.com/?channelId=344"),
        Channel("湖南都市", "https://live.mgtv.com/?channelId=346"),
        Channel("湖南电视剧", "https://live.mgtv.com/?channelId=484"),
        Channel("湖南国际", "https://live.mgtv.com/?channelId=229"),
        Channel("金鹰卡通", "https://live.mgtv.com/?channelId=287"),
        Channel("深圳卫视", "https://www.sztv.com.cn/dianshi.shtml?id=7867"),
    )
    .addChannels(
        "海外",
        Channel("东森新闻", "https://news.ebc.net.tw/live", 0.01F, 0.01F),
    )