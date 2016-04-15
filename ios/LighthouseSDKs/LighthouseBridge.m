//
//  LighthouseBridge.m
//  Bluetrack
//
//  Created by Paul Bao on 2/09/2015.
//  Copyright (c) 2015 Facebook. All rights reserved.
//
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import "LighthouseBridge.h"
#import "LighthouseManager.h"

@implementation LighthouseBridge

// The React Native bridge needs to know our module
RCT_EXPORT_MODULE()

@synthesize bridge = _bridge;
RCT_EXPORT_METHOD(configure:(NSDictionary *)configuration) {
  [self stopMonitor];
  //launch Lighthouse SDK
  // Enable logging (optional)
  [LighthouseManager disableLogging];
  [LighthouseManager disableProduction];
  
  [[LighthouseManager sharedInstance] configure:configuration];
  
  [[LighthouseManager sharedInstance] requestPermission];
  
  // Listen to notifications from Lighthouse
  [[LighthouseManager sharedInstance] subscribe:@"LighthouseDidEnterBeacon" observer:self selector:@selector(didEnterBeacon:)];
  [[LighthouseManager sharedInstance] subscribe:@"LighthouseDidExitBeacon" observer:self selector:@selector(didExitBeacon:)];
  
  [[LighthouseManager sharedInstance] subscribe:@"LighthouseDidRangeBeacon" observer:self selector:@selector(didRangeBeacon:)];
}

RCT_EXPORT_METHOD(setProperties:(NSDictionary *)properties) {
  [[LighthouseManager sharedInstance] setProperties:properties];
}
RCT_EXPORT_METHOD(startMonitor) {
  // Start monitoring
  [[LighthouseManager sharedInstance] launch];
}

RCT_EXPORT_METHOD(stopMonitor) {
  [[LighthouseManager sharedInstance] removeAll:self];
  [[LighthouseManager sharedInstance] unload];
}
- (void)didEnterBeacon:(NSDictionary *)data {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"didEnterBeacon" body:data];
}
- (void)didExitBeacon:(NSDictionary *)data {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"didExitBeacon" body:data];
}
- (void)didRangeBeacon:(NSDictionary *)data {
  [self.bridge.eventDispatcher sendDeviceEventWithName:@"didRangeBeacon" body:data];
}
@end
