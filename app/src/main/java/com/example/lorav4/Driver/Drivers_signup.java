package com.example.lorav4.Driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lorav4.R;
import com.example.lorav4.Verify_otp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.UUID;


public class Drivers_signup extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText first_name, last_name, m_number, password, confirm_password;
    private Button termsConditionButton, clear, submit;
    private CheckBox agreeCheckbox;
    private FirebaseDatabase DB;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_signup);

        countryCodePicker = findViewById(R.id.Drivers_countrycode);
        clear = findViewById(R.id.Drivers_clear);
        submit = findViewById(R.id.Drivers_submit);
        agreeCheckbox = findViewById(R.id.Drivers_agree_checkbox);
        termsConditionButton = findViewById(R.id.Drivers_tems_condition);

        first_name = findViewById(R.id.Drivers_first_name);
        last_name = findViewById(R.id.Drivers_last_name);
        m_number = findViewById(R.id.Drivers_m_number);
        password = findViewById(R.id.Drivers_password);
        confirm_password = findViewById(R.id.Drivers_confirm_password);


        termsConditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsAndConditionsDialog();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the text in all EditText fields
                first_name.setText("");
                last_name.setText("");
                m_number.setText("");
                password.setText("");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreeCheckbox.isChecked()) {
                    if (validateConfirmPassword()) {
                        DB = FirebaseDatabase.getInstance();
                        reference = DB.getReference().child("LORA");
                        register();
                    }
                } else {
                    Toast.makeText(Drivers_signup.this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showTermsAndConditionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage("\n" +
                "\n" +
                "Last Updated: [11/11/2023]\n" +
                "\n" +
                "Welcome to LORA!\n" +
                "\n" +
                "By accessing or using LORA, you agree to comply with and be bound by the following terms and conditions of use. If you do not agree to these terms, please do not use the app.\n" +
                "\n" +
                "1. **User Agreement:**\n" +
                "   - You must be at least 18 years old to use this app.\n" +
                "   - You are responsible for maintaining the confidentiality of your account and password.\n" +
                "\n" +
                "2. **Content:**\n" +
                "   - LORA reserves the right to modify, suspend, or discontinue any aspect of the app at any time.\n" +
                "   - Users are responsible for the content they post or share. Prohibited content includes but is not limited to offensive, abusive, or illegal materials.\n" +
                "\n" +
                "3. **Privacy:**\n" +
                "   - LORA respects your privacy. Please review our Privacy Policy for details on how we collect, use, and disclose information.\n" +
                "\n" +
                "4. **Intellectual Property:**\n" +
                "   - All content and materials in this app, including but not limited to text, graphics, logos, and images, are the property of LORA and are protected by intellectual property laws.\n" +
                "\n" +
                "5. **User Conduct:**\n" +
                "   - Users agree not to engage in any conduct that may harm the app or interfere with other users.\n" +
                "\n" +
                "6. **Termination:**\n" +
                "   - LORA reserves the right to terminate or suspend your account at any time without notice for any reason.\n" +
                "\n" +
                "7. **Disclaimer:**\n" +
                "   - The app is provided \"as is\" without any warranties.\n" +
                "   - LORA is not responsible for any damages resulting from the use of the app.\n" +
                "\n" +
                "8. **Governing Law:**\n" +
                "   - These terms are governed by and construed in accordance with the laws of Philippines.\n" +
                "\n" +
                "9. **Contact:**\n" +
                "   - For questions or concerns regarding these terms, please contact 09564001376.\n" +
                "\n" +
                "By using LORA, you acknowledge that you have read and understood these terms and conditions and agree to be bound by them.\n" +
                "\n" +
                "Logistics Routing Solution\n" +
                "56JG+C3 Los Baños, Laguna\n" +
                "09564001376"
        );

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

        if(val.matches("m_number")){

        }
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

    private boolean validatePassword() {
        String val = password.getEditableText().toString();
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        if (val.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else if (!val.matches(passwordPattern)) {
            password.setError("Password must contain at least 8 characters with one uppercase letter, one lowercase letter, one digit, and one special character");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String passwordVal = password.getEditableText().toString();
        String confirmPasswordVal = confirm_password.getEditableText().toString();

        if (!confirmPasswordVal.equals(passwordVal)) {
            confirm_password.setError("Passwords do not match");
            return false;
        } else {
            confirm_password.setError(null);
            return true;
        }
    }


    public void register() {
        if (!validateFirstName() || !validateLastName() || !validateMNumber() ||
                !validatePassword() || !validateConfirmPassword()) {
            return;
        }


        String fname = first_name.getEditableText().toString();
        String lname = last_name.getText().toString();
        String mnumber = m_number.getText().toString();
        String pass = password.getText().toString();


        DatabaseReference userRef = DB.getReference().child("LORA").child(mnumber);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mobile number already registered
                    m_number.setError("Mobile number already registered");
                } else {

                    String userType = "Driver";
                    // Generate a user ID (you can use a UUID or any other method)
                    String userId = UUID.randomUUID().toString();


                    // Mobile number is unique, proceed with registration
                    Drivers_helper helper = new Drivers_helper(userId, fname, lname, mnumber, pass, userType);
                    reference.child(mnumber).setValue(helper)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Registration", "Registration successful");
                                        // Registration successful, proceed with OTP verification
                                        countryCodePicker.registerCarrierNumberEditText(m_number);
                                        if (!countryCodePicker.isValidFullNumber()) {
                                            m_number.setError("Phone number not valid");
                                            return;
                                        }

                                        // Start OTP verification
                                        Intent intent = new Intent(Drivers_signup.this, Verify_otp.class);
                                        intent.putExtra("m_number", countryCodePicker.getFullNumberWithPlus());
                                        intent.putExtra("user_id", userId); // Pass user ID to the next activity
                                        intent.putExtra("identity", userType);
                                        startActivity(intent);
                                    } else {
                                        Log.e("Registration", "Registration failed", task.getException());
                                        // Registration failed
                                        // Handle the error, if needed
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // This method is called when the activity is being destroyed.
        // Release resources, unregister listeners, or perform cleanup tasks here.
    }
}