package com.johnsonsu.rnsoundplayer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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


public class RNSoundPlayerModule extends ReactContextBaseJavaModule {

  public final static String EVENT_FINISHED_PLAYING = "FinishedPlaying";
  public final static String EVENT_FINISHED_LOADING = "FinishedLoading";
  public final static String EVENT_FINISHED_LOADING_FILE = "FinishedLoadingFile";
  public final static String EVENT_FINISHED_LOADING_URL = "FinishedLoadingURL";

  private final ReactApplicationContext reactContext;
  private MediaPlayer mediaPlayer;

  public RNSoundPlayerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNSoundPlayer";
  }

  @ReactMethod
  public void playSoundFile(String name, String type) throws IOException {
    mountSoundFile(name, type);
    this.mediaPlayer.start();
  }

  @ReactMethod
  public void loadSoundFile(String name, String type) throws IOException {
    mountSoundFile(name, type);
  }

  @ReactMethod
  public void playUrl(String url) throws IOException {
    prepareUrl(url);
    this.mediaPlayer.start();
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
  public void resume() throws IllegalStateException {
    if (this.mediaPlayer != null) {
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
      this.mediaPlayer.seekTo((int)seconds * 1000);
    }
  }

  @ReactMethod
  public void setVolume(float volume) throws IOException {
    if (this.mediaPlayer != null) {
      this.mediaPlayer.setVolume(volume, volume);
    }
  }

  @ReactMethod
  public void getInfo(
      Promise promise) {
    WritableMap map = Arguments.createMap();
    map.putDouble("currentTime", this.mediaPlayer.getCurrentPosition() / 1000.0);
    map.putDouble("duration", this.mediaPlayer.getDuration() / 1000.0);
    promise.resolve(map);
  }

  private void sendEvent(ReactApplicationContext reactContext,
                       String eventName,
                       @Nullable WritableMap params) {
    reactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(eventName, params);
  }

  private void mountSoundFile(String name, String type) throws IOException {
    if (this.mediaPlayer == null) {
      int soundResID = getReactApplicationContext().getResources().getIdentifier(name, "raw", getReactApplicationContext().getPackageName());

      if (soundResID > 0) {
        this.mediaPlayer = MediaPlayer.create(getCurrentActivity(), soundResID);
      } else {
        this.mediaPlayer = MediaPlayer.create(getCurrentActivity(), this.getUriFromFile(name, type));
      }

      this.mediaPlayer.setOnCompletionListener(
        new OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer arg0) {
            WritableMap params = Arguments.createMap();
            params.putBoolean("success", true);
            sendEvent(getReactApplicationContext(), EVENT_FINISHED_PLAYING, params);
          }
      });
    } else {
      Uri uri;
      int soundResID = getReactApplicationContext().getResources().getIdentifier(name, "raw", getReactApplicationContext().getPackageName());

      if (soundResID > 0) {
        uri = Uri.parse("android.resource://" + getReactApplicationContext().getPackageName() + "/raw/" + name);
      } else {
        uri = this.getUriFromFile(name, type);
      }

      this.mediaPlayer.reset();
      this.mediaPlayer.setDataSource(getCurrentActivity(), uri);
      this.mediaPlayer.prepare();
    }

    WritableMap params = Arguments.createMap();
    params.putBoolean("success", true);
    sendEvent(getReactApplicationContext(), EVENT_FINISHED_LOADING, params);
    WritableMap onFinishedLoadingFileParams = Arguments.createMap();
    onFinishedLoadingFileParams.putBoolean("success", true);
    onFinishedLoadingFileParams.putString("name", name);
    onFinishedLoadingFileParams.putString("type", type);
    sendEvent(getReactApplicationContext(), EVENT_FINISHED_LOADING_FILE, onFinishedLoadingFileParams);
  }

  private Uri getUriFromFile(String name, String type) {
    String folder = getReactApplicationContext().getFilesDir().getAbsolutePath();
    String file = name + "." + type;

    // http://blog.weston-fl.com/android-mediaplayer-prepare-throws-status0x1-error1-2147483648
    // this helps avoid a common error state when mounting the file
    File ref = new File(folder + "/" + file);

    if (ref.exists()) {
      ref.setReadable(true, false);
    }

    return Uri.parse("file://" + folder + "/" + file);
  }

  private void prepareUrl(String url) throws IOException {
    if (this.mediaPlayer == null) {
      Uri uri = Uri.parse(url);
      this.mediaPlayer = MediaPlayer.create(getCurrentActivity(), uri);
      this.mediaPlayer.setOnCompletionListener(
        new OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer arg0) {
            WritableMap params = Arguments.createMap();
            params.putBoolean("success", true);
            sendEvent(getReactApplicationContext(), EVENT_FINISHED_PLAYING, params);
          }
      });
    } else {
      Uri uri = Uri.parse(url);
      this.mediaPlayer.reset();
      this.mediaPlayer.setDataSource(getCurrentActivity(), uri);
      this.mediaPlayer.prepare();
    }
    WritableMap params = Arguments.createMap();
    params.putBoolean("success", true);
    sendEvent(getReactApplicationContext(), EVENT_FINISHED_LOADING, params);
    WritableMap onFinshedLoadingURLParams = Arguments.createMap();
    onFinshedLoadingURLParams.putBoolean("success", true);
    onFinshedLoadingURLParams.putString("url", url);
    sendEvent(getReactApplicationContext(), EVENT_FINISHED_LOADING_URL, onFinshedLoadingURLParams);
  }
}
