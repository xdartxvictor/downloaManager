package com.downloadmanager;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by victoraliaga on 7/12/17.
 */

public class CustomReactPackage implements ReactPackage {

    private CustomDownloadManager CustomDownloadManagerModule = null;
    private static CustomReactPackage instance = null;

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
//        List<NativeModule> modules = new ArrayList<>();
//
//        modules.add(new TwitterSignin(reactContext));
//
//        return modules;
        CustomDownloadManagerModule = new CustomDownloadManager(reactContext);
        if (instance == null) {
            instance = this;
        }

        return Arrays.<NativeModule>asList(CustomDownloadManagerModule);
    }

    public static CustomReactPackage getInstance() {
        if (instance == null) {
            instance = new CustomReactPackage();
        }

        return instance;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        CustomDownloadManagerModule.onActivityResult(activity, requestCode, resultCode, data);
    }
}
