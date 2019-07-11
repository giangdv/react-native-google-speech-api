package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class GoogleSpeechApiModule extends ReactContextBaseJavaModule {

    private static final String TAG = "GoogleSpeechApi";

    public GoogleSpeechApiModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void start() {
        Log.i(TAG, "start");
    }

    @ReactMethod
    public void stop() {
        Log.i(TAG, "stop");
    }

    @ReactMethod
    public void setApiKey(String apiKey) {
        Log.i(TAG, "setApiKey: " + apiKey);
    }

    @Override
    public String getName() {
        return TAG;
    }
}