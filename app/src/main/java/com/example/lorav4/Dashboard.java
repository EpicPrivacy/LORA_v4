package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {

    Button btn_logout,btn_transaction,btn_shop_details,btn_update_loc,btn_changePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btn_logout = findViewById(R.id.btn_logout);
        btn_transaction = findViewById(R.id.btn_transaction);
        btn_shop_details = findViewById(R.id.btn_shop_details);
        btn_update_loc = findViewById(R.id.btn_update_loc);
        btn_changePass = findViewById(R.id.btn_changePass);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase Authentication
                FirebaseAuth.getInstance().signOut();

                // Redirect to the login or main activity
                startActivity(new Intent(Dashboard.this, MainActivity.class));
                finish(); // Finish the current activity to prevent going back with the back button
            }
        });
        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Dashboard.this, Transactions.class));

            }
        });
        btn_shop_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Dashboard.this, Shop_details.class));

            }
        });
        btn_update_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Dashboard.this, Update_location.class));

            }
        });
        btn_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Dashboard.this, Change_password.class));

            }
        });
    }
    @Override
    public void onBackPressed() {
        // Do nothing (disable the back button)
        super.onBackPressed();
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