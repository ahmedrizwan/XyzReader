package com.example.xyzreader.ui;

import android.support.v4.app.Fragment;

import com.example.xyzreader.components.AppComponent;
import com.example.xyzreader.components.DaggerAppComponent;
import com.example.xyzreader.modules.AppModule;

import javax.inject.Inject;

import io.realm.Realm;
import retrofit.Retrofit;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class BaseFragment extends Fragment{
    @Inject
    public Retrofit mRetrofit;

    @Inject
    public Realm mRealm;

    public void injectFragment(BaseFragment baseFragment){
        final AppModule appModule = new AppModule(getActivity().getApplication());
        AppComponent mAppComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .build();

        mAppComponent.inject(baseFragment);
    }
}
