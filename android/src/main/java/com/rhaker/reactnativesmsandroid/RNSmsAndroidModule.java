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
import android.database.Cursor;

import java.util.Map;
import java.util.HashMap;
import java.lang.SecurityException;
import java.lang.String;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RNSmsAndroidModule extends ReactContextBaseJavaModule {

    private static final String TAG = RNSmsAndroidModule.class.getSimpleName();

    private ReactApplicationContext reactContext;

    // set the activity - pulled in from Main
    public RNSmsAndroidModule(ReactApplicationContext reactContext) {
      super(reactContext);
      this.reactContext = reactContext;
    }

    @Override
    public String getName() {
      return "SmsAndroid";
    }

    @ReactMethod
    public void sms(String phoneNumberString, String body, String sendType, Callback callback) {

        // send directly if user requests and android greater than 4.4
        if ((sendType.equals("sendDirect")) && (body != null) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumberString,null,body,null,null);
                callback.invoke(null,"success");
            }

            catch (Exception e) {
                callback.invoke(null,"error");
                e.printStackTrace();
            }

        } else {

            // launch default sms package, user hits send
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumberString.trim()));
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (body != null) {
                sendIntent.putExtra("sms_body", body);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getCurrentActivity());
                if (defaultSmsPackageName != null) {
                    sendIntent.setPackage(defaultSmsPackageName);
                }
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

    @ReactMethod
    public void list(String filter, final Callback errorCallback, final Callback successCallback) {
        try{
            JSONObject filterJ = new JSONObject(filter);
            String uri_filter = filterJ.has("box") ? filterJ.optString("box") : "inbox";
            int fread = filterJ.has("read") ? filterJ.optInt("read") : -1;
            int fid = filterJ.has("_id") ? filterJ.optInt("_id") : -1;
            String faddress = filterJ.optString("address");
            String fcontent = filterJ.optString("body");
            int indexFrom = filterJ.has("indexFrom") ? filterJ.optInt("indexFrom") : 0;
            int maxCount = filterJ.has("maxCount") ? filterJ.optInt("maxCount") : -1;
            Cursor cursor = getCurrentActivity().getContentResolver().query(Uri.parse("content://sms/"+uri_filter), null, "", null, null);
            int c = 0;
            JSONArray jsons = new JSONArray();
            while (cursor.moveToNext()) {
                boolean matchFilter = false;
                if (fid > -1)
                matchFilter = fid == cursor.getInt(cursor.getColumnIndex("_id"));
                else if (fread > -1)
                matchFilter = fread == cursor.getInt(cursor.getColumnIndex("read"));
                else if (faddress.length() > 0)
                matchFilter = faddress.equals(cursor.getString(cursor.getColumnIndex("address")).trim());
                else if (fcontent.length() > 0)
                matchFilter = fcontent.equals(cursor.getString(cursor.getColumnIndex("body")).trim());
                else {
                    matchFilter = true;
                }
                if (matchFilter)
                {
                    if (c >= indexFrom) {
                        if (maxCount>0 && c >= indexFrom + maxCount) break;
                        c++;
                        // Long dateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex("date")));
                        // String message = cursor.getString(cursor.getColumnIndex("body"));
                        JSONObject json;
                        json = getJsonFromCursor(cursor);
                        jsons.put(json);

                    }
                }

            }
            cursor.close();
            try {
                successCallback.invoke(c, jsons.toString());
            } catch (Exception e) {
                errorCallback.invoke(e.getMessage());
            }
        } catch (JSONException e)
        {
            errorCallback.invoke(e.getMessage());
            return;
        }
    }

    private JSONObject getJsonFromCursor(Cursor cur) {
        JSONObject json = new JSONObject();

        int nCol = cur.getColumnCount();
        String[] keys = cur.getColumnNames();
        try
        {
            for (int j = 0; j < nCol; j++)
            switch (cur.getType(j)) {
                case 0:
                json.put(keys[j], null);
                break;
                case 1:
                json.put(keys[j], cur.getLong(j));
                break;
                case 2:
                json.put(keys[j], cur.getFloat(j));
                break;
                case 3:
                json.put(keys[j], cur.getString(j));
                break;
                case 4:
                json.put(keys[j], cur.getBlob(j));
            }
        }
        catch (Exception e)
        {
            return null;
        }

        return json;
    }
}
