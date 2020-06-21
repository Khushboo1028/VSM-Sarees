package com.replon.vsm.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.replon.vsm.R;
import com.replon.vsm.Utility.DefaultTextConfig;

public class PendingApprovalActivity extends AppCompatActivity {

    public static final String TAG = "PendingActivity";
    RelativeLayout relativeLayout;
    TextView contact_us, pending,sign_out;
    Button btn_continue;
    String name;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), PendingApprovalActivity.this);

        setContentView(R.layout.activity_pending_approval);

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

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            contactUsDialog();
            }
        });
        String pending_text = getString(R.string.pending_text);


        //Change Name here
        name=getIntent().getStringExtra("name");
        if(name!=null){
            pending_text = pending_text.replace("name",name);

        }else{
            pending_text = pending_text.replace("name","");
        }

        pending.setText(pending_text);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut("","Are you sure you want to logout?");
            }
        });
    }

    private void init(){
        relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        contact_us = (TextView) findViewById(R.id.contact_us_text);
        pending = (TextView) findViewById(R.id.pending_text);
        sign_out = (TextView) findViewById(R.id.sign_out_text);
        btn_continue = (Button) findViewById(R.id.continue_button);

        mAuth=FirebaseAuth.getInstance();



    }

    public void logOut(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(PendingApprovalActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                finish();
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
                    String message=getString(R.string.verification_status_text);
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

    }
}
