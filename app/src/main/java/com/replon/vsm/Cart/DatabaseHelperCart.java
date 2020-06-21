package com.replon.vsm.Cart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import android.util.Log;

public class DatabaseHelperCart extends SQLiteOpenHelper{

    public static final String TAG = "DatabaseHelperCart";

    public static final String DATABASE_NAME="AddToCart.db";
    public static final String TABLE_NAME="Cart_table";

    public static final String COL_1="document_id"; //unique id for each row of table
    public static final String COL_2="catalogue_image_url";
    public static final String COL_3="date_created";
    public static final String COL_4="fabric";
    public static final String COL_5="name";
    public static final String COL_6="packaging";
    public static final String COL_7="pieces";
    public static final String COL_8="price";
    public static final String COL_9="saree_image_url";
    public static final String COL_10="saree_length";
    public static final String COL_11="tag";
    public static final String COL_12="type";
    public static final String COL_13="pdf_url";
    public static final String COL_14="video_url";





    public DatabaseHelperCart(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+ "("+COL_1+" TEXT PRIMARY KEY,"+COL_2+" TEXT,"+COL_3+" TEXT,"+COL_4+" TEXT,"+COL_5+" TEXT,"+COL_6+" TEXT,"+COL_7+" TEXT,"+COL_8+" TEXT,"+COL_9+","+COL_10+" TEXT,"+COL_11+" INTEGER,"+COL_12+" INTEGER,"+COL_13+" TEXT,"+COL_14+" TEXT)");
        Log.i(TAG,"Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Log.i(TAG,"onUpgrade running");
    }

    public boolean addDataToCart(String document_id,String catalogue_image_url,String date_created,String fabric, String name,String packaging, String pieces,
                              String price,String saree_image_url,String saree_length,int tag, int type, String pdf_url, String video_url){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(COL_1,document_id);
        contentValues.put(COL_2,catalogue_image_url);
        contentValues.put(COL_3,date_created);
        contentValues.put(COL_4,fabric);
        contentValues.put(COL_5,name);
        contentValues.put(COL_6,packaging);
        contentValues.put(COL_7,pieces);
        contentValues.put(COL_8,price);
        contentValues.put(COL_9,saree_image_url);
        contentValues.put(COL_10,saree_length);
        contentValues.put(COL_11,tag);
        contentValues.put(COL_12,type);
        contentValues.put(COL_13,pdf_url);
        contentValues.put(COL_14,video_url);



        //if data not inserted it will return -1 else it will show the values

        long result=db.insert(TABLE_NAME,null,contentValues);

        if(result==-1){
            return false;
        }else{
            return true;
        }

    }

    public boolean removeDataFromCart(String document_id){
        SQLiteDatabase db=this.getWritableDatabase();

        long result =db.delete(TABLE_NAME," "+COL_1+" = ?",new String[] {document_id} );

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateQuantity(String id,String quantity){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();


        db.insert(TABLE_NAME,null,contentValues);

        long result=db.update(TABLE_NAME,contentValues," "+COL_1+" = ?",new String[]{ id });

        if(result==-1){
            return false;
        }else{
            return true;
        }

    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }

    public Cursor getDataWithDocumentId(String document_id){
        SQLiteDatabase db=this.getWritableDatabase();
        String[] columns ={COL_1, COL_2,COL_3,COL_4,COL_5,COL_6,COL_7,COL_8,COL_9,COL_10,COL_11,COL_12,COL_13,COL_14};

        Cursor res = db.query(TABLE_NAME,columns, " "+COL_1+" = ?" , new String[] {document_id }, null, null, null);
        return res;
    }

}