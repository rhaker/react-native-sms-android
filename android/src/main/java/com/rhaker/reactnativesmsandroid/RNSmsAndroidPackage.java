package com.rhaker.reactnativesmsandroid;

import android.app.Activity;

import java.util.*;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

public class RNSmsAndroidPackage implements ReactPackage {
    private RNSmsAndroidModule mModuleInstance;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      mModuleInstance = new RNSmsAndroidModule(reactContext);
      return Arrays.<NativeModule>asList(mModuleInstance);
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

}
