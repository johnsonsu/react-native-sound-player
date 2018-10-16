# react-native-sound-player

Play audio files, stream audio from URL, using ReactNative.

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

  - On iOS, drag and drop sound file into project in Xcode. Remember to check **"Copy items if needed"** option and **"Add to targets"**.
  - On Android, put sound files in `{project_root}/android/app/src/main/res/raw/`. Just create the folder if it doesn't exist.


2. Import the library and call the `playSoundFile(fileName, fileType)` function:

```javascript
import SoundPlayer from 'react-native-sound-player'

try {
  // play the file tone.mp3
  SoundPlayer.playSoundFile('tone', 'mp3')
  // or play from url
  SoundPlayer.playUrl('https://example.com/music.mp3')
} catch (e) {
  console.log(`cannot play the sound file`, e)
}
```


### Finished playing event

```javascript
...

// subscribe to the finished playing event in componentDidMount
componentDidMount() {
  SoundPlayer.onFinishedPlaying((success: boolean) => { // success is true when the sound is played
    console.log('finished playing', success)
  })
}

// unsubscribe when unmount
componentWillUnmount() {
  SoundPlayer.unmount()
}


}

...
```


## Functions

### playSound(fileName: string, fileType: string)
Play the sound file named `fileName` with file type `fileType`.

### playUrl(url: string)
Play the audio from url. Supported formats are:
  - [AVPlayer (iOS)](https://stackoverflow.com/questions/21879981/avfoundation-avplayer-supported-formats-no-vob-or-mpg-containers)
  - [MediaPlayer (Android)](https://developer.android.com/guide/topics/media/media-formats)

### onFinishedPlaying(callback: (success: boolean) => any)
Subscribe to the "finished playing" event. The `callback` function is called ever a file is finished playing.

### unmount()
Unsubscribe the "finished playing" event.

### pause()

Pause the currently playing file.

### resume()

Resume from pause and continue playing the same file.

### stop()

Stop playing, call `playSound(fileName: string, fileType: string)` to start playing again.

### getInfo() => Promise<{currentTime: number, duration: number}>

Get the `currentTime` and `duration` of the currently mounted audio media. This function returns a promise which resolves to an Object containing `currentTime` and `duration` properties.
```javascript
// Example
...
  playSong() {
    try {
      SoundPlayer.playSoundFile('engagementParty', 'm4a')
    } catch (e) {
      alert('Cannot play the file')
      console.log('cannot play the song file', e)
    }
  }

  async getInfo() { // You need to keyword async
    try {
      const info = await SoundPlayer.getInfo() // Also, you need to await this because it is async
      console.log('getInfo', info) // {duration: 12.416, currentTime: 7.691}
    } catch (e) {
      console.log('There is no song playing', e)
    }
  }

  onPressPlayButton() {
    this.playSong()
    this.getInfo()
  }

...
```

