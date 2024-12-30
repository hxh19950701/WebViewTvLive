<div align="center">
    <h1>WebView 电视</h1>
<div align="center">

![GitHub Repo stars](https://img.shields.io/github/stars/hxh19950701/WebViewTvLive)
![GitHub top language](https://img.shields.io/github/languages/top/hxh19950701/WebViewTvLive)
![GitHub repo size](https://img.shields.io/github/repo-size/hxh19950701/WebViewTvLive)
![GitHub Release](https://img.shields.io/github/v/release/hxh19950701/WebViewTvLive)


</div>
    <p>使用 腾讯X5 WebView 开发的电视直播App</p>
</div>
    <p>原理：加载官方直播网页，查找网页中的视频元素，然后自动全屏。</p>

    
<img src="./images/image_1.jpg"/>
<br/>
<img src="./images/image_2.jpg"/>


## 功能

- 自动更新频道列表
- 多直播源
- 播放异常自动换源/刷新
- 应用自定义设置
- 适配手机、平板和电视
- 上下键换台反转

## 待开发的功能
- 数字键换台
- 自定义播放列表源
- 局域网远程设置
- 手势调节屏幕亮度和音量
- 适配回看功能
- 自主选择是否启用TBS内核

## 获取应用
- GitHub <br>
https://github.com/hxh19950701/WebViewTvLive/releases <br>
- 微信公众号 <br>
不方便访问 GitHub 的朋友，也可关注微信公众号 “**网页电视**” 获取最新版下载链接。 <br>
- **本项目的发布地址仅有以上两个，对于其他渠道发布的此应用，均与开发者无关。**<br>

## 优势

- 直接使用官网直播链接，非常稳定可靠
- 兼容 m3u8 链接

## 缺点

- 比起直接加载直播源，载入频道稍长，需要等待一定时间
- 使用的是网页播放器播放，对设备性能有一定的要求
- 对低版本 Android 设备兼容性比较差，建议 Android 9 或以上的设备
- 无法选择视频清晰度，大部分为 720P，少部分为 1080P
- 官网直播，大部分为 24FPS，基本没有 50FPS

## FAQ

**Q: 在我的电视机/盒子上无法自动全屏？**<br/>
A: 一般安装 X5 内核即可解决此问题。按返回键打开左侧边栏，进入设置 - TBS 调试界面，然后选择“安装线上内核”即可。<br/>
请注意 TBS 调试界面没有选中状态，遥控器用户可以盲操作：按两下右键，再按确定键。<br/>
<br/>
**Q: 访问央视网提示“您当前的浏览器不支持视频播放，请升级浏览器或更换设备（如果是360、 QQ 或搜狗浏览器，须使用极速模式观看）”？**<br/>
A: 系统自带的 WebView 版本太低，安装 X5 内核或者升级设备的 WebView 即可解决。<br/>
<br/>
**Q: 在TBS调试界面“安装线上内核”老是失败？**<br/>
A: 可能刚好遇到了官方 X5 内核下载限流时间段，请换个时间段重试。参见[《关于官网X5内核SDK加载不稳定问题说明》](https://doc.weixin.qq.com/doc/w3_AGoAtwbdAFwlo0hmqkbTl6p19tCOV)。<br/>
<br/>
**Q: 尝试了以上所有的办法，在我的设备上依然闪退/无法自动全屏？**<br/>
A: 如果是魔百盒设备，可以刷其他固件来解决问题。其他设备，请去这里反馈。https://github.com/hxh19950701/WebViewTvLive/issues/34<br/>
<br/>
**Q: 换台速度特别慢，能不能优化？**<br/>
A: 应用的工作原理决定了换台速度不会快。换台速度取决于设备的性能，无法优化。对于低性能的设备，在设置内打开“无图模式”，或许可以得到些许的性能提升。<br/>
<br/>
**Q: 有没有适配 Android 5.0 以下设备的打算？**<br/>
A: 没有这个打算。这些设备的性能通常都不高，WebView 版本也比较低而且无法升级，即便适配了，也无法使用。<br/>
<br/>
**Q: 我打开怎么只有 CCTV-1？**<br/>
A: 出现这个问题，说明无法加载在线列表。试试先清除应用数据，如果不能解决，请等待一会重试。若连续几天问题持续出现，请联系开发者。<br/>

## 注意
- 请尊重作者，源码仅供您学习、交流使用。<br/>
- 对于二次打包的应用，仅限自用，或者在注明原 App 名称的情况下适度分享。<br/>
- 不要利用此项目牟利。<br/>

## 声明
- 本项目中收录的官方直播地址均为网友自行收集提交，若无意中侵犯了版权方的权益，请联系开发者删除。<br/>

## 捐赠
如果项目对您有帮助，欢迎捐赠开发者。<br/>
<a href="DonationList.md">查看已捐赠名单</a>
<br/>
<br/>
<img src="./images/image_5.png"/>