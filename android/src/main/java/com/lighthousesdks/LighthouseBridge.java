package com.lighthousesdks;


import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.inlight.lighthousesdk.CampaignData;
import com.inlight.lighthousesdk.LighthouseConfig;
import com.inlight.lighthousesdk.LighthouseManager;
import com.inlight.lighthousesdk.LighthouseNotification;
import com.inlight.lighthousesdk.LighthouseNotifier;
import com.inlight.lighthousesdk.LighthouseSettings;
import com.inlight.lighthousesdk.ibeacon.IBeacon;
import com.inlight.lighthousesdk.ibeacon.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by paulbao on 1/12/2015.
 */
public class LighthouseBridge extends ReactContextBaseJavaModule {
    private LighthouseManager lighthouseManager;
    public LighthouseBridge(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "LighthouseBridge";
    }
    @ReactMethod
    public void configure(ReadableMap configuration) {
        if (lighthouseManager == null){
            lighthouseManager = LighthouseManager.getInstance(this.getReactApplicationContext());

            LighthouseConfig lighthouseConfig = new LighthouseConfig(
                    this.getReactApplicationContext(),
                    configuration.getString("appId"),
                    configuration.getString("appKey"),
                    configuration.getString("appToken"));
            lighthouseManager.setLighthouseConfig(lighthouseConfig);
            lighthouseManager.debug = BuildConfig.DEBUG;
        }

        lighthouseManager.setLightHouseNotifier(new LighthouseNotifier() {
            @Override
            public void LighthouseDidEnterBeacon(IBeacon iBeacon) {
                getReactApplicationContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("didEnterBeacon", convertBeacon(iBeacon));
            }

            @Override
            public void LighthouseDidExitBeacon(IBeacon iBeacon) {

                getReactApplicationContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("didExitBeacon", convertBeacon(iBeacon));
            }

            @Override
            public void LighthouseDidRangeBeacon(Collection<IBeacon> iBeacons, Region region) {
                for (IBeacon iBeacon : iBeacons) {
                    getReactApplicationContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("didRangeBeacon", convertBeacon(iBeacon));
                }
            }

            @Override
            public void LighthouseDidReceiveNotification(LighthouseNotification lighthouseNotification) {

            }

            @Override
            public void LighthouseDidReceiveCampaign(CampaignData campaignData) {

            }

            @Override
            public void LighthouseDidActionCampaign(LighthouseNotification lighthouseNotification) {

            }

            @Override
            public void LighthouseDidUpdateSettings(LighthouseSettings lighthouseSettings) {

            }
        });
    }

    @ReactMethod
    public void requestPushNotifications(String send_id) {
        lighthouseManager.requestPushNotifications(send_id);
    }
    @ReactMethod
    public void setProperties(ReadableMap properties) {
        if(lighthouseManager != null) {
            lighthouseManager.setProperties(reactToJSON(properties));
        }
    }
    @ReactMethod
    public void startMonitor() {
        lighthouseManager.launch();
    }


    @ReactMethod
    public void stopMonitor() {
        lighthouseManager.reset();
    }

    private JSONObject reactToJSON(ReadableMap readableMap) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        ReadableMapKeySetIterator  iterator = readableMap.keySetIterator();
        while(iterator.hasNextKey()){
            String key = iterator.nextKey();
            ReadableType valueType = readableMap.getType(key);
            switch (valueType){
                case Null:
                    jsonObject.put(key,JSONObject.NULL);
                    break;
                case Boolean:
                    jsonObject.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    jsonObject.put(key, readableMap.getInt(key));
                    break;
                case String:
                    jsonObject.put(key, readableMap.getString(key));
                    break;
                case Map:
                    jsonObject.put(key, reactToJSON(readableMap.getMap(key)));
                    break;
                case Array:
                    jsonObject.put(key, reactToJSON(readableMap.getArray(key)));
                    break;
            }
        }

        return jsonObject;
    }

    private JSONArray reactToJSON(ReadableArray readableArray) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i < readableArray.size(); i++) {
            ReadableType valueType = readableArray.getType(i);
            switch (valueType){
                case Null:
                    jsonArray.put(JSONObject.NULL);
                    break;
                case Boolean:
                    jsonArray.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    jsonArray.put(readableArray.getInt(i));
                    break;
                case String:
                    jsonArray.put(readableArray.getString(i));
                    break;
                case Map:
                    jsonArray.put(reactToJSON(readableArray.getMap(i)));
                    break;
                case Array:
                    jsonArray.put(reactToJSON(readableArray.getArray(i)));
                    break;
            }
        }
        return jsonArray;
    }

    private WritableMap convertBeacon(IBeacon iBeacon) {
        String key = String.format("%s-%s-%s", iBeacon.getProximityUuid(),
                iBeacon.getMajor(), iBeacon.getMinor());
        WritableMap params = Arguments.createMap();
        params.putString("key", key);
        params.putString("uuid", iBeacon.getProximityUuid().toUpperCase());
        params.putString("major", iBeacon.getMajor().toString());
        params.putString("minor", iBeacon.getMinor().toString());
        params.putDouble("accuracy", iBeacon.getAccuracy());
        params.putString("distance", formatDistance(iBeacon.getDistance()));

        return params;
    }

    private String formatDistance(Double distance){
        return String.format( "%.2f", distance );
    }
}
