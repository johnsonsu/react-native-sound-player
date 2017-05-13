package rnsoundplayer;

import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;


public class RNSoundPlayerModule extends ReactContextBaseJavaModule {

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
    if (this.mediaPlayer == null) {
      int soundResID = getReactApplicationContext().getResources().getIdentifier(name, "raw", getReactApplicationContext().getPackageName());
      this.mediaPlayer = MediaPlayer.create(getCurrentActivity(), soundResID);
    } else {
      Uri uri = Uri.parse("android.resource://" + getReactApplicationContext().getPackageName() + "/raw/" + name);
      this.mediaPlayer.reset();
      this.mediaPlayer.setDataSource(getCurrentActivity(), uri);
      this.mediaPlayer.prepare();
    }
    this.mediaPlayer.start();
  }

}
