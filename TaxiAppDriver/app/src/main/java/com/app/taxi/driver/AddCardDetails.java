package com.app.taxi.driver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddCardDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_details);
    }

    public void cardSwitch(View view) {
        switch (view.getId()){
            case R.id.cardscreen_back_layout:
                Intent navIntent = new Intent(AddCardDetails.this, ProfileScreen.class);
                startActivity(navIntent);
                finish();
                break;
        }
    }
}
