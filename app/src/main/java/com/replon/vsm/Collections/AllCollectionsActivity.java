package com.replon.vsm.Collections;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.replon.vsm.Cart.CartActivity;
import com.replon.vsm.R;
import com.replon.vsm.Utility.ContentsCatalogue;
import com.replon.vsm.Utility.ContentsUser;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.util.ArrayList;
import java.util.Collections;

public class AllCollectionsActivity extends AppCompatActivity {


    public static final String TAG = "AllCollectionsActivity";
    ImageView back, cartImage;

    ArrayList<String> prices, imageUrl, names;
    int type;

    GridView gridView;
    TextView header_text;
    String header;
    ArrayList<ContentsCatalogue> catalogueList;
    FirebaseAuth mAuth;

    ArrayList<ContentsCatalogue> customList;
    boolean dealer;

    CollectionGridAdapter gridAdapter;

    EditText searchView;
    TextView price_sort_text;
    ArrayList<ContentsUser> userArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AllCollectionsActivity.this);

        setContentView(R.layout.activity_all_catalogs);


        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.lightGrey));
        }

        init();



        catalogueList = this.getIntent().getExtras().getParcelableArrayList("catalogueList");
        userArrayList=this.getIntent().getExtras().getParcelableArrayList("userList");


        header_text.setText(header);

        if (header_text.getText().toString().equals(getString(R.string.vsm_weaves))){
            type = 0;
        }else if(header_text.getText().toString().equals(getString(R.string.vsm_sarees))){
            type = 1;
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(catalogueList!=null) {

            for (int i = 0; i < catalogueList.size(); i++) {

                if (catalogueList.get(i).getType() == type) {

                    customList.add(catalogueList.get(i));
                }

            }
        }

        dealer=getIntent().getBooleanExtra("dealer",false);

        if(mAuth.getCurrentUser()==null){
            dealer=false;
            price_sort_text.setVisibility(View.GONE);
        }

        if(!dealer){
            price_sort_text.setVisibility(View.GONE);
        }else{
            price_sort_text.setVisibility(View.VISIBLE);
        }
        Log.i(TAG,"Dealer is "+dealer);

        gridAdapter = new CollectionGridAdapter(catalogueList, AllCollectionsActivity.this,userArrayList);
        gridView.setAdapter(gridAdapter);




       price_sort_text.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               SortingClass sortingClass=new SortingClass(customList);
               if( price_sort_text.getText().toString().equals("Price ↓")){
                   Collections.reverse(sortingClass.sortPriceLowToHigh());
                   prices.clear();
                   imageUrl.clear();
                   names.clear();
                   for(int i=0;i<customList.size();i++){
                       prices.add(customList.get(i).getPrice());
                       imageUrl.add(customList.get(i).getCatalogue_image_url());
                       names.add(customList.get(i).getName());


                   }
                   gridAdapter.notifyDataSetChanged();
                   price_sort_text.setText("Price ↑");

               }else if(price_sort_text.getText().toString().equals("Price ↑")){
                   Collections.reverse(sortingClass.sortPriceHighToLow());
                   prices.clear();
                   names.clear();
                   imageUrl.clear();
                   for(int i=0;i<customList.size();i++){
                       prices.add(customList.get(i).getPrice());
                       imageUrl.add(customList.get(i).getCatalogue_image_url());
                       names.add(customList.get(i).getName());
                   }
                   gridAdapter.notifyDataSetChanged();
                   price_sort_text.setText("Price ↓");
               }
           }
       });

        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra("dealer",dealer);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("userList",userArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });


    }



    private void init(){

        back = (ImageView) findViewById(R.id.back);
        gridView = (GridView) findViewById(R.id.grid_view);
        header_text = (TextView) findViewById(R.id.header_text);
        cartImage = (ImageView) findViewById(R.id.cart_image);
        searchView = findViewById(R.id.search_field);
        price_sort_text=(TextView)findViewById(R.id.price_sort_text);

        catalogueList=new ArrayList<>();
        names = new ArrayList<>();
        imageUrl = new ArrayList<>();
        prices = new ArrayList<>();
        customList=new ArrayList<>();
        userArrayList=new ArrayList<>();

        // Getting values from intent
        header = getIntent().getStringExtra("header");
        header = header.replace("\n\n"," ");

        mAuth=FirebaseAuth.getInstance();





    }

    private void filter(String text){
        ArrayList<ContentsCatalogue> temp = new ArrayList();

        Log.i(TAG,"catalogue list is "+catalogueList);
        for(int i=0;i<catalogueList.size();i++){
            if(catalogueList.get(i).getName().toLowerCase().contains(text)){
                temp.add(catalogueList.get(i));
            }
        }

        //update recyclerview
        gridAdapter.updateList(temp);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }
}
