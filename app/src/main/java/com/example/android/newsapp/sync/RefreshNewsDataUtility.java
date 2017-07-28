package com.example.android.newsapp.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Vahedi on 7/25/17.
 */

public class RefreshNewsDataUtility {
    //number of minutes I want my job wait before start
    private static final int REFRESH_DATA_INTERVAL_MINUTES = 1;
    //converted value of above parameter to seconds
    private static final int REFRESH_DATA_INTERVAL_SECONDS = (int)TimeUnit.MINUTES.toSeconds(REFRESH_DATA_INTERVAL_MINUTES);
    //number of seconds of leeway you wnt to give the execution wondow
    private static final int SYNC_FLEXTIME_SECONDS = REFRESH_DATA_INTERVAL_SECONDS;
    // this is a unique tag that is going to identify my job
    private static final String REFRESH_DATA_TAG = "refresh-data-tag";

    //this parameter is going to keep track of whether my job has started or not
    private static boolean sInitialized;

    //this is the method that actually going to start the job.
    // this is a synchronized method since we never want to execute this method more than once in a while
    synchronized public static void scheduleRefreshNewsData(@NonNull final Context context){

        //this line of code check if job has already been set up and if it has , it is going to return
        if (sInitialized) return;

        //otherwise it is going to create an object of GooglePlayDriver
        Driver driver = new GooglePlayDriver(context);

        //here we are going to create a firebaseJobDispatcher object according to driver
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //in this line of code we are going to create this job using all of it's constraints
        Job constraintRefreshNewsDataJob = dispatcher.newJobBuilder()
                .setService(AsyncTaskFirebaseJobService.class)
                .setTag(REFRESH_DATA_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REFRESH_DATA_INTERVAL_SECONDS,
                        REFRESH_DATA_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        //since we have the job we are going to schedule it
        dispatcher.schedule(constraintRefreshNewsDataJob);

        //this line of code shows that our job has been set up
        sInitialized = true;



    }

}
