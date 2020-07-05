# IceVideo
Play Video In Minecraft Window

## 实现

目前使用[vlcj-5](https://github.com/caprica/vlcj "vlcj-5")的回调实现

目前仅提供LibVLC4.0 Windows x64版本

如需其他版本请自行下载提取[VLC 4.0](https://nightlies.videolan.org/ "vlc")


原始版本实现方式

使用JavaCV中的FFmpegFrameGrabber帧捕捉器捕捉每一帧画面并渲染到Minecraft窗口上

此方法可能会导致CPU占用率大幅上升

```java
FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(new File("1.mp4"));
fFmpegFrameGrabber.start();
int ftp = fFmpegFrameGrabber.getLengthInFrames();
double fps = fFmpegFrameGrabber.getFrameRate();
long sleepCnt = (int) (1000/fps);
```
