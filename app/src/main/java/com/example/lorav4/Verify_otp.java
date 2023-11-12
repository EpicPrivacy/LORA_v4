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

public class Verify_otp extends AppCompatActivity {

    String m_number;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken  resendingToken;

    EditText otp_input;
    Button btn_next;
    ProgressBar progressBar;
    TextView btn_resend;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        otp_input = findViewById(R.id.otp_input);
        btn_next = findViewById(R.id.btn_next);
        progressBar = findViewById(R.id.progressBar);
        btn_resend = findViewById(R.id.btn_resend);

        m_number = getIntent().getStringExtra("m_number");
        if (m_number == null) {
            // Handle the case where m_number is null
            Log.e("VerifyOtpActivity", "m_number is null");
            finish();  // Finish the activity or take appropriate action
            return;
        }

        sendOtp(m_number,false);

        btn_next.setOnClickListener(v -> {
            String enteredOtp = otp_input.getText().toString();

            // Check if verificationCode is not null or empty before proceeding
            if (verificationCode != null && !verificationCode.isEmpty()) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
            } else {
                // Handle the case where verificationCode is null or empty
                AndroidUtil.showtoast(getApplicationContext(), "Verification code is not available");
            }
        });

        btn_resend.setOnClickListener((v)->{
            sendOtp(m_number,true);
        });

    }

    void sendOtp(String phoneNumber, boolean isResend) {
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
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showtoast(getApplicationContext(), "OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showtoast(getApplicationContext(), "OTP sent successfully");
                                setInProgress(false);

                                Log.d("VerifyOtpActivity", "Entered OTP: " + otp_input.getText().toString());
                                Log.d("VerifyOtpActivity", "Verification Code: " + verificationCode);
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
            progressBar.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
        Log.d("VerifyOtpActivity", "Attempting to sign in...");

        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(Verify_otp.this,MainActivity.class);
                    intent.putExtra("m_number",m_number);
                    startActivity(intent);
                    finish();
                }else{
                    AndroidUtil.showtoast(getApplicationContext(),"OTP verification failed");
                }
            }
        });


    }

    void startResendTimer() {
        btn_resend.setEnabled(false);
        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_resend.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    }
                });

                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            btn_resend.setEnabled(true);
                        }
                    });
                }
            }
        }, 0, 1000);
    }

}