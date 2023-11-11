package com.example.lorav4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sign_up extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText first_name,last_name,m_number,delivery_add,password;
    Button terms_condition,clear,submit;

    FirebaseDatabase DB;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        countryCodePicker = findViewById(R.id.login_countrycode);

        clear = findViewById(R.id.clear);
        submit = findViewById(R.id.submit);


        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        m_number = findViewById(R.id.m_number);
        delivery_add = findViewById(R.id.delivery_add);
        password = findViewById(R.id.password);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DB = FirebaseDatabase.getInstance();
                reference = DB.getReference().child("LORA");


                register();
            }
        });
    }
    private boolean validateFirstName(){
        String val = first_name.getEditableText().toString();

        if(val.isEmpty()){
            first_name.setError("Field cannot be empty");
            return false;
        }else {
            first_name.setError(null);

            return true;
        }
    }
    private boolean validateLastName(){
        String val = last_name.getEditableText().toString();

        if(val.isEmpty()){
            last_name.setError("Field cannot be empty");
            return false;
        }else {
            last_name.setError(null);
            return true;
        }
    }
    private boolean validateMNumber(){
        String val = m_number.getEditableText().toString();
        String NumberMatch = "^[+]?[0-9]{11}$";

        if(val.isEmpty()){
           m_number.setError("Field cannot be empty");
            return false;
        } else if (val.length()!=11) {
            m_number.setError("Mobile number not valid");
            return false;
        }else if (!val.matches(NumberMatch)) {
            m_number.setError("Philippine number only");
            return false;
        }
        else {
            m_number.setError(null);
            return true;
        }
    }
    private boolean validateDeliveryAdd(){
        String val = delivery_add.getEditableText().toString();


        if(val.isEmpty()){
            delivery_add.setError("Field cannot be empty");
            return false;
        }else {
            delivery_add.setError(null);
            return true;
        }


    }

    private boolean validatePassword(){
        String val = password.getEditableText().toString();
        String PasswordVal = "(?=\\S+$)"+".{6,}";

        if(val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }else if (!val.matches(PasswordVal)) {
            password.setError("Password is too weak");
            return false;
        }
        else {
            password.setError(null);
            return true;
        }
    }

    public void register() {

        if (!validateFirstName() | !validateLastName() | !validateMNumber() | !validateDeliveryAdd() | !validatePassword()) {
            return;
        }

        String fname = first_name.getEditableText().toString();
        String lname = last_name.getText().toString();
        String mnumber = m_number.getText().toString();
        String delivery = delivery_add.getText().toString();
        String pass = password.getText().toString();


        DatabaseReference userRef = DB.getReference().child("LORA").child(mnumber);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mobile number already registered
                    m_number.setError("Mobile number already registered");
                } else {
                    // Mobile number is unique, proceed with registration
                    Helper helper = new Helper(fname, lname, mnumber, delivery, pass);
                    reference.child(mnumber).setValue(helper);

                    // Assuming the next part of your code is for OTP verification
                    countryCodePicker.registerCarrierNumberEditText(m_number);
                    if (!countryCodePicker.isValidFullNumber()) {
                        m_number.setError("Phone number not valid");
                        return;
                    }

                    Intent intent = new Intent(Sign_up.this, Verify_otp.class);
                    intent.putExtra("m_number", countryCodePicker.getFullNumberWithPlus());
                    startActivity(intent);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}