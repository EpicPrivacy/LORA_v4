package com.example.lorav4.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lorav4.Change_password;
import com.example.lorav4.Driver.Drivers_signup;
import com.example.lorav4.Login;
import com.example.lorav4.R;
import com.example.lorav4.Update_location;
import com.google.firebase.auth.FirebaseAuth;

public class Admin_dashboard extends AppCompatActivity {

    private Button btn_logout,btn_transaction,btn_shop_details,btn_update_loc,btn_changePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btn_logout = findViewById(R.id.Admin_btn_logout);
        btn_transaction = findViewById(R.id.Admin_btn_transaction);
        btn_shop_details = findViewById(R.id.Admin_btn_drivers_signup);
        btn_update_loc = findViewById(R.id.Admin_btn_update_loc);
        btn_changePass = findViewById(R.id.Admin_btn_changePass);


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog before logging out
                new AlertDialog.Builder(Admin_dashboard.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Sign out the user from Firebase Authentication
                                FirebaseAuth.getInstance().signOut();

                                // Redirect to the login or main activity
                                Intent intent = new Intent(Admin_dashboard.this, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // Finish the current activity to prevent going back with the back button
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Admin_dashboard.this, Admin_transaction.class);
                startActivity(intent);

            }
        });
        btn_shop_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Admin_dashboard.this, Drivers_signup.class);
                startActivity(intent);

            }
        });
        btn_update_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Admin_dashboard.this, Update_location.class);
                startActivity(intent);

            }
        });
        btn_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Admin_dashboard.this, Change_password.class);
                startActivity(intent);

            }
        });
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