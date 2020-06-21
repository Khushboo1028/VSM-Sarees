package com.replon.vsm.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.vsm.MainActivity;
import com.replon.vsm.R;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    RelativeLayout relativeLayout;
    ImageView back;
    EditText et_phone,et_otp;
    TextView not_registered;
    Button btn_otp;
    ProgressBar progressBar;
    String phone;
    Button login_button;
    TextView resend_otp;

    //firebase
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private boolean mVerificationInProgress = false,verificationSuccessful;
    PhoneAuthCredential credential_global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), LoginActivity.this);

        setContentView(R.layout.activity_login);

        init();



        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.lightBlack));
        }

        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_otp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_otp.setBackgroundResource(R.drawable.edittext_borders_light);
                return false;
            }
        });

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_phone.getText().toString().length()!=10){
                    showMessage("Error!","Please enter a 10 digit phone number.");
                }else if(et_phone.getText().toString().charAt(0) != '9' &&
                        et_phone.getText().toString().charAt(0) != '8'  &&
                        et_phone.getText().toString().charAt(0) != '7'  &&
                        et_phone.getText().toString().charAt(0) != '6'){
                    showMessage("Error","Please enter a valid phone number");
                }else {

                    showMessage("Send OTP to +91" + et_phone.getText().toString(), "VSM will send you and OTP to verfiy your mobile number. You will recieve and SMS for verification and standard rates may apply.");
                    phone=et_phone.getText().toString().trim();
                    btn_otp.setVisibility(View.GONE);
                    et_otp.setVisibility(View.VISIBLE);
                    hideKeyboard(v);
                }
            }
        });




        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                login_button.setEnabled(false);
                if(!et_otp.getText().toString().trim().isEmpty()){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, et_otp.getText().toString().trim());
                    signInWithPhoneAuthCredential(credential);
                }else{
                    login_button.setEnabled(true);
                    showMessage("Uh-Oh","Please enter OTP");
                }



            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phone,mResendToken);
            }
        });


    }

    private void init(){
        relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        back = (ImageView) findViewById(R.id.back);
        btn_otp = (Button) findViewById(R.id.otp_button);
        et_otp = (EditText) findViewById(R.id.et_otp);
        et_phone = (EditText) findViewById(R.id.et_phone);
        not_registered = (TextView) findViewById(R.id.not_register_text);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        login_button=(Button)findViewById(R.id.login_button);
        resend_otp=(TextView)findViewById(R.id.resend_otp);

        et_otp.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        resend_otp.setVisibility(View.GONE);
        not_registered.setVisibility(View.GONE);
        login_button.setVisibility(View.GONE);


        mAuth=FirebaseAuth.getInstance();

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }

    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                getOtp();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn_otp.setVisibility(View.VISIBLE);
                et_otp.setVisibility(View.GONE);
            }
        });

        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    private void getOtp() {

        et_otp.setVisibility(View.VISIBLE);
        btn_otp.setVisibility(View.GONE);
        resend_otp.setVisibility(View.VISIBLE);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(final PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;

                //for phones that automatically detect otp and move ahead
                verificationSuccessful=true;

                credential_global=credential;
//                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);

                mVerificationInProgress = false;


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    showMessage("Oops!","Your phone number is invalid");
                    changeUI();


                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(getApplicationContext(),"An internal error occurred. Please try again later",Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(getApplicationContext(),"OTP Sent",Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;
                mResendToken = token;
                login_button.setVisibility(View.VISIBLE);

            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);


    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();


                            final DocumentReference doc_ref= FirebaseFirestore.getInstance().collection(getString(R.string.users)).document(user.getUid());

                            doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot snapshot) {
                                    if(!snapshot.exists()){
                                        Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                                        finish();
                                        overridePendingTransition(0,0);
                                        intent.putExtra("phone",phone);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);

                                    }else{
                                        Intent intent =  new Intent(getApplicationContext(), MainActivity.class);
                                        Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(0,0);
                                        startActivity(intent);

                                    }
                                }
                            });




                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                showMessage("Oops!","Invalid Code Entered");
                                changeUI();
                                login_button.setEnabled(true);

                            }



                        }
                    }
                });
    }

    private void changeUI() {

        Vibrator vibrator = (Vibrator) this.getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(500);
        }
         et_otp.setBackgroundResource(R.drawable.edittext_borders_red);
        progressBar.setVisibility(View.GONE);

    }

    private void hideKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
