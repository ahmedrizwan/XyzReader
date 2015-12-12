package com.example.xyzreader.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import example.com.xyzreader.R;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Application context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Context providesAppContext() {
        return mContext;
    }

    @Provides
    @Singleton
    public Realm providesRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(final Context context) {
        return context.getSharedPreferences(context.getApplicationInfo().name,Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    public Gson providesGsonForRealm() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass()
                                .equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Context context, Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(context.getString(R.string.base_url))
                .build();
        return retrofit;
    }
}