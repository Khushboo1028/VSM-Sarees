package com.replon.vsm.Cart;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.replon.vsm.R;
import com.replon.vsm.Utility.ContentsCatalogue;
import com.replon.vsm.Utility.ContentsUser;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {


    public static final String TAG = "CartActivity";
    ImageView back;
    LinearLayout whatsapp_rel, call_rel;

    RecyclerView recyclerView;
    CartAdapter cartAdapter;


    ArrayList<ContentsCatalogue> cartList;
    DatabaseHelperCart myDb;

    ArrayList<String> dummyListForSareesUrl;
    Boolean dealer;

    String message1,message2, message3,message4,message5;
    TextView empty_cart_text;
    TextView enquire_text;

    ArrayList<ContentsUser> userArrayList;

    LottieAnimationView lottieAnimationView;

    ArrayList<String> quantityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), CartActivity.this);

        setContentView(R.layout.activity_cart);

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.lightGrey));
        }

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        whatsapp_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber=getString(R.string.admin_phone_number);
                message1="";message2="";message3="";message4="";message5="";

                if(dealer){
                    message1="Hello, I would like to order about some of your products:\n\n";

                    for(int i=0;i<cartList.size();i++){


                        message2= message2 + "-"+ cartList.get(i).getName() + " "+"Quantity: "+ quantityList.get(i)+ "\n" ;

                    }


                    message3="\n"+userArrayList.get(0).getCity() +", "+userArrayList.get(0).getState();
                    message4="\n"+userArrayList.get(0).getCompany_name()+"\n"+userArrayList.get(0).getPhone()+"\nOrder is subject to availability of stock. You will be contacted for final confirmation.";

                    message5 ="\n\n- Sent from V.S.M. for Android";

                    message1 = message1.replace(" ","%20");
                    message1 = message1.replace("\n","%0a");

                    message2 = message2.replace(" ","%20");
                    message2 = message2.replace("\n","%0a");

                    message3 = message3.replace(" ","%20");
                    message3 = message3.replace("\n","%0a");

                    message4 = message4.replace(" ","%20");
                    message4 = message4.replace("\n","%0a");

                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+message1+message2+ message3 +message4+message5);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }else{
                    //not a dealer

                    message1="Hello, I would like to enquire about some of your products:\n\n";

                    for(int i=0;i<cartList.size();i++){


                        message2= message2 + "-"+ cartList.get(i).getName() + "\n" ;

                    }


                    message3 ="\n- Sent from V.S.M. for Android";

                    message1 = message1.replace(" ","%20");
                    message1 = message1.replace("\n","%0a");

                    message2 = message2.replace(" ","%20");
                    message2 = message2.replace("\n","%0a");

                    message3 = message3.replace(" ","%20");
                    message3 = message3.replace("\n","%0a");

                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+message1+message2+ message3);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }


            }
        });

        call_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = getString(R.string.admin_phone_number);
                tel = "tel:"+tel;
                Intent acCall = new Intent(Intent.ACTION_DIAL);
                acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                acCall.setData(Uri.parse(tel));
                startActivity(acCall);
            }
        });


        dealer=getIntent().getBooleanExtra("dealer",false);

        if(dealer){
            enquire_text.setText("Order");
        }

        getData();


        cartAdapter = new CartAdapter(getApplicationContext(),cartList,dealer,empty_cart_text,lottieAnimationView){
            @Override
            public void onBindViewHolder(@NonNull final CartAdapter.CartViewHolder holder, final int i) {

                final ContentsCatalogue contents = cartList.get(i);

                Glide.with(getApplicationContext()).load(contents.getCatalogue_image_url())
                        .into(holder.catalog_image);

                holder.catalog_name.setText(capitalize(contents.getName()));
                holder.length.setText(contents.getSaree_length());
                holder.packing.setText(capitalize(contents.getPackaging()));
                holder.price.setText(contents.getPrice() + " x " + contents.getPieces());
                holder.fabric.setText(capitalize(contents.getFabric()));

                quantityList.add("1");

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.i(TAG,"Position clicked is "+i);
                        if(checkIfItemPresentInCart(cartList.get(i).getDocument_id())){
                            deleteDataFromSql(cartList.get(i).getDocument_id(),i);

                        }
                    }
                });


                if(!dealer){
                    holder.quantity_layout.setVisibility(View.GONE);
                    holder.price_layout.setVisibility(View.GONE);
                }


                holder.et_quantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        String quant=holder.et_quantity.getText().toString().trim();
                        if(quant!=null || !quant.isEmpty()){

                            quantityList.set(i,quant);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(cartAdapter);

        if(cartList==null || cartList.size()==0){
            empty_cart_text.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
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


    private void getData() {

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



                cartList.add(new ContentsCatalogue(
                        res.getString(4),
                        res.getString(3),
                        res.getString(9),
                        res.getString(5),
                        res.getString(6),
                        res.getInt(11),
                        res.getInt(10),
                        false,
                        "",
                        "",
                        res.getString(7),
                        res.getString(1),
                        dummyListForSareesUrl,
                        res.getString(12),
                        res.getString(13),
                        res.getString(2),
                        res.getString(0)

                ));



            }


            Log.i(TAG,buffer.toString());


        }
    }

    private void init(){
        back = (ImageView) findViewById(R.id.back);
        call_rel = (LinearLayout) findViewById(R.id.call_rel);
        whatsapp_rel = (LinearLayout) findViewById(R.id.whatsapp_rel);
        empty_cart_text=(TextView)findViewById(R.id.empty_cart_text);
        enquire_text=(TextView)findViewById(R.id.enquire_text);


        recyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
        cartList = new ArrayList<>();
        dummyListForSareesUrl=new ArrayList<>();
        quantityList =new ArrayList<>();
        userArrayList=new ArrayList<>();
        myDb=new DatabaseHelperCart(getApplicationContext());

        userArrayList=getIntent().getExtras().getParcelableArrayList("userList");

        lottieAnimationView=(LottieAnimationView)findViewById(R.id.lottieAnimationView);




    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }
}
