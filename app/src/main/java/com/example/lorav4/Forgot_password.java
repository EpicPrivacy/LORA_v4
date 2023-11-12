package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lorav4.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Forgot_password extends AppCompatActivity {

    String m_number;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken  resendingToken;

    EditText mobile_otp;
    Button btn_send;
    ProgressBar progressBar2;
    TextView resend_txtview;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mobile_otp = findViewById(R.id.mobile_otp);
        btn_send = findViewById(R.id.btn_send);
        progressBar2 = findViewById(R.id.progressBar2);
        resend_txtview = findViewById(R.id.resend_txtview);

        // Check if intent has extras and m_number is not null
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("m_number")) {
            m_number = getIntent().getExtras().getString("m_number");
            if (m_number == null || m_number.isEmpty()) {
                // Handle the case where m_number is null or empty
                // You may want to show an error message and finish the activity
                finish();
                return;
            }
        } else {
            // Handle the case where intent extras or "m_number" key is missing
            finish();
            return;
        }

        sendOtp(m_number, false);

        runOnUiThread(() -> {
            btn_send.setOnClickListener(v -> {
                String enteredOtp = mobile_otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
            });
        });

        resend_txtview.setOnClickListener((v) -> {
            sendOtp(m_number, true);
        });

    }

    void sendOtp(String phoneNumber,boolean isResend){
        startResendTimer();
        setInProgress(true);

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showtoast(getApplicationContext(), "OTP verification failed");
                                setInProgress(false); // Move the setInProgress(false) here
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showtoast(getApplicationContext(), "OTP sent successfully");
                            }
                        });

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar2.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);
        }else{
            progressBar2.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false); // Move setInProgress(false) here
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Forgot_password.this, Verify_otp_forgot_password.class);
                    intent.putExtra("m_number", m_number);
                    startActivity(intent);
                } else {
                    AndroidUtil.showtoast(getApplicationContext(), "OTP verification failed");
                }
            }
        });


    }

    void startResendTimer(){
        resend_txtview.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resend_txtview.setText("Resend OTP in "+timeoutSeconds +" seconds");
                if(timeoutSeconds<=0){
                    timeoutSeconds =60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resend_txtview.setEnabled(true);
                    });
                }
            }
        },0,1000);
    }
}