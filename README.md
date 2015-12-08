## react-native-sms-android

This is a react native module that sends a sms message to a phone number. If the user has a default sms app, the message will open up directly. If there is no default, a sms chooser will be shown. This is for android only.

For ios, you can use the LinkingIOS component which is part of the core.

To get a contact's phone number, you can use my react-native-select-contact-android module.  

## Installation

```js
npm install react-native-sms-android --save
```

## Usage Example

```js
var SmsAndroid = require('react-native-sms-android');

SmsAndroid.sms(
  '123456789', // phone number to send sms to
  'This is the sms text', // sms body
  (err, message) => {
    if (err){
      console.log("error");
    } else {
      console.log(message); // callback message
    }
  }
);

```

## Getting Started - Android
* In `android/setting.gradle`
```gradle
...
include ':react-native-sms-android'
project(':react-native-sms-android').projectDir = new File(settingsDir, '../node_modules/react-native-sms-android/android')
```

* In `android/app/build.gradle`
```gradle
...
dependencies {
    ...
    compile project(':react-native-sms-android')
}
```

* register module (in android/app/src/main/java/[your-app-namespace]/MainActivity.java)
```java
import com.rhaker.reactnativesmsandroid.RNSmsAndroidPackage; // <------ add import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......

  private ReactRootView mReactRootView;
  private RNSmsAndroidPackage mRNSmsAndroidPackage;  // <------ add package
  ......

  mRNSmsAndroidPackage = new RNSmsAndroidPackage(this);  // <------ add package
  mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(mRNSmsAndroidPackage)                    // <------ add package
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();
  ......

      mReactInstanceManager.onResume(this);
      }
    }

}
```

* add Sms permission (in android/app/src/main/AndroidManifest.xml)
```xml
...
  <uses-permission android:name="android.permission.SEND_SMS" />
...
```
## Additional Notes

You should parse the phone number for digits only for best results. And the text message should be kept as short as possible to prevent truncation.

The module does not directly send the text message. It just pre-populates the message to the recipient. The user still needs to hit the send button for the sms to be sent.

## Acknowledgements and Special Notes

This module owes a special thanks to @lucasferreira for his react-native-send-intent module.  
