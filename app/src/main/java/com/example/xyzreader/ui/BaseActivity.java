package com.example.xyzreader.ui;

import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.components.AppComponent;
import com.example.xyzreader.components.DaggerAppComponent;
import com.example.xyzreader.modules.AppModule;

import javax.inject.Inject;

import io.realm.Realm;
import retrofit.Retrofit;

/**
 * Created by ahmedrizwan on 13/12/2015.
 */
public class BaseActivity extends AppCompatActivity {
    @Inject
    Retrofit mRetrofit;

    @Inject
    Realm mRealm;

    void injectActivity(BaseActivity baseActivity){
        final AppModule appModule = new AppModule(getApplication());
        AppComponent mAppComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .build();

        mAppComponent.inject(baseActivity);
    }
}
