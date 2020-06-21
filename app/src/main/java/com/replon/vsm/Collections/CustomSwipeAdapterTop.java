package com.replon.vsm.Collections;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.replon.vsm.Catalogue.CatalogViewActivity;
import com.replon.vsm.R;
import com.replon.vsm.Utility.ContentsCatalogue;
import com.replon.vsm.Utility.ContentsUser;

import java.util.ArrayList;

public class CustomSwipeAdapterTop extends PagerAdapter {

    ArrayList<String> imageUrlList,textList;
    private Activity activity;
    private LayoutInflater layoutInflater;
    ArrayList<ContentsCatalogue> catalogueList;
    ArrayList<ContentsUser> userArrayList;


    public CustomSwipeAdapterTop(Activity activity, ArrayList<String> imageUrlList, ArrayList<String> textList,ArrayList<ContentsCatalogue> catalogueList,ArrayList<ContentsUser> userArrayList) {
        this.imageUrlList = imageUrlList;
        this.textList=textList;
        this.activity = activity;
        this.catalogueList=catalogueList;
        this.userArrayList=userArrayList;

    }

    @Override
    public int getCount() {
        return imageUrlList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view==(FrameLayout)o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater=(LayoutInflater) activity.getApplicationContext().getSystemService(activity.getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View itemView=layoutInflater.inflate(R.layout.swipe_layout_top,container,false);
        ImageView imageView=itemView.findViewById(R.id.image_view);
        TextView textView=itemView.findViewById(R.id.image_text);

        Glide.with(activity.getApplicationContext()).load(imageUrlList.get(position)).placeholder(R.drawable.ic_place_holder_collections).into(imageView);
        textView.setText(textList.get(position));
        container.addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "This page was clicked: " + position);

                Intent intent = new Intent(activity.getApplicationContext(), CatalogViewActivity.class);
                intent.putExtra("position",position);
                if(userArrayList!=null && userArrayList.size()>0){
                    intent.putExtra("dealer",Boolean.parseBoolean(userArrayList.get(0).getDealer()));
                }
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("catalogueList", catalogueList);
                bundle.putParcelableArrayList("userList",userArrayList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.getApplicationContext().startActivity(intent);
                activity.overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);

            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}
