# react-native-sound-player

Play audio files in ReactNative on iOS/Android.

## Installation

### 1. `yarn` or `npm`
```
    // yarn
    yarn add react-native-sound-player
    // or npm
    npm install --save react-native-sound-player
```
### 2. Link

```
    react-native link react-native-sound-player
```


## Usage


### Play sound with file name and type

1. Add sound files to iOS/Android.

  - On iOS, drag and drop sound file into project in Xcode. Remember to check "Copy items if needed" option.
  - On Android, put sound files in {project_root}/android/app/src/main/res/raw/. Just create the folder if it doesn't exist.


2. Import the library and call the `playSoundFile(fileName, fileType)` function:

```javascript
import SoundPlayer from 'react-native-sound-player';

// play the file tone.mp3
try {
  SoundPlayer.playSoundFile('tone', 'mp3');
} catch (e) {
  console.log(`cannot play the sound file`, e);
}
```


### Finished playing event

```javascript
...

// subscribe to the finished playing event in componentDidMount
componentDidMount() {
  SoundPlayer.onFinishedPlaying((success: boolean) => { // success is true when the sound is played
    console.log('finished playing', success);
  });
}

// unsubscribe when unmount
componentWillUnmount() {
  SoundPlayer.unmount();
}


}

...
```


## Function

### playSound(fileName: string, fileType: string)
Play the sound file named `fileName` with file type `fileType`.

### onFinishedPlaying(callback: (success: boolean) => any)
Subscribe to the "finished playing" event. The `callback` function is called ever a file is finished playing.

### unmount()
Unsubscribe the "finished playing" event.
