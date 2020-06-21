package com.replon.vsm.AboutUs;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.replon.vsm.R;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    View view;
    LinearLayout location_rel,contact_rel,email_rel,insta_rel;
    TextView developer_credits;


    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        view = inflater.inflate(R.layout.fragment_about_us, container, false);

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.lightBlack));
        }

        init();

        location_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "http://maps.google.co.in/maps?q=" + getString(R.string.company_name)+" "+getString(R.string.company_location);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                getActivity().startActivity(i);

            }
        });

        contact_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    contactUsDialog();

            }
        });

        email_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getString(R.string.admin_email);
                email = "mailto:"+email;
                Intent acMail = new Intent(Intent.ACTION_VIEW);
                acMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                acMail.setData(Uri.parse(email));
                getActivity().startActivity(acMail);
            }
        });

        developer_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_QUICK_VIEW,Uri.parse(getString(R.string.developer_website)));
                getActivity().startActivity(intent);
            }
        });


        insta_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.instagram.com/visionsilkmills/");
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                if (isIntentAvailable(getContext(), insta)){
                    startActivity(insta);
                } else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/visionsilkmills/")));
                }

            }
        });



        return view;
    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public void contactUsDialog() {

        final String call = "Call";
        final String whatsApp = "WhatsApp";
        final CharSequence[] items = {call, whatsApp};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

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
                    String message=getString(R.string.intro_whatsapp_text);
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
    private void init(){

        location_rel = (LinearLayout) view.findViewById(R.id.location_rel);
        contact_rel = (LinearLayout) view.findViewById(R.id.contact_rel);
        email_rel = (LinearLayout) view.findViewById(R.id.email_rel);
        insta_rel = (LinearLayout) view.findViewById(R.id.insta_rel);

        developer_credits = (TextView) view.findViewById(R.id.developer_credits);


    }

}
