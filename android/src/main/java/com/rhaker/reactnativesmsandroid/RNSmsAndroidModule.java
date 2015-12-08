package com.rhaker.reactnativesmsandroid;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Map;
import java.util.HashMap;
import java.lang.SecurityException;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class RNSmsAndroidModule extends ReactContextBaseJavaModule {

    private static final String TAG = RNSmsAndroidModule.class.getSimpleName();

    private Activity mActivity = null;

    private ReactApplicationContext reactContext;

    // set the activity - pulled in from Main
    public RNSmsAndroidModule(ReactApplicationContext reactContext, Activity activity) {
      super(reactContext);
      this.reactContext = reactContext;
      mActivity = activity;
    }

    @Override
    public String getName() {
      return "SmsAndroid";
    }

    @ReactMethod
    public void sms(String phoneNumberString, String body, Callback callback) {

      Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumberString.trim()));
      sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mActivity);

      if (body != null) {
         sendIntent.putExtra("sms_body", body);
      }

      if (defaultSmsPackageName != null) {
          sendIntent.setPackage(defaultSmsPackageName);
      }

      try {
         this.reactContext.startActivity(sendIntent);
         callback.invoke(null,"success");
      }

      catch (Exception e) {
         callback.invoke(null,"error");
         e.printStackTrace();
      }

    }

}
