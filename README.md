# react-native-sound-player
Play sound file in ReactNative

## Installation

### yarn

    yarn add react-native-sound-player

### npm

    npm install --save react-native-sound-player

## Usage

### Play sound with file name and type

    import SoundPlayer from 'react-native-sound-player';

    const fileName = 'tone';
    const fileType = 'mp3';
    try {
      SoundPlayer.playSound(fileName, fileType);
    } catch (e) {
      console.log(`cannot play sound file ${fileName}.${fileType}`, e);
    }
