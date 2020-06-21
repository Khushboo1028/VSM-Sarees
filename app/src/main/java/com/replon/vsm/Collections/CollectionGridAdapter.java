package com.replon.vsm.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.replon.vsm.Cart.DatabaseHelperCart;
import com.replon.vsm.Catalogue.CatalogViewActivity;
import com.replon.vsm.R;
import com.replon.vsm.Utility.ContentsCatalogue;
import com.replon.vsm.Utility.ContentsUser;

import java.util.ArrayList;

public class CollectionGridAdapter extends BaseAdapter {

    public static final String TAG = "CollectionGridAdapter";
//    private ArrayList<String> images;
//    private ArrayList<String> names;
//    private ArrayList<String> prices;
    private ArrayList<ContentsUser> userList;
    private DatabaseHelperCart myDb;
    private ArrayList<String> saree_image_url;
    private String str_saree_image_url;
    private ArrayList<ContentsCatalogue> catalogueArrayList;


    private Activity activity;

    private LayoutInflater inflater;

    LinearLayout add_to_cart;
    ImageView add_to_cart_image;
    TextView add_to_cart_text;

    boolean dealer;

    public CollectionGridAdapter(ArrayList<ContentsCatalogue> catalogueArrayList, Activity activity,ArrayList<ContentsUser> userList) {
        this.catalogueArrayList = catalogueArrayList;
        this.activity = activity;
        this.userList=userList;

    }



    @Override
    public int getCount() {
        return catalogueArrayList.size();
    }

    @Override
    public Object getItem(final int position) {


        Object object = new Object(){
            String rname = catalogueArrayList.get(position).getName();
            String rprice = catalogueArrayList.get(position).getPrice();

        };
        return object;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View gridView=convertView;

        if(convertView==null){

            inflater=(LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView=inflater.inflate(R.layout.row_catalog_grid,null);
        }

        ImageView image=(ImageView) gridView.findViewById(R.id.catalog_image);
        TextView price=(TextView)gridView.findViewById(R.id.catalog_price);
        TextView name = (TextView) gridView.findViewById(R.id.catalog_name);
        LinearLayout price_layout=(LinearLayout)gridView.findViewById(R.id.prices_layout);
        ImageView tag_image=(ImageView)gridView.findViewById(R.id.tag_image);

        add_to_cart=(LinearLayout)gridView.findViewById(R.id.add_to_cart_linear_layout);

        add_to_cart_image = (ImageView) gridView.findViewById(R.id.add_to_cart_image);
        add_to_cart_text = (TextView) gridView.findViewById(R.id.add_to_cart_text);

        image.setClipToOutline(true);

        Glide.with(activity.getApplicationContext()).load(catalogueArrayList.get(position).getCatalogue_image_url()).placeholder(R.drawable.ic_place_holder_catalog).into(image);

        if(catalogueArrayList.get(position).getTag() == 1){
            tag_image.setImageDrawable(activity.getApplicationContext().getDrawable(R.drawable.ic_best_seller));
        }else if(catalogueArrayList.get(position).getTag() == 2){
            tag_image.setImageDrawable(activity.getApplicationContext().getDrawable(R.drawable.ic_featured));
        }else if(catalogueArrayList.get(position).getTag() == 3){
            tag_image.setImageDrawable(activity.getApplicationContext().getDrawable(R.drawable.ic_new_arrival));

        }else{
            tag_image.setVisibility(View.GONE);
        }


        if(userList==null || userList.isEmpty()){
            dealer=false;
        }else{
            dealer=Boolean.parseBoolean(userList.get(0).getDealer());
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"item clicked with name "+catalogueArrayList.get(position).getName());
                Intent intent =new Intent(activity.getApplicationContext(), CatalogViewActivity.class);
                intent.putExtra("position",position);
                if(dealer){
                    intent.putExtra("dealer",true);
                }else{
                    intent.putExtra("dealer",false);
                }

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("catalogueList", catalogueArrayList);
                bundle.putParcelableArrayList("userList",userList);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.getApplicationContext().startActivity(intent);

            }
        });
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveDataToCart(position,catalogueArrayList.get(position).getDocument_id());



            }
        });


        name.setText(capitalize(catalogueArrayList.get(position).getName()));


        if(dealer){
            price_layout.setVisibility(View.VISIBLE);
            price.setText(catalogueArrayList.get(position).getPrice() + " x " + catalogueArrayList.get(position).getPieces());
        }else{
            price_layout.setVisibility(View.GONE);
        }


        return gridView;

    }


    public void updateList(ArrayList<ContentsCatalogue> list){
        catalogueArrayList = list;
        notifyDataSetChanged();
    }


    private void saveDataToCart(int position,String document_id) {
        myDb=new DatabaseHelperCart(activity.getApplicationContext());
        saree_image_url=new ArrayList<>();
        saree_image_url=catalogueArrayList.get(position).getSaree_image_url();
        str_saree_image_url = ("" + saree_image_url).replaceAll("(^.|.$)", "  ").replace(", ", ", " );

        if(!checkIfItemPresentInCart(document_id)){
            boolean isInserted= myDb.addDataToCart(
                    catalogueArrayList.get(position).getDocument_id(),
                    catalogueArrayList.get(position).getCatalogue_image_url(),
                    catalogueArrayList.get(position).getDate_created(),
                    catalogueArrayList.get(position).getFabric(),
                    catalogueArrayList.get(position).getName(),
                    catalogueArrayList.get(position).getPackaging(),
                    catalogueArrayList.get(position).getPieces(),
                    catalogueArrayList.get(position).getPrice(),
                    str_saree_image_url,
                    catalogueArrayList.get(position).getSaree_length(),
                    catalogueArrayList.get(position).getTag(),
                    catalogueArrayList.get(position).getType(),
                    catalogueArrayList.get(position).getPdf_url(),
                    catalogueArrayList.get(position).getVideo_url()

            );
            if(isInserted){
                Log.i(TAG,"Data added to cart database");
                Toast.makeText(activity.getApplicationContext(),"Item added to cart",Toast.LENGTH_SHORT).show();
                viewDataForDebugging();

            }else{
                Log.i(TAG,"Unable to add data to cart database");
                Toast.makeText(activity.getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();

            }
        }else{
            Log.i(TAG,"Item already present in cart");
            showMessage("Uh-Oh!","Item already in cart.");
            viewDataForDebugging();

        }


    }

    private boolean checkIfItemPresentInCart(String document_id) {


        StringBuffer buffer=new StringBuffer();


            Cursor cursor = myDb.getDataWithDocumentId(document_id);
            if(cursor.getCount()==0){
                Log.i(TAG,"There is no data in cart with this document id");
                return false;

            }else{

                while(cursor.moveToNext()){

                    buffer.append("DOCUMENT_ID: " +cursor.getString(0)+"\n" );


                }

                Log.i(TAG,buffer.toString());
                return true;

            }

    }


    private void viewDataForDebugging() {

        myDb = new DatabaseHelperCart(activity.getApplicationContext());


        StringBuffer buffer=new StringBuffer();


            Cursor res = myDb.getAllData();
            if(res.getCount()==0){
                Log.i(TAG,"There is no stored data");

            }else{

                while(res.moveToNext()){


                    buffer.append("document_id: " +res.getString(0)+"\n" );
                    buffer.append("catalogue_image_url: " +res.getString(1)+"\n");
                    buffer.append("date_created: "+res.getString(2)+"\n");
                    buffer.append("fabric " +res.getString(3)+"\n");
                    buffer.append("name " +res.getString(4)+"\n");
                    buffer.append("packaging " +res.getString(5)+"\n");
                    buffer.append("pieces " +res.getString(6)+"\n");
                    buffer.append("price " +res.getString(7)+"\n");
                    buffer.append("saree_image_url " +res.getString(8)+"\n");
                    buffer.append("saree_length " +res.getString(9)+"\n");
                    buffer.append("tag " +res.getInt(10)+"\n");
                    buffer.append("type " +res.getInt(11)+"\n");
                    buffer.append("pdf_url " +res.getString(12)+"\n\n");
                    buffer.append("video_url " +res.getString(13)+"\n\n");



                }

                Log.i(TAG,buffer.toString());


            }



    }

    public static String capitalize(@NonNull String input) {

        String[] words = input.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i > 0 && word.length() > 0) {
                builder.append(" ");
            }

            String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
            builder.append(cap);
        }
        return builder.toString();
    }


    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(activity,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
}
