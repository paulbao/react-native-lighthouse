'use strict';

var {
  NativeModules,
  DeviceEventEmitter,
} = require('react-native');

var LighthouseBridge = NativeModules.LighthouseBridge;

class Lighthouse {
  static configure(configuration: Object) {
    LighthouseBridge.configure(configuration);
  }
  static launch() {
    LighthouseBridge.startMonitor()
  }
  static stop() {
    LighthouseBridge.stopMonitor()
  }
  static setProperties(properties: Object) {
  	LighthouseBridge.setProperties(properties)
  }
  static requestPushNotifications(send_id: String) {
  	LighthouseBridge.requestPushNotifications(send_id)
  }
}

module.exports = Lighthouse;
