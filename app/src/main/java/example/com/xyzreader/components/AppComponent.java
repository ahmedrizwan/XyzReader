package example.com.xyzreader.components;

import javax.inject.Singleton;

import dagger.Component;
import example.com.xyzreader.modules.AppModule;
import example.com.xyzreader.ui.BaseActivity;

/**
 * Created by ahmedrizwan on 13/12/2015.
 */
@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {

    void inject(BaseActivity activity);

}