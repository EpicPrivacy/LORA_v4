package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

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
    private void clearInputFields() {
        reg_number.setText("");
        password2.setText("");
    }

    private void loginUserWithEmailAndPassword() {
        String rawPhoneNumber = reg_number.getText().toString().trim();
        String password = password2.getText().toString().trim();

        // Ensure the phone number is in E.164 format
        String phoneNumber = formatPhoneNumber(rawPhoneNumber);

        // Use the FirebaseAuth instance to initiate the phone number authentication
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60, // Timeout duration
                TimeUnit.SECONDS,
                this, // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // This callback is triggered if the phone number can be instantly verified
                        // without needing to send or enter a verification code.
                        mAuth.signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Log.d("LoginActivity", "signInWithCredential:success, user: " + user.getPhoneNumber());
                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                                            Toast.makeText(Login.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();

                                            // Clear fields
                                            clearInputFields();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // This callback is invoked when an invalid request for verification is made,
                        // for instance, if the phone number format is not valid.
                        Log.w("LoginActivity", "onVerificationFailed", e);
                        Toast.makeText(Login.this, "Phone number verification failed. Please check your phone number.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // This callback is invoked when the verification code is successfully sent to the user's phone.
                        // Save the verification ID and token for later use
                        // You can use these values to manually verify the code if needed.
                        // For simplicity, we'll let Firebase handle the verification in onVerificationCompleted.
                    }
                });

        // Note: The verification process will continue in the callbacks, and you don't need to explicitly create PhoneAuthCredential here.
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Check if the phone number already starts with a plus sign
        if (!phoneNumber.startsWith("+")) {
            // Add the plus sign and any necessary country code
            // For example, assuming country code for the Philippines is +63
            phoneNumber = "+63" + phoneNumber;
        }
        return phoneNumber;
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
                            String m_numberDB = userSnapshot.child("m_number").getValue(String.class);
                            String delivery_addDB = userSnapshot.child("delivery_add").getValue(String.class);

                            Intent intent = new Intent(Login.this, Dashboard.class);

                            intent.putExtra("first_name", firstnameDB);
                            intent.putExtra("last_name", lastnameDB);
                            intent.putExtra("m_number", m_numberDB);
                            intent.putExtra("delivery_add", delivery_addDB);
                            intent.putExtra("password", PasswordFromDB);

                            startActivity(intent);
                            finish();
                            return; // Exit the method after successful login
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