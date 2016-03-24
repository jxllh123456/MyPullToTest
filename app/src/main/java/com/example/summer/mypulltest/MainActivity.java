package com.example.summer.mypulltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;

import view.PullToRefreshView;

public class MainActivity extends AppCompatActivity {


      ScrollView scrollView;
      PullToRefreshView pullToRefreshView;
      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            scrollView = (ScrollView)findViewById(R.id.scrollView);
            pullToRefreshView = (PullToRefreshView)findViewById(R.id.pull_to_refresh);
//            pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
//                  @Override
//                  public void onRefresh() {
//                        pullToRefreshView.postDelayed(new Runnable() {
//                              @Override
//                              public void run() {
//                                    pullToRefreshView.setRefreshing(false);
//                              }
//                        }, 1500);
//                  }
//            });
      }


}
