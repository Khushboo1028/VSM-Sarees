package com.replon.vsm.Catalogue;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.replon.vsm.Cart.CartActivity;
import com.replon.vsm.Cart.DatabaseHelperCart;
import com.replon.vsm.R;
import com.replon.vsm.Utility.ContentsCatalogue;
import com.replon.vsm.Utility.ContentsUser;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.util.ArrayList;


public class CatalogViewActivity extends YouTubeBaseActivity {

    public static final String TAG = "AllCollectionsActivity";
    ImageView back, cartImage,add_to_cart_image;
    //GridView gridView;
    ArrayList<ContentsCatalogue> catalogueList;
    ArrayList<String> sareeImageUrlList;
    ArrayList<ContentsUser> userArrayList;
    int position;

    TextView fabric_text,saree_length_text,packaging_text,pieces_text,catalog_name_text,download_pdf,add_to_cart_text,price_text;
    LinearLayout add_to_cart;

    DatabaseHelperCart myDb;
    String str_saree_image_url;
    boolean dealer;
    LinearLayout enquire_layout, price_layout;

    RecyclerView recyclerView;

    FirebaseAuth mAuth;

    String message1,message2, message3,message4,message5;

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer activePlayer;

    String video_url;

    Boolean FULL_SCREEN_FLAG = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), CatalogViewActivity.this);

        setContentView(R.layout.activity_catalog_view);

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.lightBlack));
        }

        init();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        catalogueList = this.getIntent().getExtras().getParcelableArrayList("catalogueList");
        userArrayList=this.getIntent().getExtras().getParcelableArrayList("userList");
        position=getIntent().getIntExtra("position",0);
        sareeImageUrlList=catalogueList.get(position).getSaree_image_url();
        pieces_text.setText(catalogueList.get(position).getPieces());
        fabric_text.setText(capitalize(catalogueList.get(position).getFabric()));
        packaging_text.setText(capitalize(catalogueList.get(position).getPackaging()));
        catalog_name_text.setText(capitalize(catalogueList.get(position).getName()));
        price_text.setText(" ₹ " + catalogueList.get(position).getPrice());
        saree_length_text.setText(catalogueList.get(position).getSaree_length());
        video_url = catalogueList.get(position).getVideo_url();

        dealer=getIntent().getBooleanExtra("dealer",false);

        Log.i(TAG, "position is"+position);

//        Log.i(TAG,"Pieces in this catalogue are "+catalogueList.get(position).getPieces());

        if(mAuth.getCurrentUser()==null){
            dealer=false;
        }

        if(dealer){
            price_layout.setVisibility(View.VISIBLE);
        }

        CatalogueAdapter mAdapter=new CatalogueAdapter(CatalogViewActivity.this, sareeImageUrlList);
        recyclerView.setAdapter(mAdapter);

        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.putExtra("dealer",dealer);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("userList",userArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
            }
        });

        download_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri path= Uri.parse(catalogueList.get(position).getPdf_url());
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path , "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try
                {
                    startActivity(pdfIntent);
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(),"No Application available to view PDF",
                            Toast.LENGTH_SHORT).show();
                }




            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToCart(position,catalogueList.get(position).getDocument_id());
            }
        });

        enquire_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               contactUsDialog();
            }
        });

        if (video_url.equals("")){
            youTubePlayerView.setVisibility(View.GONE);
        }else{
            youTubePlayerView.setVisibility(View.VISIBLE);
        }

        youTubePlayerView.initialize("AIzaSyD70aNotNwhQoRmECQz2m8S7aYlUgXCvo4",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {

                        // do any work here to cue video, play video, etc.

                        activePlayer = youTubePlayer;
                        String url_cue = video_url.substring(video_url.lastIndexOf("=")+1);
                        activePlayer.cueVideo(url_cue);

                        activePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                            @Override
                            public void onFullscreen(boolean b) {
                                FULL_SCREEN_FLAG =b;
                            }
                        });

                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }

                });

    }

    private void saveDataToCart(int position,String document_id) {


        str_saree_image_url = ("" + sareeImageUrlList).replaceAll("(^.|.$)", "  ").replace(", ", ", " );

        if(!checkIfItemPresentInCart(document_id)){
            boolean isInserted= myDb.addDataToCart(
                    catalogueList.get(position).getDocument_id(),
                    catalogueList.get(position).getCatalogue_image_url(),
                    catalogueList.get(position).getDate_created(),
                    catalogueList.get(position).getFabric(),
                    catalogueList.get(position).getName(),
                    catalogueList.get(position).getPackaging(),
                    catalogueList.get(position).getPieces(),
                    catalogueList.get(position).getPrice(),
                    str_saree_image_url,
                    catalogueList.get(position).getSaree_length(),
                    catalogueList.get(position).getTag(),
                    catalogueList.get(position).getType(),
                    catalogueList.get(position).getPdf_url(),
                    catalogueList.get(position).getVideo_url()


            );
            if(isInserted){
                Log.i(TAG,"Data added to cart database");
                add_to_cart_text.setText("ADDED");
                add_to_cart_image.setImageDrawable(getDrawable(R.drawable.ic_check_white));

            }else{
                Log.i(TAG,"Unable to add data to cart database");
                showMessage("Uh-Oh!","An error occurred.");
            }
        }else{
            Log.i(TAG,"Item already present in cart");
            showMessage("Uh-Oh!","Item already in cart.");


        }


    }

    private boolean checkIfItemPresentInCart(String document_id) {


        StringBuffer buffer=new StringBuffer();


        Cursor cursor = myDb.getDataWithDocumentId(document_id);
        if(cursor.getCount()==0){
            Log.i(TAG,"There is no data in cart with this document id");
            add_to_cart_text.setText("Add to Cart");
            add_to_cart_image.setImageDrawable(getDrawable(R.drawable.ic_add_to_cart_white));
            add_to_cart.setEnabled(true);
            return false;

        }else{

            add_to_cart_text.setText("ADDED");
            add_to_cart_image.setImageDrawable(getDrawable(R.drawable.ic_check_white));
            add_to_cart.setEnabled(false);

            while(cursor.moveToNext()){

                buffer.append("DOCUMENT_ID: " +cursor.getString(0)+"\n" );


            }

            Log.i(TAG,buffer.toString());
            return true;

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
        final AlertDialog.Builder builder=new AlertDialog.Builder(CatalogViewActivity.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    private void init(){

        back = (ImageView) findViewById(R.id.back);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cartImage = (ImageView) findViewById(R.id.cart_image);
        fabric_text=(TextView)findViewById(R.id.fabric_text);
        saree_length_text=(TextView)findViewById(R.id.saree_length_text);
        packaging_text=(TextView)findViewById(R.id.packaging_text);
        pieces_text=(TextView)findViewById(R.id.pieces_text);
        catalog_name_text=(TextView)findViewById(R.id.catalog_name_text);
        price_text=(TextView)findViewById(R.id.price_text);
        download_pdf=(TextView)findViewById(R.id.download_pdf_text);
        add_to_cart=(LinearLayout)findViewById(R.id.add_to_cart_linear_layout);
        add_to_cart_text=(TextView)findViewById(R.id.add_to_cart_text);
        add_to_cart_image=(ImageView)findViewById(R.id.add_to_cart_image);
        enquire_layout=(LinearLayout)findViewById(R.id.enquire_layout);
        price_layout = (LinearLayout)findViewById(R.id.price_layout);


        catalogueList=new ArrayList<>();
        sareeImageUrlList=new ArrayList<>();
        userArrayList=new ArrayList<>();

        price_layout.setVisibility(View.GONE);

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.player);

        myDb=new DatabaseHelperCart(getApplicationContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth=FirebaseAuth.getInstance();

    }


    public void contactUsDialog() {

        final String call = "Call";
        final String whatsApp = "WhatsApp";
        final CharSequence[] items = {call, whatsApp};
        final AlertDialog.Builder builder = new AlertDialog.Builder(CatalogViewActivity.this);

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

                    if(dealer){


                        message2="Hello, I would like to enquire "+catalogueList.get(position).getName() +" catalog.";


                        message3="\n"+userArrayList.get(0).getCity() +", "+userArrayList.get(0).getState();
                        message4="\n"+userArrayList.get(0).getCompany_name()+"\n"+userArrayList.get(0).getPhone();

                        message5 ="\n\n- Sent from V.S.M. for Android";

                        message2 = message2.replace(" ","%20");
                        message2 = message2.replace("\n","%0a");

                        message3 = message3.replace(" ","%20");
                        message3 = message3.replace("\n","%0a");

                        message4 = message4.replace(" ","%20");
                        message4 = message4.replace("\n","%0a");

                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+message2+ message3 +message4+message5);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }else{
                        //not a dealer
                        String message="Hello, I would like to enquire about  "+catalogueList.get(position).getName() +" catalog." + "\n\n - Sent from V. S. M. for Android";
                        message = message.replace(" ","%20");
                        message = message.replace("\n","%0a");
                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phoneNumber+"&text="+message);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            }
        });


        builder.show();
        builder.setCancelable(true);



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (FULL_SCREEN_FLAG){
            activePlayer.setFullscreen(false);
        }else{
            finish();
            overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
        }




    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfItemPresentInCart(catalogueList.get(position).getDocument_id());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(activePlayer!=null){
            activePlayer.release();
            activePlayer=null;
        }
    }
}
