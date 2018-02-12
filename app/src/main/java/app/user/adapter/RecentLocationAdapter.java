package app.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.taxiapp.user.R;

/**
 * Created by user1 on 12/28/2017.
 */

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.Recent> {
    LayoutInflater layoutInflater;

    public RecentLocationAdapter() {


    }


    @Override
    public Recent onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.from(parent.getContext()).inflate(R.layout.recent, null);
        Recent r = new Recent(v);

        return r;
    }

    @Override
    public void onBindViewHolder(Recent holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class Recent extends RecyclerView.ViewHolder {
        RelativeLayout rl_loc;
        ImageView icon;
        TextView loc, loc_add;

        public Recent(View itemView) {
            super(itemView);

            rl_loc = (RelativeLayout) itemView.findViewById(R.id.rl_loc);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            loc = (TextView) itemView.findViewById(R.id.loc);
            loc_add = (TextView) itemView.findViewById(R.id.loc_add);
        }
    }
}
