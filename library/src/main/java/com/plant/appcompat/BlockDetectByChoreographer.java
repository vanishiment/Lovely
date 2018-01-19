package com.plant.appcompat;

import android.view.Choreographer;
import java.util.concurrent.TimeUnit;

/**
 * > 80ms 发生卡顿，抽样间隔 52ms
 * https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653579565&idx=1&
 * sn=ecc33d3675a8aef7c3dcc6b20e53989c&chksm=84b3bb2ab3c4323c16d41332c6906
 * d18144ba2afce47fea79981eadfaea3d87c2e332a36c6af&mpshare=1&scene=23&srcid
 * =0119XMpe6ViOqkkAdeaUO7dp#rd
 */
public class BlockDetectByChoreographer {

  public static void start() {
    Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
      long lastFrameTimeNanos = 0;
      long currentFrameTimeNanos = 0;

      @Override
      public void doFrame(long frameTimeNanos) {
        if(lastFrameTimeNanos == 0){
          lastFrameTimeNanos = frameTimeNanos;
        }
        currentFrameTimeNanos = frameTimeNanos;
        long diffMs = TimeUnit.MILLISECONDS.convert(currentFrameTimeNanos-lastFrameTimeNanos, TimeUnit.NANOSECONDS);
        if (diffMs > 80L) {
          LogMonitor.getInstance().logToFile();
        }
        if (LogMonitor.getInstance().isMonitor()) {
          LogMonitor.getInstance().removeMonitor();
        }
        LogMonitor.getInstance().startMonitor();
        Choreographer.getInstance().postFrameCallback(this);
      }
    });
  }

}
