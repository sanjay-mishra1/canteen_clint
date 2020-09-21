package com.example.sanjay.canteen_clint.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanjay.canteen_clint.R;
import com.example.sanjay.canteen_clint.payments.payment_completed_list;
import com.example.sanjay.canteen_clint.payments.pending_payment_list;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.sanjay.canteen_clint.payments.payment_admin.tax;
import static com.example.sanjay.canteen_clint.payments.pending_from_app.filter;


public class tab_admin extends Fragment {

    public    TabLayout tabLayout;
    public    ViewPager viewPager;
    public  static int int_items= 2;
    public static int totalamount=1;


    public tab_admin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.tab_payment,null);
        tabLayout=(TabLayout)v.findViewById(R.id.tabs);
        viewPager=(ViewPager)v.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter( getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int   position=tab.getPosition();
                if (position==0) {
                    try {

                        TextView t = getActivity().findViewById(R.id.TotalAmount);
                        TextView t1 = getActivity().findViewById(R.id.monthfilter);
                         {
                            t1.setText(getString(R.string.pay));
                        }

                        long total = (payment_admin.totalAmount);
                        t.setText(getFormatedAmount(total));
                        t = getActivity().findViewById(R.id.TotalTax);
                        t.setText(String.format("+ %s TAX", getFormatedAmount(tax)));
                        if (total <= 0) {
                            t.setText(getFormatedAmount(0));
                            t1.setVisibility(View.GONE);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
                else{
                    try {

                        TextView t1 = getActivity().findViewById(R.id.monthfilter);
                        t1.setVisibility(View.VISIBLE);
                     try {
                         if (pending_from_app.filter!=null)
                             t1.setText(dayName(pending_from_app.filter,"yyMM"));
                         else   t1.setText(R.string.all);
                     }catch (Exception e){
                         t1.setText(R.string.all);
                     }
                         TextView t= getActivity().findViewById(R.id.TotalAmount);
                        long total= (payment_admin.totalamountFilters);
                      //  if (total>0)
                        t.setText(getFormatedAmount(total));
                          t= getActivity().findViewById(R.id.TotalTax);
                          total= (payment_admin.taxfilters);
                        t.setText(String.format("+ %s TAX", getFormatedAmount(total)));

                    }catch (Exception e){e.printStackTrace();}

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return v;
    }


    public String dayName(String inputDate, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format,Locale.UK).parse(inputDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("MMM yyyy",Locale.UK).format(date);
    }

    private String getFormatedAmount(long amount){
        return getString(R.string.rupeesSymbol)+" "+NumberFormat.getNumberInstance(Locale.UK).format(amount)+".00";
    }




    public class MyAdapter  extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new pending_payment_list();
                case 1:
                    return new payment_completed_list();



            }
            return null;
        }

        @Override
        public int getCount() {


            return int_items;
        }

        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return "Payment waiting";
                case 1:
                    return "Payments";

            }

            return null;
        }

    }


}
