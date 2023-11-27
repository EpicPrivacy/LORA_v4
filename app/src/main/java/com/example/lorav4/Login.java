package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
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
            loginUserWithEmailAndPassword();
        }
    }
    private void clearInputFields() {
        reg_number.setText("");
        password2.setText("");
    }

    private void loginUserWithEmailAndPassword() {
        String email = reg_number.getText().toString().trim();
        String password = password2.getEditableText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            } else {
                                // Other authentication failures
                                Toast.makeText(Login.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            }

                            // Clear fields
                            clearInputFields();
                        }
                    }

                });

    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Navigate to the dashboard or perform other actions for successful login
            Intent intent = new Intent(Login.this, Dashboard.class);
            startActivity(intent);
            finish();
        } else {
            // Clear fields and show an error message
            clearInputFields();
            Toast.makeText(Login.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
        }
    }


    private void isUser() {
        String RegNumEnter = reg_number.getEditableText().toString().trim();
        String PasswordEnter = password2.getEditableText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LORA");
        Query CheckUser = reference.orderByChild("mobileNumber").equalTo(RegNumEnter);

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

                        finish();


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