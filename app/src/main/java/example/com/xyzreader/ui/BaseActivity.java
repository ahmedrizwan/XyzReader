package example.com.xyzreader.ui;

import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import example.com.xyzreader.XyzApp;
import retrofit.Retrofit;

/**
 * Created by ahmedrizwan on 13/12/2015.
 */
public class BaseActivity extends AppCompatActivity {
    @Inject
    Retrofit mRetrofit;

    void injectActivity(BaseActivity baseActivity){
        ((XyzApp) getApplication()).getAppComponent().inject(baseActivity);
    }
}
