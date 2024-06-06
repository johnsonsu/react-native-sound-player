package com.johnsonsu.rnsoundplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;

import java.io.File;

import java.io.IOException;
import javax.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.LifecycleEventListener;


public class RNSoundPlayerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  public final static String EVENT_SETUP_ERROR = "OnSetupError";
  public final static String EVENT_FINISHED_PLAYING = "FinishedPlaying";
  public final static String EVENT_FINISHED_LOADING = "FinishedLoading";
  public final static String EVENT_FINISHED_LOADING_FILE = "FinishedLoadingFile";
  public final static String EVENT_FINISHED_LOADING_URL = "FinishedLoadingURL";

  private final ReactApplicationContext reactContext;
  private MediaPlayer mediaPlayer;
  private float volume;
  private AudioManager audioManager;

  public RNSoundPlayerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.volume = 1.0f;
    this.audioManager = (AudioManager) this.reactContext.getSystemService(Context.AUDIO_SERVICE);
    reactContext.addLifecycleEventListener(this);
  }

  @Override
  public String getName() {
    return "RNSoundPlayer";
  }

  @ReactMethod
  public void setSpeaker(Boolean on) {
    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    audioManager.setSpeakerphoneOn(on);
  }

  @Override
  public void onHostResume() {
  }

  @Override
  public void onHostPause() {
  }

  @Override
  public void onHostDestroy() {

    this.stop();
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }

  @ReactMethod
  public void playSoundFile(String name, String type) throws IOException {
    mountSoundFile(name, type);
    this.resume();
  }

  @ReactMethod
  public void loadSoundFile(String name, String type) throws IOException {
    mountSoundFile(name, type);
  }

  @ReactMethod
  public void playUrl(String url) throws IOException {
    prepareUrl(url);
    this.resume();
  }

  @ReactMethod
  public void loadUrl(String url) throws IOException {
    prepareUrl(url);
  }

  @ReactMethod
  public void pause() throws IllegalStateException {
    if (this.mediaPlayer != null) {
      this.mediaPlayer.pause();
    }
  }

  @ReactMethod
  public void resume() throws IOException, IllegalStateException {
    if (this.mediaPlayer != null) {
      this.setVolume(this.volume);
      this.mediaPlayer.start();
    }
  }

  @ReactMethod
  public void stop() throws IllegalStateException {
    if (this.mediaPlayer != null) {
      this.mediaPlayer.stop();
    }
  }

  @ReactMethod
  public void seek(float seconds) throws IllegalStateException {
    if (this.mediaPlayer != null) {
      this.mediaPlayer.seekTo((int) seconds * 1000);
    }
  }

  @ReactMethod
  public void setVolume(float volume) throws IOException {
    this.volume = volume;
    if (this.mediaPlayer != null) {
      this.mediaPlayer.setVolume(volume, volume);
    }
  }

  @ReactMethod
  public void getInfo(
          Promise promise) {
    if (this.mediaPlayer == null) {
      promise.resolve(null);
      return;
    }
    WritableMap map = Arguments.createMap();
    map.putDouble("currentTime", this.mediaPlayer.getCurrentPosition() / 1000.0);
    map.putDouble("duration", this.mediaPlayer.getDuration() / 1000.0);
    promise.resolve(map);
  }

  @ReactMethod
  public void addListener(String eventName) {
    // Set up any upstream listeners or background tasks as necessary
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    // Remove upstream listeners, stop unnecessary background tasks
  }

  private void sendEvent(ReactApplicationContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }

  private void mountSoundFile(String name, String type) throws IOException {
    try {
      Uri uri;
      int soundResID = getReactApplicationContext().getResources().getIdentifier(name, "raw", getReactApplicationContext().getPackageName());

      if (soundResID > 0) {
        uri = Uri.parse("android.resource://" + getReactApplicationContext().getPackageName() + "/raw/" + name);
      } else {
        uri = this.getUriFromFile(name, type);
      }

      if (this.mediaPlayer == null) {
        this.mediaPlayer = initializeMediaPlayer(uri);
      } else {
        this.mediaPlayer.reset();
        this.mediaPlayer.setDataSource(getCurrentActivity(), uri);
        this.mediaPlayer.prepare();
      }
      sendMountFileSuccessEvents(name, type);
    } catch (IOException e) {
      sendErrorEvent(e);
    }
  }

  private Uri getUriFromFile(String name, String type) {
    String folder = getReactApplicationContext().getFilesDir().getAbsolutePath();
    String file = (!type.isEmpty()) ? name + "." + type : name;

    // http://blog.weston-fl.com/android-mediaplayer-prepare-throws-status0x1-error1-2147483648
    // this helps avoid a common error state when mounting the file
    File ref = new File(folder + "/" + file);

    if (ref.exists()) {
      ref.setReadable(true, false);
    }

    return Uri.parse("file://" + folder + "/" + file);
  }

  private void prepareUrl(final String url) throws IOException {
    try {
      if (this.mediaPlayer == null) {
        Uri uri = Uri.parse(url);
        this.mediaPlayer = initializeMediaPlayer(uri);
        this.mediaPlayer.setOnPreparedListener(
                new OnPreparedListener() {
                  @Override
                  public void onPrepared(MediaPlayer mediaPlayer) {
                    WritableMap onFinishedLoadingURLParams = Arguments.createMap();
                    onFinishedLoadingURLParams.putBoolean("success", true);
                    onFinishedLoadingURLParams.putString("url", url);
                    sendEvent(getReactApplicationContext(), EVENT_FINISHED_LOADING_URL, onFinishedLoadingURLParams);
                  }
                }
        );
      } else {
        Uri uri = Uri.parse(url);
        this.mediaPlayer.reset();
        this.mediaPlayer.setDataSource(getCurrentActivity(), uri);
        this.mediaPlayer.prepare();
      }
      WritableMap params = Arguments.createMap();
      params.putBoolean("success", true);
      sendEvent(getReactApplicationContext(), EVENT_FINISHED_LOADING, params);
    } catch (IOException e) {
      WritableMap errorParams = Arguments.createMap();
      errorParams.putString("error", e.getMessage());
      sendEvent(getReactApplicationContext(), EVENT_SETUP_ERROR, errorParams);
    }
  }

  private MediaPlayer initializeMediaPlayer(Uri uri) throws IOException {
    MediaPlayer mediaPlayer = MediaPlayer.create(getCurrentActivity(), uri);

    if (mediaPlayer == null) {
      throw new IOException("Failed to initialize MediaPlayer for URI: " + uri.toString());
    }

    mediaPlayer.setOnCompletionListener(
            new OnCompletionListener() {
              @Override
              public void onCompletion(MediaPlayer arg0) {
                WritableMap params = Arguments.createMap();
                params.putBoolean("success", true);
                sendEvent(getReactApplicationContext(), EVENT_FINISHED_PLAYING, params);
              }
            }
    );

    return mediaPlayer;
  }

  private void sendMountFileSuccessEvents(String name, String type) {
    WritableMap params = Arguments.createMap();
    params.putBoolean("success", true);
    sendEvent(reactContext, EVENT_FINISHED_LOADING, params);

    WritableMap onFinishedLoadingFileParams = Arguments.createMap();
    onFinishedLoadingFileParams.putBoolean("success", true);
    onFinishedLoadingFileParams.putString("name", name);
    onFinishedLoadingFileParams.putString("type", type);
    sendEvent(reactContext, EVENT_FINISHED_LOADING_FILE, onFinishedLoadingFileParams);
  }


  private void sendErrorEvent(IOException e) {
    WritableMap errorParams = Arguments.createMap();
    errorParams.putString("error", e.getMessage());
    sendEvent(reactContext, EVENT_SETUP_ERROR, errorParams);
  }
}
