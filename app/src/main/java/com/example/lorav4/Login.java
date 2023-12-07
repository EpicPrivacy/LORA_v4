package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lorav4.Admin.Admin_transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText reg_number,password2;
    Button btn_login2,btn_forgot,btn_newAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_newAccount = findViewById(R.id.btn_newAccount);
        btn_login2 = findViewById(R.id.btn_login2);
        btn_forgot = findViewById(R.id.btn_forgot);


        reg_number = findViewById(R.id.reg_number);
        password2 = findViewById(R.id.password2);

        mAuth = FirebaseAuth.getInstance();

        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                forgot();

            }
        });

        btn_login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginUser();

            }
        });

        btn_newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Login.this, Sign_up.class));
            }
        });

    }

    private boolean ValidateRegNumber(){
        String val = reg_number.getText().toString();
        String NumberMatch = "^[+]?[0-9]{11}$";

        if(val.isEmpty()){
            reg_number.setError("Field cannot be empty");
            return false;
        } else if (val.length()!=11) {
            reg_number.setError("Mobile number not valid");
            return false;
        }else if (!val.matches(NumberMatch)) {
            reg_number.setError("Philippine number only");
            return false;
        }
        else {
            reg_number.setError(null);
            return true;
        }
    }
    private boolean ValidatePassword(){
        String val = password2.getEditableText().toString();
        String PasswordVal = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        if(val.isEmpty()){
            password2.setError("Field cannot be empty");
            return false;
        }else if (!val.matches(PasswordVal)) {
            password2.setError("Password require at least 8 characters with at least one uppercase letter, one lowercase letter, one digit, and one special character");
            return false;
        }
        else {
            password2.setError(null);
            return true;
        }
    }

    public void forgot(){

        Intent intent = new Intent(Login.this, Forgot_password.class);
        startActivity(intent);
    }

    public void LoginUser(){

        if (!ValidateRegNumber() | !ValidatePassword()){

        }else {
            isUser();

        }
    }
    private void isUser() {
        String RegNumEnter = reg_number.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LORA");
        Query CheckUser = reference.orderByChild("mobileNumber").equalTo(RegNumEnter);

        CheckUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String PasswordFromDB = userSnapshot.child("password").getValue(String.class);

                        // Check if the entered password matches the password from the database
                        if (PasswordFromDB.equals(password2.getText().toString().trim())) {
                            reg_number.setError(null);

                            String firstnameDB = userSnapshot.child("first_name").getValue(String.class);
                            String lastnameDB = userSnapshot.child("last_name").getValue(String.class);
                            //String m_numberDB = userSnapshot.child("m_number").getValue(String.class);
                            String delivery_addDB = userSnapshot.child("delivery_add").getValue(String.class);

                            if (RegNumEnter.equals("09000000000") && PasswordFromDB.equals("@dminPassw0rd")) {
                                Intent intent = new Intent(Login.this, Admin_transaction.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(Login.this, Login_verify.class);

                                String formatNum = reg_number.getText().toString().substring(1).trim();
                                String countryCode = "+63" + formatNum;

                                intent.putExtra("m_number", countryCode);

                                startActivity(intent);
                            }

                            finish();
                            return;
                        } else {
                            password2.setError("Invalid Credentials");
                            password2.requestFocus();
                            return; // Exit the method if the passwords don't match
                        }
                    }
                } else {
                    reg_number.setError("User does not Exist");
                    reg_number.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
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
    @Override
    protected void onStop() {
        super.onStop();
        // Remove Firebase listeners here
    }

}