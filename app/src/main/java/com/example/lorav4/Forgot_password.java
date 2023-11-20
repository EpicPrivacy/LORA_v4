package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Timer;
import java.util.TimerTask;

public class Forgot_password extends AppCompatActivity {

    EditText mobile_otp, resend_txtview;
    CountryCodePicker login_countryCode;
    Long timeoutSeconds = 60L;

    ProgressBar progressBar2;

    Button btn_send;

    FirebaseDatabase DB;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mobile_otp = findViewById(R.id.mobile_otp);
        login_countryCode = findViewById(R.id.login_countryCode);

        DB = FirebaseDatabase.getInstance();
        reference = DB.getReference().child("LORA");


        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Verify_otp_forgot_password();
            }
        });
    }

    private boolean validateMNumber() {
        String val = mobile_otp.getEditableText().toString().trim();
        String NumberMatch = "^[+]?[0-9]{10}$";

        if (val.isEmpty()) {
            mobile_otp.setError("Field cannot be empty");
            return false;
        } else if (val.length() != 10) {
            mobile_otp.setError("Mobile number not valid");
            return false;
        } else if (!val.matches(NumberMatch)) {
            mobile_otp.setError("Philippine number only");
            return false;
        } else {
            mobile_otp.setError(null);
            return true;
        }
    }

    public void register() {
        startResendTimer();
        setInProgress(true);

        if (!validateMNumber()) {
            return;
        }

        String mnumber = mobile_otp.getText().toString();

        DatabaseReference userRef = reference.child(mnumber);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Mobile number already registered
                    mobile_otp.setError("Mobile number not registered");
                } else {
                    // Mobile number is unique, proceed with registration
                    Helper helper = new Helper(mnumber);
                    reference.child(mnumber).setValue(helper)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        login_countryCode.registerCarrierNumberEditText(mobile_otp);
                                        if (!login_countryCode.isValidFullNumber()) {
                                            mobile_otp.setError("Phone number not valid");
                                            return;
                                        }

                                        // Start OTP verification
                                        Intent intent = new Intent(Forgot_password.this, Verify_otp_forgot_password.class);
                                        intent.putExtra("m_number", login_countryCode.getFullNumberWithPlus());
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

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar2.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);
        } else {
            progressBar2.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
        }
    }

    void startResendTimer() {
        resend_txtview.setEnabled(false);
        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resend_txtview.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    }
                });

                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            resend_txtview.setEnabled(true);
                        }
                    });
                }
            }
        }, 0, 1000);
    }
}
