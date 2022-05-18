package com.sleticalboy.transform.bean;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created on 2022/5/18
 * <br/>
 * 测试类
 *
 * @author binlee
 */
public final class Foo implements Callable<List<String>>, Future<Image>, Runnable {

  @Override public List<String> call() throws Exception {
    return null;
  }

  @Override public void run() {
  }

  @Override public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override public boolean isCancelled() {
    return true;
  }

  @Override public boolean isDone() {
    return false;
  }

  @Override public Image get() throws ExecutionException, InterruptedException {
    return new Image(null, "", null);
  }

  @Override public Image get(long timeout, TimeUnit unit)
    throws ExecutionException, InterruptedException, TimeoutException {
    return null;
  }
}
