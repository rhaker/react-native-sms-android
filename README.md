## react-native-sms-android

This is a react native module that sends a sms message to a phone number. There are two options for sending a sms: 1) directly inside the app or 2) outside the app by launching the default sms application. This is for android only.

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
  'sendDirect', // sendDirect or sendIndirect
  (err, message) => {
    if (err){
      console.log("error");
    } else {
      console.log(message); // callback message
    }
  }
);

/* List SMS messages matching the filter */
var filter = {
    box: 'inbox', // 'inbox' (default), 'sent', 'draft', 'outbox', 'failed', 'queued', and '' for all
    // the next 4 filters should NOT be used together, they are OR-ed so pick one
    read: 0, // 0 for unread SMS, 1 for SMS already read
    _id: 1234, // specify the msg id
    address: '+97433------', // sender's phone number
    body: 'Hello', // content to match
    // the next 2 filters can be used for pagination
    indexFrom: 0, // start from index 0
    maxCount: 10, // count of SMS to return each time
};

SmsAndroid.list(JSON.stringify(filter), (fail) => {
        console.log("OH Snap: " + fail)
    },
    (count, smsList) => {
        console.log('Count: ', count);
        console.log('List: ', smsList);
        var arr = JSON.parse(smsList);
        for (var i = 0; i < arr.length; i++) {
            var obj = arr[i];
            console.log("Index: " + i);
            console.log("-->" + obj.date);
            console.log("-->" + obj.body);
        }
    });

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

* If using RN < 0.29, register module (in android/app/src/main/java/[your-app-namespace]/MainActivity.java)
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

If using RN 0.29+, register module (in android/app/src/main/java/[your-app-namespace]/MainApplication.java)
``` java
// ...
import com.rhaker.reactnativesmsandroid.RNSmsAndroidPackage; // <--

public class MainApplication extends Application implements ReactApplication {

    private final ReactNativeHost reactNativeHost = new ReactNativeHost(this) {

            // ...

            @Override
            protected List<ReactPackage> getPackages() {
                return Arrays.<ReactPackage>asList(
                    new RNSmsAndroidPackage(),  // <--
                    // ...
                );
            }
        };
    }

    // ...
}
```

* add Sms permission if you're sending SMS using the ```sendDirect``` method (in android/app/src/main/AndroidManifest.xml)
```xml
...
  <uses-permission android:name="android.permission.SEND_SMS" />
  <uses-permission android:name="android.permission.READ_SMS" />
...
```
## Additional Notes

You should parse the phone number for digits only for best results. And the text message should be kept as short as possible to prevent truncation.

The sendDirect option sends the text message silently from within your app. This requires android version 4.4 or higher. You must also specify a body message. If you provide invalid data, the module will default to the sendIndirect method.

The sendIndirect option launches either the default sms application or the chooser. From here, the data is pre-populated in the sms message to the recipient. The user still needs to hit the send button for the sms to be sent. This occurs outside of your app.

## Acknowledgements and Special Notes

This module owes a special thanks to @lucasferreira for his react-native-send-intent module.  
This module was updated for RN v0.29+ by @sharafat
This module was updated to include a list feature by @DarrylID
