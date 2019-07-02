/**
 * @flow
 */
'use strict'

import { NativeModules, NativeEventEmitter, Platform } from 'react-native'
const { RNSoundPlayer } = NativeModules

const _soundPlayerEmitter = new NativeEventEmitter(RNSoundPlayer)
let _finishedPlayingListener = null
let _finishedLoadingListener = null

module.exports = {

  playSoundFile: (name: string, type: string) => {
    RNSoundPlayer.playSoundFile(name, type)
  },

  loadSoundFile: (name: string, type: string) => {
    RNSoundPlayer.loadSoundFile(name, type)
  },

  playUrl: (url: string) => {
    RNSoundPlayer.playUrl(url)
  },

  loadUrl: (url: string) => {
    RNSoundPlayer.loadUrl(url)
  },

  onFinishedPlaying: (callback: (success: boolean) => any) => {
    if (_finishedPlayingListener) {
      _finishedPlayingListener.remove()
      _finishedPlayingListener = undefined
    }

    _finishedPlayingListener = _soundPlayerEmitter.addListener(
      'FinishedPlaying',
      callback
    )
  },

  onFinishedLoading: (callback: (success: boolean) => any) => {
    if (_finishedLoadingListener) {
      _finishedLoadingListener.remove()
      _finishedLoadingListener = undefined
    }

    _finishedLoadingListener = _soundPlayerEmitter.addListener(
      'FinishedLoading',
      callback
    )
  },

  addEventListener: (eventName: 'FinishedLoading' | 'FinishedPlaying' | 'FinishedLoadingURL' | 'FinishedLoadingFile', callback: Function) => _soundPlayerEmitter.addListener(eventName, callback),

  play: () => {
    // play and resume has the exact same implementation natively
    RNSoundPlayer.resume()
  },

  pause: () => {
    RNSoundPlayer.pause()
  },

  resume: () => {
    RNSoundPlayer.resume()
  },

  stop: () => {
    RNSoundPlayer.stop()
  },

  seek: (seconds) => {
    RNSoundPlayer.seek(seconds)
  },

  setVolume: (volume: number) => {
    RNSoundPlayer.setVolume(volume)
  },

  setSpeaker: (on: boolean) => {
    if(Platform.OS === "android"){
      console.log("setSpeaker is not implement on Android");
    } else {
      RNSoundPlayer.setSpeaker(on);
    }
  },

  getInfo: async () => RNSoundPlayer.getInfo(),

  unmount: () => {
    if (_finishedPlayingListener) {
      _finishedPlayingListener.remove()
      _finishedPlayingListener = undefined
    }

    if (_finishedLoadingListener) {
      _finishedLoadingListener.remove()
      _finishedLoadingListener = undefined
    }
  }
}
