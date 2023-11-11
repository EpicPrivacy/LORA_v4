package com.example.lorav4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText reg_number,password2;
    Button btn_login2,btn_forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login2 = findViewById(R.id.btn_login2);
        btn_forgot = findViewById(R.id.btn_forgot);

        reg_number = findViewById(R.id.reg_number);
        password2 = findViewById(R.id.password2);


        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Login.this, Forgot_password.class));

            }
        });

        btn_login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginUser();

            }
        });

    }

    private boolean ValidateRegNumber(){
        String val = reg_number.getEditableText().toString();
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
        String PasswordVal = "(?=\\S+$)"+".{6,}";

        if(val.isEmpty()){
            password2.setError("Field cannot be empty");
            return false;
        }else if (!val.matches(PasswordVal)) {
            password2.setError("Password is too weak");
            return false;
        }
        else {
            password2.setError(null);
            return true;
        }
    }


    public void LoginUser(){

        if (!ValidateRegNumber() | !ValidatePassword()){

    }else {
            isUser();
        }
}



    private void isUser() {
        String RegNumEnter = reg_number.getEditableText().toString().trim();
        String PasswordEnter = password2.getEditableText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LORA");
        Query CheckUser = reference.orderByChild("m_number").equalTo(RegNumEnter);

        CheckUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    reg_number.setError(null);
                    String PasswordFromDB = snapshot.child(RegNumEnter).child("password").getValue(String.class);


                    if(PasswordFromDB.equals(PasswordEnter)){
                        reg_number.setError(null);

                        String firstnameDB = snapshot.child(RegNumEnter).child("first_name").getValue(String.class);
                        String lastnameDB = snapshot.child(RegNumEnter).child("last_name").getValue(String.class);
                        String m_numberDB = snapshot.child(RegNumEnter).child("m_number").getValue(String.class);
                        String delivery_addDB = snapshot.child(RegNumEnter).child("delivery_add").getValue(String.class);


                        Intent intent = new Intent(Login.this,Dashboard.class);

                        intent.putExtra("first_name",firstnameDB);
                        intent.putExtra("last_name",lastnameDB);
                        intent.putExtra("m_number",m_numberDB);
                        intent.putExtra("delivery_add",delivery_addDB);
                        intent.putExtra("password",PasswordFromDB);

                        startActivity(intent);




                    }else {
                        password2.setError("Invalid Credentials");
                        password2.requestFocus();
                    }
                }
                else {
                    reg_number.setError("User does not Exist");
                    reg_number.requestFocus();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    }