package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class Forgot_password extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    private EditText mobileEditText;
    private Button sendOTPButton;
    private ProgressBar progressBar;
    private TextView resendTextView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mobileEditText = findViewById(R.id.mobile_otp);
        sendOTPButton = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progressBar2);
        resendTextView = findViewById(R.id.resend_btn);
        countryCodePicker = findViewById(R.id.login_countryCode);

        firebaseAuth = FirebaseAuth.getInstance();

        countryCodePicker.registerCarrierNumberEditText(mobileEditText);
        sendOTPButton.setOnClickListener((view -> {

                if (!countryCodePicker.isValidFullNumber()) {
                    mobileEditText.setError("Phone number no valid");
                    return;
                }
                runOnUiThread(() -> {
                    Intent intent = new Intent(Forgot_password.this, Forgot_verify.class);
                    intent.putExtra("m_number", countryCodePicker.getFullNumberWithPlus());
                    Log.d("Forgot_password", "Starting Verify_otp_forgot_password activity");
                    startActivity(intent);
                });


        }));
    }

    private boolean ValidateRegNumber(){
        String val = mobileEditText.getText().toString();
        String NumberMatch = "^[+]?[0-9]{10}$";

        if(val.isEmpty()){
            mobileEditText.setError("Field cannot be empty");
            return false;
        } else if (val.length()!=10) {
            mobileEditText.setError("Mobile number not valid");
            return false;
        }else if (!val.matches(NumberMatch)) {
            mobileEditText.setError("Philippine number only");
            return false;
        }
        else {
            mobileEditText.setError(null);
            return true;
        }
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
