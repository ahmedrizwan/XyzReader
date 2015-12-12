package example.com.xyzreader;

import android.app.Application;

import example.com.xyzreader.components.AppComponent;
import example.com.xyzreader.components.DaggerAppComponent;
import example.com.xyzreader.modules.AppModule;

/**
 * Created by ahmedrizwan on 13/12/2015.
 */
public class XyzApp extends Application {

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        final AppModule appModule = new AppModule(this);
        mAppComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .build();
    }


}
