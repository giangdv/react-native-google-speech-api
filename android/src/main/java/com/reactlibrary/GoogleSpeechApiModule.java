package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.reactlibrary.voice_recorder.VoiceEvent;
import com.reactlibrary.voice_recorder.VoiceRecorder;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class GoogleSpeechApiModule extends ReactContextBaseJavaModule {

    private static final String TAG = "GoogleSpeechApi";

    private VoiceRecorder voiceRecorder = new VoiceRecorder();
    private Disposable voiceRecorderDisposable;

    public GoogleSpeechApiModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void start() {
        Log.i(TAG, "start");
        if (voiceRecorderDisposable != null){
            voiceRecorderDisposable.dispose();
        }
        voiceRecorderDisposable =
                voiceRecorder.getVoiceEventObservable().subscribe(this::handleVoiceEvent);
        voiceRecorder.start();
    }

    @ReactMethod
    public void stop() {
        Log.i(TAG, "stop");
        voiceRecorder.stop();
        voiceRecorderDisposable.dispose();
    }

    @ReactMethod
    public void setApiKey(String apiKey) {
        Log.i(TAG, "setApiKey: " + apiKey);
    }

    @Override
    public String getName() {
        return TAG;
    }

    private void handleVoiceEvent(VoiceEvent event) {
        switch (event.getState()) {
            case START:
                onVoiceStart();
                break;
            case VOICE:
                onVoice(event.getData(), event.getSize());
                break;
            case END:
                onVoiceEnd();
                break;
        }
    }

    private void onVoiceStart() {
        Log.i(TAG, "onVoiceStart");
    }

    private void onVoice(byte[] data, int size) {
        Log.i(TAG, "onVoice");
    }

    private void onVoiceEnd() {
        Log.i(TAG, "onVoiceEnd");
    }
}