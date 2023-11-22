package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Forgot_password_change extends AppCompatActivity {

    EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    Button changePasswordButton;

    FirebaseDatabase DB;
    DatabaseReference userRef;
    String m_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_change);

        Intent intent = getIntent();
        m_number = intent.getStringExtra("m_number"); // Use the correct key

        oldPasswordEditText = findViewById(R.id.old_password);
        newPasswordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        changePasswordButton = findViewById(R.id.change_password_button);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String password = oldPasswordEditText.getEditableText().toString().trim();
        String newPassword = newPasswordEditText.getEditableText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getEditableText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LORA");
        Query checkUser = reference.orderByChild("m_number").equalTo(m_number);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child(m_number).child("password").getValue(String.class);

                    if (passwordFromDB.equals(password)) {
                        // Old password is correct
                        Toast.makeText(Forgot_password_change.this, "Old password is correct", Toast.LENGTH_SHORT).show();

                        if (newPassword.equals(confirmPassword)) {
                            // Password change successful
                            Toast.makeText(Forgot_password_change.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                            // Update the password in the database
                            DatabaseReference userRef = reference.child(m_number).child("password");
                            userRef.setValue(newPassword);

                            startActivity(new Intent(Forgot_password_change.this, Login.class));
                        } else {
                            // New passwords do not match
                            Toast.makeText(Forgot_password_change.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Old password is incorrect
                        Toast.makeText(Forgot_password_change.this, "Incorrect old password", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Toast.makeText(Forgot_password_change.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
