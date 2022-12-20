// RemoteService_aidl.aidl
package com.example.wrclibrary;

// Declare any non-default types here with import statements

interface RemoteService_aidl {
    int getPid();
    int getRandomNumber_remote();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}