package com.example.lorav4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lorav4.utils.AndroidUtil;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Verify_otp extends AppCompatActivity {

    String m_number;
    Long time_out = 60L;

    String VerificationCode;
    PhoneAuthProvider.ForceResendingToken ResendingToken;
    EditText otp_input;
    Button btn_next,btn_resend;
    ProgressBar progressBar;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        otp_input = findViewById(R.id.otp_input);
        btn_next = findViewById(R.id.btn_next);
        progressBar = findViewById(R.id.progressBar);
        btn_resend = findViewById(R.id.btn_resend);

        m_number = getIntent().getExtras().getString("m_number");

        sendOtp(m_number,false);
    }

    void sendOtp(String m_number,boolean isResend){
        setInProgress(true);

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(m_number)
                        .setTimeout(time_out, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signup(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showtoast(getApplicationContext(),"OTP Verification Failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                VerificationCode =s;
                                ResendingToken = forceResendingToken;
                                AndroidUtil.showtoast(getApplicationContext(),"OTP Sent Successfully");
                                setInProgress(false);
                            }
                        });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(ResendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
        }
    }

    void signup(PhoneAuthCredential phoneAuthCredential){

    }
}