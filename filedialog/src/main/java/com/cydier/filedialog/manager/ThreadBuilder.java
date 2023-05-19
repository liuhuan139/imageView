package com.cydier.filedialog.manager;

import com.cydier.filedialog.interfaze.IThreadCompleteCallback;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ThreadBuilder {
    private static final int THREAD_TIMES = 10;

    private ThreadTask mTask;
    private ThreadTask task;
    private ThreadType mType;
    private ThreadType type;

    private IThreadCompleteCallback mCallback;
    private IThreadCompleteCallback callback;

    private ExecutorService mExecutorService;

    public enum ThreadType {
        SINGLE, TIMES, MANY
    }

    public void buildAndRun() {
        mTask = task;
        mType = type;
        initService(mType);
        mCallback = callback;
        run();
    }

    private void initService(ThreadType type) {
        switch (type) {
            case SINGLE ->mExecutorService =  Executors.newSingleThreadExecutor();
            case TIMES -> mExecutorService = Executors.newFixedThreadPool(THREAD_TIMES);
            case MANY -> mExecutorService = Executors.newCachedThreadPool();
        }
    }

    private void run() {
        if (mExecutorService != null && mTask != null) {
            mExecutorService.submit(mTask);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Object object = mTask.get();
                        if (object != null) {
                            mCallback.call(mTask.getTaskId(), object);
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        System.out.println("get future result error");
                    }
                }
            }, 0, 200);
        }
    }

    public ThreadBuilder setType(ThreadType type) {
        this.type = type;
        return this;
    }

    public ThreadBuilder setTask(ThreadTask task) {
        this.task = task;
        return this;
    }

    public ThreadBuilder setCallBack(IThreadCompleteCallback callback) {
        this.callback = callback;
        return this;
    }

    public static class ThreadTask extends FutureTask {
        private int taskId;

        public ThreadTask(Callable callable) {
            super(callable);
        }

        public void setTaskId(int id) {
            taskId = id;
        }

        public int getTaskId() {
            return taskId;
        }

    }

}
