package com.hxh19950701.webviewtvlive

import com.hxh19950701.webviewtvlive.playlist.Channel

val fullChannels = mutableListOf<Channel>()
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
        Channel("金鹰纪实", "https://live.mgtv.com/?channelId=316"),
        Channel("湖南爱晚", "https://live.mgtv.com/?channelId=216"),
        Channel("先锋乒羽", "https://live.mgtv.com/?channelId=329"),
        Channel("快乐垂钓", "https://live.mgtv.com/?channelId=218"),
        Channel("茶频道", "https://live.mgtv.com/?channelId=578"),
        Channel("长沙新闻综合", "https://live.mgtv.com/?channelId=269"),
        Channel("长沙政法", "https://live.mgtv.com/?channelId=254"),
        Channel("长沙女性", "https://live.mgtv.com/?channelId=230"),
    )
    .addChannels(
        "广东",
        Channel("广东卫视", "https://www.gdtv.cn/tvChannelDetail/43"),
        Channel("深圳卫视", "https://www.sztv.com.cn/dianshi.shtml?id=7867"),
        Channel("深圳都市", "https://www.sztv.com.cn/dianshi.shtml?id=7868"),
        Channel("深圳电视剧", "https://www.sztv.com.cn/dianshi.shtml?id=7880"),
        Channel("深圳公共", "https://www.sztv.com.cn/dianshi.shtml?id=7874"),
        Channel("深圳娱乐", "https://www.sztv.com.cn/dianshi.shtml?id=7872"),
        Channel("深圳财经", "https://www.sztv.com.cn/dianshi.shtml?id=7871"),
        Channel("深圳国际", "https://www.sztv.com.cn/dianshi.shtml?id=7944"),
        Channel("深圳少儿", "https://www.sztv.com.cn/dianshi.shtml?id=7881"),
    )
    .addChannels(
        "海外",
        Channel("东森新闻", "https://news.ebc.net.tw/live", 0.01F, 0.01F),
    )