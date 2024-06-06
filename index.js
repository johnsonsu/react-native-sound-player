/**
 * @flow
 */
"use strict";

import { NativeModules, NativeEventEmitter, Platform } from "react-native";
import resolveAsset from 'react-native/Libraries/Image/resolveAssetSource';
const { RNSoundPlayer } = NativeModules;

const _soundPlayerEmitter = new NativeEventEmitter(RNSoundPlayer);
let _finishedPlayingListener = null;
let _finishedLoadingListener = null;

export default {
  playSoundFile: (name: string, type: string) => {
    RNSoundPlayer.playSoundFile(name, type);
  },

  playSoundFileWithDelay: (name: string, type: string, delay: number) => {
    RNSoundPlayer.playSoundFileWithDelay(name, type, delay);
  },

  loadSoundFile: (name: string, type: string) => {
    RNSoundPlayer.loadSoundFile(name, type);
  },

  setNumberOfLoops: (loops: number) => {
    RNSoundPlayer.setNumberOfLoops(loops);
  },

  playUrl: (url: string) => {
    RNSoundPlayer.playUrl(url);
  },

  loadUrl: (url: string) => {
    RNSoundPlayer.loadUrl(url);
  },
  
  playAsset: async (asset: number) => {
    if (!(__DEV__) && Platform.OS === "android") {
      RNSoundPlayer.playSoundFile(resolveAsset(asset).uri, '');
    } else {
      RNSoundPlayer.playUrl(resolveAsset(asset).uri); 
    } 
  },
  
  loadAsset: (asset: number) => {
    if (!(__DEV__) && Platform.OS === "android") {
      RNSoundPlayer.loadSoundFile(resolveAsset(asset).uri, '');
    } else {
      RNSoundPlayer.loadUrl(resolveAsset(asset).uri); 
    }
  },

  onFinishedPlaying: (callback: (success: boolean) => any) => {
    if (_finishedPlayingListener) {
      _finishedPlayingListener.remove();
      _finishedPlayingListener = undefined;
    }

    _finishedPlayingListener = _soundPlayerEmitter.addListener(
        "FinishedPlaying",
        callback
    );
  },

  onFinishedLoading: (callback: (success: boolean) => any) => {
    if (_finishedLoadingListener) {
      _finishedLoadingListener.remove();
      _finishedLoadingListener = undefined;
    }

    _finishedLoadingListener = _soundPlayerEmitter.addListener(
      "FinishedLoading",
      callback
    );
  },

  addEventListener: (
    eventName:
      | "OnSetupError"
      | "FinishedLoading"
      | "FinishedPlaying"
      | "FinishedLoadingURL"
      | "FinishedLoadingFile",
    callback: Function
  ) => _soundPlayerEmitter.addListener(eventName, callback),

  play: () => {
    // play and resume has the exact same implementation natively
    RNSoundPlayer.resume();
  },

  pause: () => {
    RNSoundPlayer.pause();
  },

  resume: () => {
    RNSoundPlayer.resume();
  },

  stop: () => {
    RNSoundPlayer.stop();
  },

  seek: (seconds: number) => {
    RNSoundPlayer.seek(seconds);
  },

  setVolume: (volume: number) => {
    RNSoundPlayer.setVolume(volume);
  },

  setSpeaker: (on: boolean) => {
    RNSoundPlayer.setSpeaker(on);
  },

  setMixAudio: (on: boolean) => {
    if (Platform.OS === "android") {
      console.log("setMixAudio is not implemented on Android");
    } else {
      RNSoundPlayer.setMixAudio(on);
    }
  },

  getInfo: async () => RNSoundPlayer.getInfo(),

  unmount: () => {
    if (_finishedPlayingListener) {
      _finishedPlayingListener.remove();
      _finishedPlayingListener = undefined;
    }

    if (_finishedLoadingListener) {
      _finishedLoadingListener.remove();
      _finishedLoadingListener = undefined;
    }
  },
};
