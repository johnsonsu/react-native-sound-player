//
//  RNSoundPlayer
//
//  Created by Johnson Su on 2018-07-10.
//

#import "RNSoundPlayer.h"
#import <AVFoundation/AVFoundation.h>

@implementation RNSoundPlayer
{
    bool hasListeners;
}

static NSString *const EVENT_SETUP_ERROR = @"OnSetupError";
static NSString *const EVENT_FINISHED_LOADING = @"FinishedLoading";
static NSString *const EVENT_FINISHED_LOADING_FILE = @"FinishedLoadingFile";
static NSString *const EVENT_FINISHED_LOADING_URL = @"FinishedLoadingURL";
static NSString *const EVENT_FINISHED_PLAYING = @"FinishedPlaying";

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        self.loopCount = 0;
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(itemDidFinishPlaying:)
                                                     name:AVPlayerItemDidPlayToEndTimeNotification
                                                   object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSArray<NSString *> *)supportedEvents {
    return @[EVENT_FINISHED_PLAYING, EVENT_FINISHED_LOADING, EVENT_FINISHED_LOADING_URL, EVENT_FINISHED_LOADING_FILE, EVENT_SETUP_ERROR];
}

-(void)startObserving {
    hasListeners = YES;
}

-(void)stopObserving {
    hasListeners = NO;
}

RCT_EXPORT_METHOD(playUrl:(NSString *)url) {
    [self prepareUrl:url];
    if (self.avPlayer) {
        [self.avPlayer play];
    }
}

RCT_EXPORT_METHOD(loadUrl:(NSString *)url) {
    [self prepareUrl:url];
}

RCT_EXPORT_METHOD(playSoundFile:(NSString *)name ofType:(NSString *)type) {
    [self mountSoundFile:name ofType:type];
    if (self.player) {
        [self.player play];
    }
}

RCT_EXPORT_METHOD(playSoundFileWithDelay:(NSString *)name ofType:(NSString *)type delay:(double)delay) {
    [self mountSoundFile:name ofType:type];
    if (self.player) {
        [self.player playAtTime:(self.player.deviceCurrentTime + delay)];
    }
}

RCT_EXPORT_METHOD(loadSoundFile:(NSString *)name ofType:(NSString *)type) {
    [self mountSoundFile:name ofType:type];
}

RCT_EXPORT_METHOD(pause) {
    if (self.player != nil) {
        [self.player pause];
    }
    if (self.avPlayer != nil) {
        [self.avPlayer pause];
    }
}

RCT_EXPORT_METHOD(resume) {
    if (self.player != nil) {
        [self.player play];
    }
    if (self.avPlayer != nil) {
        [self.avPlayer play];
    }
}

RCT_EXPORT_METHOD(stop) {
    if (self.player != nil) {
        [self.player stop];
    }
    if (self.avPlayer != nil) {
        [self.avPlayer pause];
        [self.avPlayer seekToTime:kCMTimeZero];
    }
}

RCT_EXPORT_METHOD(seek:(float)seconds) {
    if (self.player != nil) {
        self.player.currentTime = seconds;
    }
    if (self.avPlayer != nil) {
        [self.avPlayer seekToTime:CMTimeMakeWithSeconds(seconds, NSEC_PER_SEC)];
    }
}

#if !TARGET_OS_TV
RCT_EXPORT_METHOD(setSpeaker:(BOOL)on) {
    AVAudioSession *session = [AVAudioSession sharedInstance];
    NSError *error = nil;
    if (on) {
        [session setCategory:AVAudioSessionCategoryPlayAndRecord error:&error];
        [session overrideOutputAudioPort:AVAudioSessionPortOverrideSpeaker error:&error];
    } else {
        [session setCategory:AVAudioSessionCategoryPlayback error:&error];
        [session overrideOutputAudioPort:AVAudioSessionPortOverrideNone error:&error];
    }
    [session setActive:YES error:&error];
    if (error) {
        [self sendErrorEvent:error];
    }
}
#endif

RCT_EXPORT_METHOD(setMixAudio:(BOOL)on) {
    AVAudioSession *session = [AVAudioSession sharedInstance];
    NSError *error = nil;
    if (on) {
        [session setCategory:AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionMixWithOthers error:&error];
    } else {
        [session setCategory:AVAudioSessionCategoryPlayback withOptions:0 error:&error];
    }
    [session setActive:YES error:&error];
    if (error) {
        [self sendErrorEvent:error];
    }
}

RCT_EXPORT_METHOD(setVolume:(float)volume) {
    if (self.player != nil) {
        [self.player setVolume:volume];
    }
    if (self.avPlayer != nil) {
        [self.avPlayer setVolume:volume];
    }
}

RCT_EXPORT_METHOD(setNumberOfLoops:(NSInteger)loopCount) {
    self.loopCount = loopCount;
    if (self.player != nil) {
        [self.player setNumberOfLoops:loopCount];
    }
}

RCT_REMAP_METHOD(getInfo,
                 getInfoWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    if (self.player != nil) {
        NSDictionary *data = @{
            @"currentTime": [NSNumber numberWithDouble:[self.player currentTime]],
            @"duration": [NSNumber numberWithDouble:[self.player duration]]
        };
        resolve(data);
    } else if (self.avPlayer != nil) {
        CMTime currentTime = [[self.avPlayer currentItem] currentTime];
        CMTime duration = [[[self.avPlayer currentItem] asset] duration];
        NSDictionary *data = @{
            @"currentTime": [NSNumber numberWithFloat:CMTimeGetSeconds(currentTime)],
            @"duration": [NSNumber numberWithFloat:CMTimeGetSeconds(duration)]
        };
        resolve(data);
    } else {
        resolve(nil);
    }
}

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    if (hasListeners) {
        [self sendEventWithName:EVENT_FINISHED_PLAYING body:@{@"success": [NSNumber numberWithBool:flag]}];
    }
}

- (void)itemDidFinishPlaying:(NSNotification *)notification {
    if (hasListeners) {
        [self sendEventWithName:EVENT_FINISHED_PLAYING body:@{@"success": [NSNumber numberWithBool:YES]}];
    }
}

- (void)mountSoundFile:(NSString *)name ofType:(NSString *)type {
    if (self.avPlayer) {
        self.avPlayer = nil;
    }

    NSString *soundFilePath = [[NSBundle mainBundle] pathForResource:name ofType:type];

    if (soundFilePath == nil) {
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        soundFilePath = [NSString stringWithFormat:@"%@.%@", [documentsDirectory stringByAppendingPathComponent:name], type];
    }

    NSURL *soundFileURL = [NSURL fileURLWithPath:soundFilePath];
    NSError *error = nil;
    self.player = [[AVAudioPlayer alloc] initWithContentsOfURL:soundFileURL error:&error];
    if (error) {
        [self sendErrorEvent:error];
        return;
    }
    [self.player setDelegate:self];
    [self.player setNumberOfLoops:self.loopCount];
    [self.player prepareToPlay];
    [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:&error];
    if (error) {
        [self sendErrorEvent:error];
        return;
    }
    if (hasListeners) {
        [self sendEventWithName:EVENT_FINISHED_LOADING body:@{@"success": [NSNumber numberWithBool:YES]}];
        [self sendEventWithName:EVENT_FINISHED_LOADING_FILE body:@{@"success": [NSNumber numberWithBool:YES], @"name": name, @"type": type}];
    }
}

- (void)prepareUrl:(NSString *)url {
    if (self.player) {
        self.player = nil;
    }
    NSURL *soundURL = [NSURL URLWithString:url];
    self.avPlayer = [[AVPlayer alloc] initWithURL:soundURL];
    [self.avPlayer.currentItem addObserver:self forKeyPath:@"status" options:0 context:nil];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    if (object == self.avPlayer.currentItem && [keyPath isEqualToString:@"status"] && hasListeners) {
        if (self.avPlayer.currentItem.status == AVPlayerItemStatusReadyToPlay) {
            [self sendEventWithName:EVENT_FINISHED_LOADING body:@{@"success": [NSNumber numberWithBool:YES]}];
            NSURL *url = [(AVURLAsset *)self.avPlayer.currentItem.asset URL];
            [self sendEventWithName:EVENT_FINISHED_LOADING_URL body:@{@"success": [NSNumber numberWithBool:YES], @"url": [url absoluteString]}];
        } else if (self.avPlayer.currentItem.status == AVPlayerItemStatusFailed) {
            [self sendErrorEvent:self.avPlayer.currentItem.error];
        }
    }
}

- (void)sendErrorEvent:(NSError *)error {
	if (hasListeners) {
	    [self sendEventWithName:EVENT_SETUP_ERROR body:@{@"error": [error localizedDescription]}];
	}
}

@end
