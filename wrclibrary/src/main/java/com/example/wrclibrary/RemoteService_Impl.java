package com.example.wrclibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class RemoteService_Impl extends Service {
    private static final String TAG = "wrc_library_remote";
    private final Random mGenerator = new Random();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the interface
        Log.i(TAG, "onBind!");
        return binder;
    }

    private final RemoteService_aidl.Stub binder = new RemoteService_aidl.Stub() {
        public int getPid(){
            return android.os.Process.myPid();
        }
        public int getRandomNumber_remote() {return mGenerator.nextInt(1000);}
        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                               float aFloat, double aDouble, String aString) {
            // Does nothing
        }
    };
}