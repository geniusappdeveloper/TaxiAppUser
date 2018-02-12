package com.app.taxi.driver;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by user1 on 12/29/2017.
 */

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideView> {

    LayoutInflater layoutInflater;
    private ArrayList<HashMap<String,String>> mride_data;
    Context mcontext;
    public RideHistoryAdapter(Context con  , ArrayList<HashMap<String,String>> ride_data) {
this.mcontext = con;
this.mride_data = ride_data;

    }


    @Override
    public RideView onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.from(parent.getContext()).inflate(R.layout.activity_ride_history_adapter, parent , false);
        RideView ride = new RideView(v);
        RideHistoryAdapter.RideView ride_viewholder = new RideHistoryAdapter.RideView(v);
        return ride_viewholder;

    }

    @Override
    public void onBindViewHolder(RideView holder, int position) {


        holder.user_name.setText(mride_data.get(position).get("username"));
        holder.ride_date.setText(mride_data.get(position).get("date"));
        holder.price.setText(mride_data.get(position).get("bill"));
        holder.pickup_source.setText(mride_data.get(position).get("pickup_location"));
        holder.pickup_dest.setText(mride_data.get(position).get("pickup_destination"));
        //holder.user_name.setText(mride_data.get(position).get("username"));
        Glide.with(mcontext).load(mride_data.get(position).get("user_image"))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.user_pic);

    }

    @Override
    public int getItemCount() {
        return mride_data.size();
    }

    public class RideView extends RecyclerView.ViewHolder {

        TextView user_name, ride_date, price,  pickup_source, pickup_dest;
        ImageView user_pic ;

        public RideView(View itemView) {
            super(itemView);
user_name = (TextView)itemView.findViewById(R.id.ride_username_textbox);
            ride_date = (TextView)itemView.findViewById(R.id.ridetime_textview);
            price = (TextView)itemView.findViewById(R.id.ride_amount);
            pickup_source = (TextView)itemView.findViewById(R.id.ride_pickup);
            pickup_dest = (TextView)itemView.findViewById(R.id.ride_destination);
            user_pic = (ImageView)itemView.findViewById(R.id.rideadapter_circleView);

        }
    }
}
