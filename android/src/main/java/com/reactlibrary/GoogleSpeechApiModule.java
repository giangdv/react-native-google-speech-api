package com.reactlibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.reactlibrary.speech_service.SpeechEvent;
import com.reactlibrary.speech_service.SpeechService;
import com.reactlibrary.voice_recorder.VoiceEvent;
import com.reactlibrary.voice_recorder.VoiceRecorder;

import io.reactivex.disposables.CompositeDisposable;

public class GoogleSpeechApiModule extends ReactContextBaseJavaModule {

    private static final String TAG = "GoogleSpeechApi";
    private static final String KEY_TEXT = "text";
    private static final String KEY_IS_FINAL = "isFinal";
    private static final String ON_SPEECH_RECOGNIZED = "onSpeechRecognized";

    private VoiceRecorder voiceRecorder = new VoiceRecorder();
    private SpeechService speechService;
    private CompositeDisposable compositeDisposable;
    private String apiKey;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            speechService = SpeechService.from(service);

            compositeDisposable.add(
                    speechService.getSpeechEventObservable()
                            .subscribe(GoogleSpeechApiModule.this::handleSpeechEvent)
            );
            compositeDisposable.add(
                    voiceRecorder.getVoiceEventObservable()
                            .subscribe(GoogleSpeechApiModule.this::handleVoiceEvent)
            );

            voiceRecorder.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            speechService = null;
        }
    };

    public GoogleSpeechApiModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void start() {
        Log.i(TAG, "start");
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
        if (speechService == null) {
            Intent serviceIntent = new Intent(getReactApplicationContext(), SpeechService.class);
            getReactApplicationContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            throw new RuntimeException("Another instance of SpeechService is already running");
        }
    }

    @ReactMethod
    public void stop() {
        Log.i(TAG, "stop");
        voiceRecorder.stop();
        compositeDisposable.dispose();
        getReactApplicationContext().unbindService(serviceConnection);
        speechService = null;
    }

    @ReactMethod
    public void setApiKey(String apiKey) {
        Log.i(TAG, "setApiKey: " + apiKey);
        this.apiKey = apiKey;
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
        if (speechService != null) {
            try {
                speechService.startRecognizing(voiceRecorder.getSampleRate(), apiKey);
            } catch (Exception e) {
                speechService.startRecognizing(16000, apiKey);
            }
        }
    }

    private void onVoice(byte[] data, int size) {
        Log.i(TAG, "onVoice");
        if (speechService != null) {
            speechService.recognize(data, size);
        }
    }

    private void onVoiceEnd() {
        Log.i(TAG, "onVoiceEnd");
        if (speechService != null) {
            speechService.finishRecognizing();
        }
    }

    private void handleSpeechEvent(SpeechEvent speechEvent) {
        Log.i(TAG, speechEvent.getText() + " " + speechEvent.isFinal());
        WritableMap params = Arguments.createMap();

        if (!TextUtils.isEmpty(speechEvent.getText())) {
            params.putString(KEY_TEXT, speechEvent.getText());
        } else {
            params.putString(KEY_TEXT, "");
        }

        params.putBoolean(KEY_IS_FINAL, speechEvent.isFinal());
        sendJSEvent(getReactApplicationContext(), ON_SPEECH_RECOGNIZED, params);
        if (speechEvent.isFinal()) {
            stop();
        }
    }

    private void sendJSEvent(ReactContext reactContext,
                           String eventName,
                           WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}