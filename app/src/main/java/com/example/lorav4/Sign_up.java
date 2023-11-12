package com.example.lorav4;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class Sign_up extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText first_name,last_name,m_number,delivery_add,password;
    Button termsConditionButton,clear,submit;

    CheckBox agreeCheckbox;

    FirebaseDatabase DB;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        countryCodePicker = findViewById(R.id.login_countrycode);

        clear = findViewById(R.id.clear);
        submit = findViewById(R.id.submit);

        agreeCheckbox = findViewById(R.id.agree_checkbox);
        termsConditionButton = findViewById(R.id.tems_condition);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        m_number = findViewById(R.id.m_number);
        delivery_add = findViewById(R.id.delivery_add);
        password = findViewById(R.id.password);

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
                delivery_add.setText("");
                password.setText("");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (agreeCheckbox.isChecked()) {
                    DB = FirebaseDatabase.getInstance();
                    reference = DB.getReference().child("LORA");
                    register();
                } else {
                    Toast.makeText(Sign_up.this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
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
        if (!validateFirstName() || !validateLastName() || !validateMNumber() || !validateDeliveryAdd() || !validatePassword()) {
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
                                        Intent intent = new Intent(Sign_up.this, Verify_otp.class);
                                        intent.putExtra("m_number", countryCodePicker.getFullNumberWithPlus());
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
}