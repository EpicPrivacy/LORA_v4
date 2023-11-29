package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;
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

public class Login_verify extends AppCompatActivity {

    String m_number;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    EditText otp_input;
    Button btn_next;
    ProgressBar progressBar;
    TextView btn_resend;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify);

        otp_input = findViewById(R.id.login_otp_input);
        btn_next = findViewById(R.id.login_btn_next);
        progressBar = findViewById(R.id.login_progressBar);
        btn_resend = findViewById(R.id.login_btn_resend);

        m_number = getIntent().getStringExtra("m_number");
        if (m_number == null) {
            Log.e("VerifyOtpActivity", "m_number is null");
            finish();
            return;
        }

        sendOtp(m_number, null, false);

        btn_next.setOnClickListener(v -> {
            ValidateRegNumber();
            String enteredOtp = otp_input.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
        });

        btn_resend.setOnClickListener((v) -> {
            sendOtp(m_number, null, true);
        });
    }

    private void ValidateRegNumber() {
        String val = otp_input.getText().toString();
        if (val.isEmpty()) {
            otp_input.setError("Field cannot be empty");
        } else {
            otp_input.setError(null);
        }
    }

    void sendOtp(String phoneNumber, String verificationId, boolean isResend) {
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
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showtoast(getApplicationContext(), "OTP sent successfully");
                                setInProgress(false); // Add this line
                            }
                        });

        if (isResend) {
            builder.setForceResendingToken(resendingToken);
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        Log.d("Login_verify", "PhoneAuthCredential: " + phoneAuthCredential.toString());

        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Login_verify.this, Dashboard.class);
                    intent.putExtra("m_number", m_number);
                    startActivity(intent);
                    finish();
                } else {
                    AndroidUtil.showtoast(getApplicationContext(), "OTP verification failed");
                }
            }
        });
    }

    void startResendTimer() {
        btn_resend.setEnabled(false);
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;

                runOnUiThread(() -> {
                    btn_resend.setText("Resend OTP in " + timeoutSeconds + " seconds");
                });

                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();

                    runOnUiThread(() -> {
                        btn_resend.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
