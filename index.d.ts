declare module "react-native-sound-player" {
  import { EmitterSubscription } from "react-native";

  export type SoundPlayerEvent =
    | "OnSetupError"
    | "FinishedLoading"
    | "FinishedPlaying"
    | "FinishedLoadingURL"
    | "FinishedLoadingFile";

  export type SoundPlayerEventData = {
    success?: boolean;
    url?: string;
    name?: string;
    type?: string;
  };

  interface SoundPlayerType {
    playSoundFile: (name: string, type: string) => void;
    playSoundFileWithDelay: (name: string, type: string, delay: number) => void;
    loadSoundFile: (name: string, type: string) => void;
    playUrl: (url: string) => void;
    loadUrl: (url: string) => void;
    playAsset: (asset: number) => void;
    loadAsset: (asset: number) => void;
    /** @deprecated  please use addEventListener*/
    onFinishedPlaying: (callback: (success: boolean) => unknown) => void;
    /** @deprecated  please use addEventListener*/
    onFinishedLoading: (callback: (success: boolean) => unknown) => void;
    /** Subscribe to any event. Returns a subscription object. Subscriptions created by this function cannot be removed by calling unmount(). You NEED to call yourSubscriptionObject.remove() when you no longer need this event listener or whenever your component unmounts. */
    addEventListener: (
      eventName: SoundPlayerEvent,
      callback: (data: SoundPlayerEventData) => void
    ) => EmitterSubscription;
    /** Play the loaded sound file. This function is the same as `resume`. */
    play: () => void;
    /** Pause the currently playing file. */
    pause: () => void;
    /** Resume from pause and continue playing the same file. This function is the same as `play`. */
    resume: () => void;
    /** Stop playing, call `playSound` to start playing again. */
    stop: () => void;
    /** Seek to seconds of the currently playing file. */
    seek: (seconds: number) => void;
    /** Set the volume of the current player. This does not change the volume of the device. */
    setVolume: (volume: number) => void;
    /** Only available on iOS. Overwrite default audio output to speaker, which forces playUrl() function to play from speaker. */
    setSpeaker: (on: boolean) => void;
    /** Only available on iOS. If you set this option, your audio will be mixed with audio playing in background apps, such as the Music app. */
    setMixAudio: (on: boolean) => void;
    /** iOS: 0 means to play the sound once, a positive number specifies the number of times to return to the start and play again, a negative number indicates an indefinite loop. Android: 0 means to play the sound once, other numbers indicate an indefinite loop. */
    setNumberOfLoops: (loops: number) => void;
    /** Get the currentTime and duration of the currently mounted audio media. This function returns a promise which resolves to an Object containing currentTime and duration properties. */
    getInfo: () => Promise<{ currentTime: number; duration: number }>;
    /** @deprecated Please use addEventListener and remove your own listener by calling yourSubscriptionObject.remove(). */
    unmount: () => void;
  }

  const SoundPlayer: SoundPlayerType;

  export default SoundPlayer;
}
