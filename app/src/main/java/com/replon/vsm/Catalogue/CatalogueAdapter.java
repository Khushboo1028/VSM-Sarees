package com.replon.vsm.Catalogue;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.replon.vsm.R;

import java.util.ArrayList;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.CatalogueViewHolder> {

    public static final String TAG = "CatalogueAdapter";
    Activity activity;
   ArrayList<String> imageUrl;


    public CatalogueAdapter(Activity activity,ArrayList<String > imageUrl) {
        this.activity=activity;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public CatalogueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(activity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_catalog_image,null);
        CatalogueViewHolder holder=new CatalogueViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueViewHolder holder, int i) {

        Log.i(TAG,"saree_image url is "+imageUrl.get(i));

        Glide.with(activity.getApplicationContext()).load(imageUrl.get(i)).placeholder(R.drawable.ic_place_holder_catalog).into(holder.saree_image);

    }


    @Override
    public int getItemCount() {
        return imageUrl.size();
    }

    class CatalogueViewHolder extends RecyclerView.ViewHolder{

        private ImageView saree_image;
        public CatalogueViewHolder(@NonNull View itemView) {
            super(itemView);

            saree_image = (ImageView) itemView.findViewById(R.id.saree_image);
            saree_image.setClipToOutline(true);
        }
    }
}
