package com.example.minyoung.finding_dog;

import android.app.Application;

import com.kakao.auth.KakaoSDK;

/**
 * Created by minyoung on 2018-07-20.
 */

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    public static GlobalApplication getGlobalApplicationContext() {

        if (instance == null) {

            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());

    }

    @Override

    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
}
