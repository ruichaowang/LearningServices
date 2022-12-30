package com.example.wrclibrary;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RemoteService_Impl extends Service {
    private static final String TAG = "wrc_library_remote";
    private final Random mGenerator = new Random();
    private IProcessStateListener mProcessStateListener;
    private boolean isServiceDestroy = false;                //当前服务是否结束
    Thread thread1 = new Thread(new ServiceWorker());       // 随机数生成

    @Override
    public void onCreate() {
        super.onCreate();

        thread1.start();
        Log.i(TAG, "onCreate， 开启随机数生成");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the interface
        Log.i(TAG, "onBind!");
        return binder;
    }


    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.i(TAG, "unbindService");
    }

    // 每 2s 生成随机数
    private class ServiceWorker implements Runnable {
        @Override public void run() {
            while (!isServiceDestroy) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int num = mGenerator.nextInt(100);
            Log.i(TAG, "不断生成的随机数为 = " + num);
            playCallback(num);
            }
        }
    }


    @Override public void onDestroy() {
        isServiceDestroy = true;
        super.onDestroy();
        Log.e(TAG,"onDestroy， 关闭随机数生成");
    }


    //创建一个RemoteCallbackList用来管理PlayCallback，用来通过Broadcast发送回调到客户端
    RemoteCallbackList<IProcessStateListener> remoteCallbackList = new RemoteCallbackList<>();


    // 更新 aidl 文件要记得编译
    private final RemoteService_aidl.Stub binder = new RemoteService_aidl.Stub() {
        // 当前是直接返回数值
        // 假设执行一个 5s+的 耗时操作，再 return 则会导致 app isn't responding ，所以这个方案不能满足长耗时操作的需求
        public int getRandomNumber_immediately() {
            try {
                TimeUnit.SECONDS.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mGenerator.nextInt(10);
        }

        @Override
        public void registerListener(IProcessStateListener listener) throws RemoteException {
            remoteCallbackList.register(listener);
            Log.i(TAG, "注册接口完成");
        }

        @Override
        public void unregisterListener(IProcessStateListener listener) throws RemoteException {
            remoteCallbackList.unregister(listener);
            Log.i(TAG, "接口注销完成");
        }
    };

    /**
     *调用回调到客户端
     */
    private void playCallback(int num) {
        //准备开始调用最近注册的Callback并且返回，已注册playCallback数（在客户端注册），目前是1个
        int N = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                remoteCallbackList.getBroadcastItem(i).onProcessFinished(num);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //必须和 beginBroadcast()成对出现？
        remoteCallbackList.finishBroadcast();
    }

}