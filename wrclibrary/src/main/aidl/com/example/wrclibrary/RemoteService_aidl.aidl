// RemoteService_aidl.aidl
package com.example.wrclibrary;

import com.example.wrclibrary.IProcessStateListener;

// Declare any non-default types here with import statements

interface RemoteService_aidl {
    int getRandomNumber_immediately();

    void registerListener(IProcessStateListener listener); // 注册接口
    void unregisterListener(IProcessStateListener listener); // 注销接口

}