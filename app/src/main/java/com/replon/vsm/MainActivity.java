package com.replon.vsm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.replon.vsm.AboutUs.AboutUsFragment;
import com.replon.vsm.Collections.CollectionsFragment;
import com.replon.vsm.Onboarding.OnboardingActivity;
import com.replon.vsm.Utility.DefaultTextConfig;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID="NOTIF";
    public static final String CHANNEL_NAME="Notifications";
    public static final String CHANNEL_DESC="This channel is for all notifications";
    public static final String TAG = "MainActivity";

    private BottomNavigationView tab_bar;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), MainActivity.this);

        setContentView(R.layout.activity_main);

        init();

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.lightGrey));
        }




        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }


        loadFragment(new CollectionsFragment());

        tab_bar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.tab_collections:
                        fragment = new CollectionsFragment();
                        break;

                    case R.id.tab_about_us:
                        fragment = new AboutUsFragment();
                        break;


                    case R.id.tab_chat:
                        String phoneNumber=getString(R.string.admin_phone_number);
                        String message=getString(R.string.intro_whatsapp_text);
                        message = message.replace(" ","%20");
                        message = message.replace("\n","%0a");
                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+message);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        return true;

                }




                return loadFragment(fragment);
            }
        });

        if(mAuth.getCurrentUser()!=null){
            getToken();
        }



    }


    private void init(){

        tab_bar = (BottomNavigationView) findViewById(R.id.tab_bar);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_tab, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        final DocumentReference doc_id = FirebaseFirestore.getInstance().collection(getString(R.string.users)).document(firebaseUser.getUid());


                        doc_id.update("token", FieldValue.arrayUnion(token)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG,"Token updated");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG,"An error occurred : "+e.getMessage());
                            }
                        });
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (isFirstTimeAppStart()) {

            Log.i(TAG,"FIRST TIME USING THE APP");

            Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            setFirstTimeStartStatus(false);

        }else{
            super.onStart();
        }
    }

    private boolean isFirstTimeAppStart(){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSlider", Context.MODE_PRIVATE);
        return ref.getBoolean("FirstTimeStartFlag",true);

    }

    private void setFirstTimeStartStatus(boolean stt){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSlider",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("FirstTimeStartFlag",stt);
        editor.commit();
    }
}
