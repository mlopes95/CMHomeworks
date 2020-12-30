package com.example.simpleasynctask;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleAsyncTask {
    ExecutorService executors =  Executors.newFixedThreadPool(1);
    TextView mTextView;

    SimpleAsyncTask(TextView tv) {
        this.mTextView = tv;
    }

    public void doAsyncTask(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                int n = r.nextInt(11);

                int s = n * 200;
                try {
                    Thread.sleep(s);
                    updateUI("Awake at last after sleeping for " + s + " milliseconds!");
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        executors.submit(runnable);
    }

    private void updateUI(Object response){
        Handler uiThread = new Handler(Looper.getMainLooper());
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText((String) response);
            }
        });

    }

}
