package com.example.lorav4;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Shop_details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // This method is called when the activity is no longer in the foreground.
        // You might want to stop ongoing processes or resources here.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // This method is called when the activity is being destroyed.
        // Release resources, unregister listeners, or perform cleanup tasks here.
    }
}