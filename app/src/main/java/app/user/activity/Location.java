package app.user.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AutoCompleteTextView;

import app.taxiapp.user.R;
import app.user.adapter.RecentLocationAdapter;

public class Location extends AppCompatActivity {
    RecyclerView recent_searches;
    RecentLocationAdapter loc_adapter;
    AutoCompleteTextView pickup,drop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        recent_searches = (RecyclerView) findViewById(R.id.recent_searches);

        loc_adapter = new RecentLocationAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(Location.this, LinearLayoutManager.VERTICAL, false);
        recent_searches.setLayoutManager(llm);
        recent_searches.setAdapter(loc_adapter);

    }
}
