package app.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.taxiapp.user.R;

/**
 * Created by user1 on 12/29/2017.
 */

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideView> {

    LayoutInflater layoutInflater;

    public RideHistoryAdapter() {


    }


    @Override
    public RideView onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_adapter, null);
        RideView ride = new RideView(v);
        return ride;
    }

    @Override
    public void onBindViewHolder(RideView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class RideView extends RecyclerView.ViewHolder {

        TextView driver_name, ride_date, price, payment_type, vhcl_details, source, dest;
        ImageView driver_pic, currency, vhcl_pic;

        public RideView(View itemView) {
            super(itemView);

        /*    driver_name = (TextView) itemView.findViewById(R.id.driver_name);
            ride_date = (TextView) itemView.findViewById(R.id.ride_date);
            price = (TextView) itemView.findViewById(R.id.price);
            payment_type = (TextView) itemView.findViewById(R.id.payment_type);
            vhcl_details = (TextView) itemView.findViewById(R.id.vhcl_details);
            source = (TextView) itemView.findViewById(R.id.source);
            dest = (TextView) itemView.findViewById(R.id.dest);

            driver_pic = (ImageView) itemView.findViewById(R.id.driver_pic);
            currency = (ImageView) itemView.findViewById(R.id.currency);
            vhcl_pic = (ImageView) itemView.findViewById(R.id.vhcl_pic);*/


        }
    }
}
