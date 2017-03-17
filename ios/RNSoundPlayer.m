//
//  RNSoundPlayer
//
//  Created by Johnson Su on 2017-02-07.
//  Copyright © 2017 Hsing Chong. All rights reserved.
//

#import "RNSoundPlayer.h"
#import <AVFoundation/AVFoundation.h>

@implementation RNSoundPlayer

RCT_EXPORT_METHOD(playSoundFile:(NSString *)name ofType:(NSString *)type)
{
  NSString *soundFilePath = [[NSBundle mainBundle] pathForResource:name ofType:type];
  NSURL *soundFileURL = [NSURL fileURLWithPath:soundFilePath];
  self.player = [[AVAudioPlayer alloc] initWithContentsOfURL:soundFileURL error:nil];
  [self.player setNumberOfLoops:0];
  [self.player prepareToPlay];
  [self.player play];
}

RCT_EXPORT_MODULE();

@end
