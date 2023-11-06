package com.example.lorav4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

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

                startActivity(new Intent(Dashboard.this, MainActivity.class));

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
}