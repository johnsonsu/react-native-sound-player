# react-native-sound-player

Play sound file in ReactNative on iOS and Android.

## Installation

### yarn
```
    yarn add react-native-sound-player
```
### npm

```
    npm install --save react-native-sound-player
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


## Function

### playSound(fileName: string, fileType: string)
