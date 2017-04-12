//
//  RNSoundPlayer
//
//  Created by Johnson Su on 2017-02-07.
//  Copyright © 2017 Hsing Chong. All rights reserved.
//

#import <React/RCTBridgeModule.h>
#import <AVFoundation/AVFoundation.h>
#import <React/RCTEventEmitter.h>

@interface RNSoundPlayer : RCTEventEmitter <RCTBridgeModule, AVAudioPlayerDelegate>
@property (nonatomic, strong) AVAudioPlayer *player;
@end
