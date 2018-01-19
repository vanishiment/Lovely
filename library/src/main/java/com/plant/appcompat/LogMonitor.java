package com.plant.appcompat;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

public class LogMonitor {

  private static LogMonitor sInstance = new LogMonitor();
  private static LogThread mLogThread = new LogThread("log",Process.THREAD_PRIORITY_BACKGROUND);
  private static Handler mIoHandler;
  private static final long TIME_BLOCK = 52L;
  private LogMonitor() {
    mLogThread.start();
    mIoHandler = new Handler(mLogThread.getLooper());
  }

  private static Runnable mLogRunnable = new Runnable() {
    @Override
    public void run() {

      StringBuilder sb = mLogThread.mStringThreadLocal.get();
      StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
      for (StackTraceElement s : stackTrace) {
        sb.append(s.toString()).append("\n");
      }

    }
  };

  public static LogMonitor getInstance() {
    return sInstance;
  }
  public boolean isMonitor() {
    return ReflectUtils.reflect(mIoHandler).method("hasCallbacks",mLogRunnable).get();
  }
  public void startMonitor() {
    mIoHandler.postDelayed(mLogRunnable, TIME_BLOCK);
  }
  public void removeMonitor() {
    mIoHandler.removeCallbacks(mLogRunnable);
  }

  public void logToFile(){
    String log = mLogThread.mStringThreadLocal.get().toString();
  }

  private static final class LogThread extends HandlerThread{

    ThreadLocal<StringBuilder> mStringThreadLocal = new ThreadLocal<>();

    public LogThread(String name) {
      this(name,Process.THREAD_PRIORITY_BACKGROUND);
    }

    public LogThread(String name, int priority) {
      super(name, priority);
      mStringThreadLocal.set(new StringBuilder());
    }
  }
}
