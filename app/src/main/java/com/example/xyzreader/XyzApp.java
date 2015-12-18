package com.example.xyzreader;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.View;

import com.example.xyzreader.components.AppComponent;
import com.example.xyzreader.components.DaggerAppComponent;
import com.example.xyzreader.modules.AppModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

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
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        Timber.plant(new Timber.DebugTree());
        final AppModule appModule = new AppModule(this);
        mAppComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launchFragmentWithSharedElements(final boolean isTwoPane,
                                                        final Fragment fromFragment,
                                                        final Fragment toFragment,
                                                        final int container,
                                                        final View... views) {
        if (isVersionLollipopAndAbove()) {
            FragmentTransaction fragmentTransaction = fromFragment.getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            if (!isTwoPane) {
                final TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new ChangeImageTransform());
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.addTransition(new ChangeTransform());
                transitionSet.setDuration(500);
                fromFragment.setSharedElementReturnTransition(transitionSet);
                fromFragment.setSharedElementEnterTransition(transitionSet);
                toFragment.setSharedElementEnterTransition(transitionSet);
                toFragment.setSharedElementReturnTransition(transitionSet);
                for (View view : views) {
                    fragmentTransaction.addSharedElement(view, view.getTransitionName());
                }

                fragmentTransaction
                        .replace(container, toFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                fragmentTransaction
                        .replace(container, toFragment)
                        .commit();
            }

        } else {
            if (isTwoPane)
                fromFragment.getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(container, toFragment)
                        .commit();
            else
                launchFragment(((AppCompatActivity) fromFragment.getActivity()), container, toFragment);
        }

    }

    /***
     * Helper method to launch Fragments
     *
     * @param activity    Parent Activity
     * @param containerId The id of the container
     * @param fragment    The fragment to load
     */
    public static void launchFragment(final AppCompatActivity activity, int containerId, final Fragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static boolean isVersionLollipopAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
