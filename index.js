/**
 * @flow
 */
'use strict';

import {
  NativeModules,
  NativeEventEmitter
} from 'react-native';
const { RNSoundPlayer } = NativeModules;

const _soundPlayerEmitter = new NativeEventEmitter(RNSoundPlayer);
let _finishedPlayingListener = null;

module.exports = {
  playSoundFile: (name: string, type: string) => {
    RNSoundPlayer.playSoundFile(name, type);
  },

  onFinishedPlaying: (callback: (success: boolean) => any) => {
    _finishedPlayingListener =  _soundPlayerEmitter.addListener(
      'FinishedPlaying',
      callback
    )
  },

  unmount: () => {
    _finishedPlayingListener && _finishedPlayingListener.remove();
  }
};
