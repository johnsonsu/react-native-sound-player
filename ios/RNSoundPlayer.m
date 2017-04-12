//
//  RNSoundPlayer
//
//  Created by Johnson Su on 2017-02-07.
//  Copyright © 2017 Hsing Chong. All rights reserved.
//

#import "RNSoundPlayer.h"

@implementation RNSoundPlayer

RCT_EXPORT_METHOD(playSoundFile:(NSString *)name ofType:(NSString *)type) {
  NSString *soundFilePath = [[NSBundle mainBundle] pathForResource:name ofType:type];
  NSURL *soundFileURL = [NSURL fileURLWithPath:soundFilePath];
  self.player = [[AVAudioPlayer alloc] initWithContentsOfURL:soundFileURL error:nil];
  [self.player setDelegate:self];
  [self.player setNumberOfLoops:0];
  [self.player prepareToPlay];
  [self.player play];
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[@"FinishedPlaying"];
}

- (void) audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
  [self sendEventWithName:@"FinishedPlaying" body:@{@"success": [NSNumber numberWithBool:flag]}];
}

RCT_EXPORT_MODULE();

@end
