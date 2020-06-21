package com.replon.vsm.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.vsm.R;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    RelativeLayout relativeLayout;
    ImageView back;

    Button btn_register;
    EditText et_phone,et_name,et_company_name,et_gst_number, et_city;
    AutoCompleteTextView et_state;
    String phone,name,company_name,gst_number, city, state;

    String [] states;
    Boolean flag;


    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), RegisterActivity.this);

        setContentView(R.layout.activity_register);

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

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=et_name.getText().toString().trim();
                company_name=et_company_name.getText().toString().trim();
                gst_number=et_gst_number.getText().toString().trim().toUpperCase();
                city = et_city.getText().toString().trim();
                state = et_state.getText().toString().trim();

                if(phone.isEmpty()||name.isEmpty()||company_name.isEmpty()|| city.isEmpty()||gst_number.isEmpty() || state.isEmpty()){
                    showMessage("Oops!","Please fill all the fields");
                }else{


                    showMessageDilogue("Please Note!","Kindly verify your details. These cannot be changed later");
                }

            }
        });


        final ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this,R.layout.spinner_item, states);


        et_state.setAdapter(stateAdapter);

        et_state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_state.showDropDown();
                et_state.requestFocus();
                return false;
            }
        });


}


    private void saveData() {
        progressBar.setVisibility(View.VISIBLE);
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


        final DocumentReference doc_ref= FirebaseFirestore.getInstance().collection(getString(R.string.users)).document(user_id);

        doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()){
                    showMessage("Oops!","You have already registered earlier. Kindly Login");
                }else{
                    Map<String, Object> user_data = new HashMap<>();
                    user_data.put(getString(R.string.date_created), new Timestamp(new Date()));
                    user_data.put(getString(R.string.name), name);
                    user_data.put(getString(R.string.city), city);
                    user_data.put(getString(R.string.state),state);
                    user_data.put(getString(R.string.company), company_name);
                    user_data.put(getString(R.string.dealer), false);
                    user_data.put(getString(R.string.gst), gst_number);
                    user_data.put(getString(R.string.phone), phone);



                    doc_ref.set(user_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progressBar.setVisibility(View.GONE);
                            Intent intent =  new Intent(getApplicationContext(), PendingApprovalActivity.class);
                            intent.putExtra("name",name);
                            finish();
                            overridePendingTransition(0,0);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document"+ e.getMessage());
                            progressBar.setVisibility(View.GONE);
                            int index=e.getMessage().indexOf(":");
                            String error_message=e.getMessage().substring(index+1).trim();
                            showMessage("Oops",error_message);

                        }
                    });

                }
            }
        });

    }

    private void init(){
        relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        back = (ImageView) findViewById(R.id.back);
        btn_register = (Button) findViewById(R.id.register_button);
        et_phone=(EditText)findViewById(R.id.et_phone);
        et_name=(EditText)findViewById(R.id.et_name);
        et_company_name=(EditText)findViewById(R.id.et_company_name);
        et_gst_number=(EditText)findViewById(R.id.et_gst_no);
        et_city =(EditText)findViewById(R.id.et_city);

        et_state = (AutoCompleteTextView) findViewById(R.id.et_state);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        
        progressBar.setVisibility(View.GONE);

        phone=getIntent().getStringExtra("phone");
        et_phone.setText(phone);
        et_phone.setEnabled(false);

        states = getResources().getStringArray(R.array.india_states);
        flag=false;




    }

    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    public void showMessageDilogue(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData();
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }
}
