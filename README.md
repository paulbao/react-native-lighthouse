# react-native-lighthouse
**react-native-lighthouse** is built to provide an easy interface to the native [Lighthouse](http://lighthouse.io) libraries on both **iOS** and **Android**.

## Installation

1. Add `"react-native-lighthouse": "https://github.com/paulbao/react-native-lighthouse.git"` under `dependencies` in your package.json
2. run `npm install react-native-lighthouse`

### Manual installation Android

* Add the following in `android/setting.gradle`

```
   include ':react-native-lighthouse'
   project(':react-native-lighthouse').projectDir = new File(settingsDir, '../node_modules/react-native-lighthouse/android')
   ```
* In `android/build.gradle`

```
buildscript {
    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.1'

        classpath 'com.google.gms:google-services:1.5.0-beta3' // <- Add this line
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
allprojects {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url "$projectDir/../../node_modules/react-native/android"
        }
        flatDir {                                                                               // <- Add this line
            dirs '$projectDir/../../../node_modules/react-native-lighthouse/android/libs'       // <- Add this line
        }                                                                                       // <- Add this line
    }
}
```

* In `android/app/build.gradle`
```
apply plugin: "com.android.application"
apply plugin: 'com.google.gms.google-services'  // <- Add this line

import com.android.build.OutputFile

...

    defaultConfig {
        applicationId "com.lighthouseapp"
        minSdkVersion 16
        minSdkVersion 18    // <- make sure it's greater than 18
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
...
            }
        }
    }
    lintOptions {
        abortOnError false
    }

}

dependencies {
    compile 'com.google.android.gms:play-services-gcm:8.3.0' <- Add this line
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:+"  
    compile project(':react-native-lighthouse') // <- Add this line
```

* In 'android/app/src/main/AndroidManifest.xml`, add these lines, be sure to change com.xxx.yyy to your package

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="<YOUR_PACKAGE_NAME>"
    xmlns:tools="http://schemas.android.com/tools">

...
      <provider
        android:name="com.inlight.lighthousesdk.provider.LighthouseProvider"
        android:authorities="${applicationId}"
        tools:replace="android:authorities" />
```

* In your `MainActivity.java`
```
import com.lighthousesdks.LighthouseReactPackage;   // <- Add this line

...

            new MainReactPackage(),
            new LighthouseReactPackage()    // <- Add this line
```

* add below `ContentProviderAuthority.java` under package `com.inlight.lighthouse`. Don't forget to change <YOUR_PACKAGE_NAME>

```
package com.inlight.lighthouse;


import <YOUR_PACKAGE_NAME>.BuildConfig;

public class ContentProviderAuthority {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
}

```
* GCM API KEY

   By following [Cloud messaging](https://developers.google.com/cloud-messaging/android/client), you can get `google-services.json` file and place it in android/app directory
   
### Manual installation iOS

* Drag `node_modules/react-native-lighthouse/ios/LighthouseSDKs.xcodeproj` to your project on Xcode (usually under the Libraries group on Xcode)

* Drag `node_modules/react-native-lighthouse/ios/LighthouseSDKs/lighthouseRelease/libLighthouse.a` to your project on Xcode

* Click on your main project file (the one that represents the .xcodeproj) select Build Phases and drag the static library(libLighthouseSKDs.a) from the Products folder inside the Library you are importing to Link Binary With Libraries

* Add below code to your Info.plist file

```
	<key>UIBackgroundModes</key>
	<array>
		<string>location</string>
		<string>bluetooth-central</string>
	</array>
	<key>NSLocationWhenInUseUsageDescription</key>
	<string></string>
	<key>NSLocationAlwaysUsageDescription</key>
	<string></string>
```

* Add the following to your `Header Search Paths` and set the search to `recursive`
    `$(SRCROOT)/../node_modules/react-native-lighthouse`

* Add the following to your `Library Search Paths` for Debug and Release version
`$(SRCROOT)/../node_modules/react-native-lighthouse/ios/LighthouseSDKs/lighthouseDebug`     //Debug

`$(SRCROOT)/../node_modules/react-native-lighthouse/ios/LighthouseSDKs/lighthouseRelease`   //Release
