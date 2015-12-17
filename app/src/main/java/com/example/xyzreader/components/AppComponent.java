package com.example.xyzreader.components;

import com.example.xyzreader.modules.AppModule;
import com.example.xyzreader.ui.BaseActivity;
import com.example.xyzreader.ui.BaseFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by ahmedrizwan on 13/12/2015.
 */
@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

}