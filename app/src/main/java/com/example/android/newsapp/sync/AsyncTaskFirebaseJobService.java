package com.example.android.newsapp.sync;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Vahedi on 7/25/17.
 */

public class AsyncTaskFirebaseJobService extends JobService {
    //Since this job will run on main thread and can cause probplem for main thread, we should create a background thread to run the job on that thread
    private AsyncTask mBackgroundThread;

    @Override
    public boolean onStartJob(final JobParameters job) {

        //Instantiate the AsyncTask object for background thread
        mBackgroundThread = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                //Log.d("------------------->","Job task is executing");
                //Here we are going to run the task in background and pass it the context which is AsyncTaskFirebaseJobService's context
                // and also the appropriate action which is FETCH_DATA action
                AsynchTasks.executeTask( AsyncTaskFirebaseJobService.this, AsynchTasks.ACTION_FETCH_DATA);
                return null;
            }

            //The best place to inform system that the job has finished is OnPostExecute method of AsyncTask object
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                //Job service needs to tell the system when it's actually done
                //we return false here because at this point we know that the job has been successful
                //and we dont need reschedule
                jobFinished( job , false);



            }
        };

        //Here we need to run the background task that we have created above
        mBackgroundThread.execute();

        //Here we return true since our task is running on another thread right now
        return true;
    }

    //onStopJob get called when the requirements of the job are no longer met
    @Override
    public boolean onStopJob(JobParameters job) {
        //Since the task requirements are no longer met we are going to cancel the job
        if (mBackgroundThread!=null) mBackgroundThread.cancel(true);
        //we return true here to say as soon as the requirements remet the should be retried
        return true;
    }
}
