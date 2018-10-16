/**
 * @flow
 */
'use strict'

import { NativeModules, NativeEventEmitter } from 'react-native'
const { RNSoundPlayer } = NativeModules

const _soundPlayerEmitter = new NativeEventEmitter(RNSoundPlayer)
let _finishedPlayingListener = null

module.exports = {
  playSoundFile: (name: string, type: string) => {
    RNSoundPlayer.playSoundFile(name, type)
  },

  playUrl: (url: string) => {
    RNSoundPlayer.playUrl(url)
  },

  onFinishedPlaying: (callback: (success: boolean) => any) => {
    _finishedPlayingListener = _soundPlayerEmitter.addListener(
      'FinishedPlaying',
      callback
    )
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

  getInfo: async () => RNSoundPlayer.getInfo(),

  unmount: () => {
    _finishedPlayingListener && _finishedPlayingListener.remove()
  }
}
