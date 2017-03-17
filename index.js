/**
 * @flow
 */
'use strict';

const { NativeModules } = require('react-native');
const { RNSoundPlayer } = NativeModules;

/**
 * RNSoundPlayer is a simple library that allows
 * ReactNative Apps to play audio files on
 * iOS and Android platform.
 */

module.exports = {
  playSoundFile: (name: string, type: string) => {
    RNSoundPlayer.playSoundFile(name, type);
  }
};
