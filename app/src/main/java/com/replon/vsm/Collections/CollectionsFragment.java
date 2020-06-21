package com.replon.vsm.Collections;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.replon.vsm.Cart.CartActivity;
import com.replon.vsm.Login.LoginActivity;
import com.replon.vsm.Login.PendingApprovalActivity;
import com.replon.vsm.Login.ProfileActivity;
import com.replon.vsm.R;
import com.replon.vsm.Utility.ContentsCatalogue;
import com.replon.vsm.Utility.ContentsUser;
import com.replon.vsm.Utility.DefaultTextConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionsFragment extends Fragment {


    private static final String TAG = "CollectionFrag";
    View view;

    int SCREEN_HEIGHT, SCREEN_WIDTH, HALF_SCREEN_HEIGHT, HALF_SCREEN_WIDTH;

    ArrayList<ContentsCatalogue> catalogueList,customHomeTopList;
    ArrayList<String> homeImageurlList,homeTextList,sareeImageUrlList,weavesImageUrlList;
    ArrayList<ContentsUser> userArrayList;


    ImageView profile,cart_image,img_vsm_Coming_soon, img_omjin_logo, img_omjin_coming_soon;
    TextView vsm_left_text;

    FrameLayout bottom_right_frame,bottom_left_frame,frame_top;
    String phone,name,company_name,gst_number, city,state;
    boolean dealer=false;



    //firebase
    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    ViewPager viewPagerTop,viewPagerLeft,viewPagerRight;
    CustomSwipeAdapterTop customSwipeAdapterTop;
    CustomSwipeAdapterLeft customSwipeAdapterLeft;
    CustomSwipeAdapterRight customSwipeAdapterRight;
    private int current_positionTop=0,current_positionLeft=0,current_positionRight=0;

    Timer timerTop,timerLeft,timerRight;


    public CollectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_collections, container, false);


        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getContext().getColor(R.color.lightGrey));
        }

        init();



        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_HEIGHT = size.y;
        SCREEN_WIDTH = size.x;
        HALF_SCREEN_HEIGHT = (SCREEN_HEIGHT - 60 - 45)/2;
        HALF_SCREEN_WIDTH = SCREEN_WIDTH/2;



        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()==null){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                }else if(dealer){

                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("phone",phone);
                    intent.putExtra("gst_number",gst_number);
                    intent.putExtra("company_name",company_name);
                    intent.putExtra("city", city);
                    intent.putExtra("state", state);
                    intent.putExtra("dealer",dealer);

                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                }else if(!dealer){
                    Intent intent = new Intent(getActivity(), PendingApprovalActivity.class);
                    intent.putExtra("name",name);

                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                }

            }
        });





        Log.i(TAG,"Dealer is "+dealer);

        customSwipeAdapterTop =new CustomSwipeAdapterTop(getActivity(),homeImageurlList,homeTextList,customHomeTopList,userArrayList);
        viewPagerTop.setAdapter(customSwipeAdapterTop);

        customSwipeAdapterLeft =new CustomSwipeAdapterLeft(getActivity(),sareeImageUrlList,catalogueList,userArrayList);
        viewPagerLeft.setAdapter(customSwipeAdapterLeft);

        customSwipeAdapterRight =new CustomSwipeAdapterRight(getActivity(),weavesImageUrlList,catalogueList,userArrayList);
        viewPagerRight.setAdapter(customSwipeAdapterRight);

        cart_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.putExtra("dealer",dealer);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("userList",userArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
            }
        });

        viewPagerTop.setOffscreenPageLimit(5);
        viewPagerRight.setOffscreenPageLimit(3);
        viewPagerRight.setOffscreenPageLimit(3);
        final Handler handler=new Handler();

        final Runnable runnableTop=new Runnable() {
            @Override
            public void run() {


                if (current_positionTop<homeImageurlList.size()){
                    viewPagerTop.setCurrentItem(current_positionTop,true);
                    current_positionTop+=1;
                }else{
                    current_positionTop=0;
                    viewPagerTop.setCurrentItem(current_positionTop,true);
                    current_positionTop=1;
                }



            }
        };


        final Runnable runnableLeft=new Runnable() {
            @Override
            public void run() {


                if (current_positionLeft<sareeImageUrlList.size()){
                    viewPagerLeft.setCurrentItem(current_positionLeft,true);
                    current_positionLeft+=1;
                }else{
                    current_positionLeft=0;
                    viewPagerLeft.setCurrentItem(current_positionLeft,true);
                    current_positionLeft=1;
                }


            }
        };


        final Runnable runnableRight=new Runnable() {
            @Override
            public void run() {


                if (current_positionRight<weavesImageUrlList.size()){
                    viewPagerRight.setCurrentItem(current_positionRight,true);
                    current_positionRight+=1;
                }else{
                    current_positionRight=0;
                    viewPagerRight.setCurrentItem(current_positionRight,true);
                    current_positionRight=1;
                }
            }
        };


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnableTop);
            }
        },0,5000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnableLeft);
            }
        },0,5000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnableRight);
            }
        },0,5000);






        return view;
    }



    private void init(){

        viewPagerTop=(ViewPager)view.findViewById(R.id.viewPagerTop);
        viewPagerRight=(ViewPager)view.findViewById(R.id.viewPagerRight);
        viewPagerLeft=(ViewPager)view.findViewById(R.id.viewPagerLeft);
        profile = (ImageView)view.findViewById(R.id.profile_image);
        cart_image = (ImageView)view.findViewById(R.id.cart_image);
        frame_top=(FrameLayout)view.findViewById(R.id.frame_top);
        bottom_left_frame=(FrameLayout)view.findViewById(R.id.bottom_left_frame);
        bottom_right_frame=(FrameLayout)view.findViewById(R.id.bottom_right_frame);
        vsm_left_text = (TextView) view.findViewById(R.id.text_left);
        img_vsm_Coming_soon = (ImageView) view.findViewById(R.id.image_left_soon) ;
        img_omjin_logo = (ImageView) view.findViewById(R.id.image_right) ;
        img_omjin_coming_soon = (ImageView) view.findViewById(R.id.image_right_soon) ;


        db=FirebaseFirestore.getInstance();
        catalogueList=new ArrayList<>();
        homeImageurlList=new ArrayList<>();
        homeTextList=new ArrayList<>();
        sareeImageUrlList=new ArrayList<>();
        weavesImageUrlList=new ArrayList<>();
        userArrayList=new ArrayList<>();
        customHomeTopList=new ArrayList<>();

        dealer=false;

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();


    }


    private void getUserData() {
        DocumentReference doc_ref=FirebaseFirestore.getInstance().collection(getString(R.string.users)).document(firebaseUser.getUid());

        listenerRegistration=doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                userArrayList.clear();
                if(snapshot.exists()){
//                    Log.i(TAG,"Data found with "+snapshot.getData());

                    userArrayList.add(new ContentsUser(snapshot.getString(getString(R.string.name_user)),
                            snapshot.getString(getString(R.string.phone)),
                            snapshot.getString(getString(R.string.company)),
                            snapshot.getString(getString(R.string.city)),
                            snapshot.getString(getString(R.string.state)),
                            snapshot.getString(getString(R.string.gst)),
                            snapshot.getBoolean(getString(R.string.dealer)).toString()));

                    name=snapshot.getString(getString(R.string.name_user));
                    phone=snapshot.getString(getString(R.string.phone));
                    company_name=snapshot.getString(getString(R.string.company));
                    gst_number=snapshot.getString(getString(R.string.gst));
                    city =snapshot.getString(getString(R.string.city));
                    state=snapshot.getString(getString(R.string.state));
                    dealer=snapshot.getBoolean(getString(R.string.dealer));

                    customSwipeAdapterRight.notifyDataSetChanged();
                    customSwipeAdapterLeft.notifyDataSetChanged();
                    customSwipeAdapterTop.notifyDataSetChanged();
                }else{
                    Log.i(TAG,"No such document");
                    mAuth.signOut();
                }
            }
        });
    }


    public void getData() {
        Query query = db.collection(getString(R.string.catalogues)).orderBy(getString(R.string.date_created), Query.Direction.DESCENDING);




       listenerRegistration= query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.w(TAG,"error is "+e.getMessage());
                }

                if(snapshot!=null && !snapshot.getDocuments().isEmpty()){

                    catalogueList.clear();
                    homeImageurlList.clear();
                    homeTextList.clear();
                    sareeImageUrlList.clear();
                    weavesImageUrlList.clear();

                    for(QueryDocumentSnapshot doc : snapshot){
//                        Log.i(TAG,"data found with "+doc.getData());

                        String home_text,home_image_url;
                        Boolean home= Boolean.parseBoolean(doc.get(getString(R.string.home)).toString());
                        if(home){
                            home_text=doc.getString(getString(R.string.home_text));
                            home_image_url=doc.getString(getString(R.string.home_image_url));
                        }else{
                            home_text="";
                            home_image_url="";
                        }

                        SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                        Timestamp ts_date_created = (Timestamp) doc.get(getString(R.string.date_created));
                        String date_created = sfd_viewFormat.format(ts_date_created.toDate());
                        String video_url="";

                        if (doc.getString(getString(R.string.video_url))!=null){
                            video_url = doc.getString(getString(R.string.video_url));
                        }else{
                            video_url="";
                        }



                        catalogueList.add(
                                new ContentsCatalogue(
                                        doc.getString(getString(R.string.name)),
                                        doc.getString(getString(R.string.fabric)),
                                        doc.getString(getString(R.string.saree_length)),
                                        doc.getString(getString(R.string.packaging)),
                                        doc.getString(getString(R.string.pieces)),
                                        Integer.parseInt(doc.get(getString(R.string.type)).toString()),
                                        Integer.parseInt(doc.get(getString(R.string.tag)).toString()),
                                        home,
                                        home_text,
                                        home_image_url,
                                        doc.getString(getString(R.string.price)),
                                        doc.getString(getString(R.string.catalogue_image_url)),
                                        (ArrayList<String>)doc.get(getString(R.string.saree_image_url)),
                                        doc.getString(getString(R.string.pdf_url)),
                                        video_url,
                                        date_created,
                                        doc.getId()

                        ));


                    }

                    for(int i=0;i<catalogueList.size();i++){
                        if(catalogueList.get(i).isHome()){
                            if(homeImageurlList.size()<5



                            ){
                                homeImageurlList.add(catalogueList.get(i).getHome_image_url());
                                homeTextList.add(catalogueList.get(i).getHome_text());
                                customHomeTopList.add(catalogueList.get(i));

                            }


                        }

                        customSwipeAdapterTop.notifyDataSetChanged();

                    }


                    for(int i=0;i<catalogueList.size();i++){
                        if(catalogueList.get(i).isHome()){
                            if(homeImageurlList.size()<5){
                                homeImageurlList.add(catalogueList.get(i).getHome_image_url());
                                homeTextList.add(catalogueList.get(i).getHome_text());
                                customHomeTopList.add(catalogueList.get(i));

                            }


                        }

                        customSwipeAdapterTop.notifyDataSetChanged();

                    }


                    for (int i = 0; i < catalogueList.size(); i++) {

                        if (catalogueList.get(i).getType() == 0) {

                            if(weavesImageUrlList.size()<3){
                                weavesImageUrlList.add(catalogueList.get(i).getCatalogue_image_url());
                            }

                        }else if(catalogueList.get(i).getType() == 1){

                            if(sareeImageUrlList.size()<3){
                                sareeImageUrlList.add(catalogueList.get(i).getCatalogue_image_url());
                            }
                        }

                        customSwipeAdapterLeft.notifyDataSetChanged();
                        customSwipeAdapterRight.notifyDataSetChanged();

                    }

//                    //by default low to high
//                    SortingClass sortingClass=new SortingClass(catalogueList);
//                   Collections.reverse(sortingClass.sortPriceLowToHigh());

                    if(weavesImageUrlList.size() == 0){
                        img_omjin_coming_soon.setVisibility(View.VISIBLE);
                        img_omjin_logo.setVisibility(View.GONE);
                    }else{
                        img_omjin_coming_soon.setVisibility(View.GONE);
                        img_omjin_logo.setVisibility(View.VISIBLE);
                    }

                    if(sareeImageUrlList.size() == 0){
                        vsm_left_text.setVisibility(View.GONE);
                        img_vsm_Coming_soon.setVisibility(View.VISIBLE);
                    }else{
                        vsm_left_text.setVisibility(View.VISIBLE);
                        img_vsm_Coming_soon.setVisibility(View.GONE);
                    }




                }else{
                    Log.i(TAG,"No data is present");
                }
            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();
        if (listenerRegistration!= null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

        if(firebaseUser!=null){
            getUserData();
        }else{
            dealer=false;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timerTop!=null){
            timerTop=null;
        }
    }
}
