package com.replon.vsm.Cart;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.replon.vsm.R;
import com.replon.vsm.Utility.ContentsCatalogue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public static final String TAG = "CartAdapter";
    private Context context;
    private ArrayList<ContentsCatalogue> cartList;
    private DatabaseHelperCart myDb;
    boolean dealer;
    TextView empty_Cart_text;
    LottieAnimationView lottieAnimationView;


    public CartAdapter(Context context, ArrayList<ContentsCatalogue> cartList, boolean dealer, TextView empty_Cart_text, LottieAnimationView lottieAnimationView) {
        this.context = context;
        this.cartList = cartList;
        this.dealer=dealer;
        this.empty_Cart_text=empty_Cart_text;
        this.lottieAnimationView=lottieAnimationView;

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.row_cart,null);
        CartViewHolder holder=new CartViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.CartViewHolder holder, final int i) {

    }


    public void removeItem(int position) {
        cartList.remove(position);
        notifyDataSetChanged();

        if(cartList.size()==0){
            empty_Cart_text.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
        }else{
            empty_Cart_text.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.GONE);
        }
    }



    public void deleteDataFromSql(String document_id,int i) {
        myDb=new DatabaseHelperCart(context);

        Boolean removeData=myDb.removeDataFromCart(document_id);

        if(removeData){
            Log.i(TAG,"Item successfully removed");

            removeItem(i);


        }else{
            Log.i(TAG,"Item not removed");
        }

    }

    public boolean checkIfItemPresentInCart(String document_id) {

        myDb=new DatabaseHelperCart(context);
        StringBuffer buffer=new StringBuffer();


        Cursor cursor = myDb.getDataWithDocumentId(document_id);
        if(cursor.getCount()==0){
            Log.i(TAG,"Item not present in cart with this ID");
            return false;

        }else{

            while(cursor.moveToNext()){

                buffer.append("DOCUMENT_ID: " +cursor.getString(0)+"\n" );


            }

            Log.i(TAG,buffer.toString());
            Log.i(TAG,"Item found with this document id");
            return true;

        }

    }



    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder{

        ImageView catalog_image,delete;
        TextView catalog_name, fabric, price, length, packing;
        EditText et_quantity;

        LinearLayout price_layout, quantity_layout;

        public CartViewHolder(@NotNull View itemView){
            super(itemView);

            catalog_image = (ImageView)itemView.findViewById(R.id.cart_view_image);
            catalog_image.setClipToOutline(true);
            delete = (ImageView) itemView.findViewById(R.id.delete_image);

            catalog_name = (TextView) itemView.findViewById(R.id.catalog_name_text);
            fabric = (TextView) itemView.findViewById(R.id.fabric_text);
            price = (TextView) itemView.findViewById(R.id.price_text);
            length = (TextView) itemView.findViewById(R.id.length_text);
            packing = (TextView) itemView.findViewById(R.id.packing_text);
            et_quantity=(EditText) itemView.findViewById(R.id.et_quantity);

            price_layout = (LinearLayout) itemView.findViewById(R.id.price_layout);
            quantity_layout = (LinearLayout) itemView.findViewById(R.id.quantity_layout);

        }
    }
}
