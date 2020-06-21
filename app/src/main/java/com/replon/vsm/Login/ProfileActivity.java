package com.replon.vsm.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.replon.vsm.R;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";
    RelativeLayout relativeLayout;
    ImageView back, save;
    String phone,name,company_name,gst_number, city,state;
    boolean dealer;

    EditText et_phone,et_name,et_company_name,et_gst_no, et_city,et_state;
    Button btn_logout;
    FirebaseAuth mAuth;

    FirebaseFirestore db;
    ProgressBar progressBar;

    TextView contact_us_text;
    String token;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ProfileActivity.this);

        setContentView(R.layout.activity_profile);

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

        name=getIntent().getStringExtra("name");
        phone=getIntent().getStringExtra("phone");
        company_name=getIntent().getStringExtra("company_name");
        gst_number=getIntent().getStringExtra("gst_number");
        city =getIntent().getStringExtra("city");
        state =getIntent().getStringExtra("state");
        dealer=getIntent().getBooleanExtra("dealer",false);

        if(name!=null){
            et_name.setText(name);
        }
        if(phone!=null){
            et_phone.setText(phone);
        }
        if(city !=null){
            et_city.setText(city);
        }
        if(state !=null){
            et_state.setText(state);
        }
        if(company_name!=null){
            et_company_name.setText(company_name);
        }
        if(gst_number!=null){
            et_gst_no.setText(gst_number);
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut("","Are you sure you want to logout?");

            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                phone=et_phone.getText().toString().trim();
                name=et_name.getText().toString().trim();
                company_name=et_company_name.getText().toString().trim();
                gst_number=et_gst_no.getText().toString().trim().toUpperCase();

                city = et_city.getText().toString().trim();
                if(phone.isEmpty()||name.isEmpty()||company_name.isEmpty()|| city.isEmpty()||gst_number.isEmpty()) {
                    showMessage("Oops!", "Please fill all the fields");
                }else{
                   // updateData();
                }

            }
        });

        contact_us_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               contactUsDialog();
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult().getToken();

                            Log.i(TAG, "token is " + token);

                        } else {
                            Log.i(TAG, "An error occurred " + task.getException().getMessage());
                        }
                    }
                });

    }

    private void updateData() {

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> data = new HashMap<>();
        data.put(getString(R.string.name), et_name.getText().toString().trim());
        data.put(getString(R.string.company), et_company_name.getText().toString().trim());
        data.put(getString(R.string.city), et_city.getText().toString().trim());
        data.put(getString(R.string.state), et_state.getText().toString().trim());
        data.put(getString(R.string.gst), et_gst_no.getText().toString().trim().toUpperCase());

        DocumentReference doc_ref=db.collection(getString(R.string.users)).document(mAuth.getCurrentUser().getUid());

        doc_ref.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG,"An error occurred in updating data" + e.getMessage());
                showMessage("Oops!","An internal error occurred");
            }
        });
    }


    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
    private void init(){

        relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        back = (ImageView) findViewById(R.id.back);
        save = (ImageView) findViewById(R.id.save);
        et_name=(EditText)findViewById(R.id.et_name);
        et_company_name=(EditText)findViewById(R.id.et_company_name);
        et_gst_no=(EditText)findViewById(R.id.et_gst_no);
        et_city =(EditText)findViewById(R.id.et_city);
        et_state =(EditText)findViewById(R.id.et_state);
        et_phone=(EditText)findViewById(R.id.et_phone);
        btn_logout=(Button)findViewById(R.id.logout_button);
        contact_us_text=(TextView)findViewById(R.id.contact_us_text);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);


        progressBar.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        et_phone.setEnabled(false);
        et_gst_no.setEnabled(false);
        et_state.setEnabled(false);
        et_company_name.setEnabled(false);
        et_city.setEnabled(false);
        et_name.setEnabled(false);



        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

    }

    public void logOut(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DocumentReference doc_id=FirebaseFirestore.getInstance().collection(getString(R.string.users)).document(mAuth.getCurrentUser().getUid());

                doc_id.update("token", FieldValue.arrayRemove(token)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG,"Token removed");
                        mAuth.signOut();
                        finish();
                        Toast.makeText(getApplicationContext(),"SignOut Successful",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG,"An error occurred : "+e.getMessage());
                        Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    public void contactUsDialog() {

        final String call = "Call";
        final String whatsApp = "WhatsApp";
        final CharSequence[] items = {call, whatsApp};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("How would you like to enquire?");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(call)) {
                    String tel = getString(R.string.admin_phone_number);
                    tel = "tel:"+tel;
                    Intent acCall = new Intent(Intent.ACTION_DIAL);
                    acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    acCall.setData(Uri.parse(tel));
                    startActivity(acCall);
                }
                else if (items[item].equals(whatsApp)) {
                    String phoneNumber=getString(R.string.admin_phone_number);
                    String message=getString(R.string.contact_us_text);
                    message = message.replace(" ","%20");
                    message = message.replace("\n","%0a");
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+message);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });


        builder.show();
        builder.setCancelable(true);



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }
}
