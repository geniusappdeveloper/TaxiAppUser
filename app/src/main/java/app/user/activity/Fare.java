package app.user.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.taxiapp.user.R;
import app.user.strip.activity.PaymentActivity;

public class Fare extends AppCompatActivity {
    TextView calculated_fare, base_fare, surge, subcharge, final_pickup, final_drop;
    Button pay;
    RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare);

        final_pickup = (TextView) findViewById(R.id.final_pickup);
        final_drop = (TextView) findViewById(R.id.final_drop);
        calculated_fare = (TextView) findViewById(R.id.calculated_fare);
        base_fare = (TextView) findViewById(R.id.base_fare);
        surge = (TextView) findViewById(R.id.surge);
        subcharge = (TextView) findViewById(R.id.subcharge);
        pay = (Button) findViewById(R.id.pay);
        back = (RelativeLayout) findViewById(R.id.back);


        calculated_fare.setText(getIntent().getStringExtra("fare"));
        base_fare.setText(getIntent().getStringExtra("fare"));
        surge.setText(getIntent().getStringExtra(""));
        subcharge.setText(getIntent().getStringExtra("with_surcharge"));

        final_drop.setText(getIntent().getStringExtra("dest_location"));
        final_pickup.setText(getIntent().getStringExtra("source_location"));

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Fare.this, PaymentActivity.class);
                i.putExtra("fare", calculated_fare.getText().toString());
                startActivity(i);
                finish();
            }
        });


    }
}
