package com.thfund.client.router.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thfund.client.router.annotation.ActivityRoute;

@ActivityRoute(routeKey = "activity_main", bundleID = "1002",
        formerBundleID = "1001",
        formerClassName = "com.thfund.client.router.demo.FormerActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
