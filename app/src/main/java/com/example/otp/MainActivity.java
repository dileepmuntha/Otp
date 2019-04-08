package com.example.otp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PhoneAuth";
    private EditText phoneText;
    private EditText codeText;
    private Button verifyButton;
    private Button sendButton;
    private FirebaseAuth mAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneText = (EditText) findViewById(R.id.phoneText);
        codeText = (EditText) findViewById(R.id.codeText);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        verifyButton.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
    }

    public void sendCode(View view) {

        String phoneNumber = phoneText.getText().toString();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificatonCallbacks() {


        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                  @Override
                    public void onVerificationCompleted(PhoneAuthCredential
                                                                credential) {
                      /* signoutButton.setEnabled(true);
                      statusText.setText("Signed In");
                      resendButton.setEnabled(false);*/
                      verifyButton.setEnabled(false);
                      codeText.setText("");
                      signInWithPhoneAuthCredential(credential);


                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        Toast.makeText(MainActivity.this, ""+verificationId.toString(), Toast.LENGTH_SHORT).show();
                        phoneVerificationId = verificationId;

                        resendToken = token;

                        verifyButton.setEnabled(true);
                        sendButton.setEnabled(false);
                       // resendButton.setEnabled(true);
                    }
                };


    }

  /*  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    }*/

    public void verifyCode(View view) {


        String code = codeText.getText().toString();


        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
        Toast.makeText(this, ""+phoneVerificationId.toString(), Toast.LENGTH_SHORT).show();
     //  Intent intent=new Intent(PhoneNumber.this,Village.class);
      //  startActivity(intent);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           // signoutButton.setEnabled(true);
                            codeText.setText("");
                           // statusText.setText("Signed In");
                           // resendButton.setEnabled(false);
                            verifyButton.setEnabled(true);
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(MainActivity.this, "valid", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                            startActivity(intent);

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(MainActivity.this, "verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


}
