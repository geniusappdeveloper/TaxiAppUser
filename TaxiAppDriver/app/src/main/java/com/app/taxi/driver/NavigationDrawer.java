package com.app.taxi.driver;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationDrawer extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener
 {
private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Button acceptbtn;
    Intent acceptscreen, profileScreen;
    LinearLayout nav_drawer, afteraccept_layout;
    CircleImageView headerImage;

    NavigationView navigationView;
    View headerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
 mDrawerLayout = (DrawerLayout) findViewById(R.id.acceptdrawer_layout);
        afteraccept_layout = (LinearLayout) findViewById(R.id.afteraccept_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        headerview = navigationView.getHeaderView(0);
        final ImageView headerImage = (ImageView) headerview.findViewById(R.id.profilescreen_circleView);
        acceptbtn = (Button) findViewById(R.id.acceptbtn_homescreen);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        nav_drawer = (LinearLayout) findViewById(R.id.homescreen_navigation_layout);
        nav_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

navigationView.setNavigationItemSelectedListener(this);
        headerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileScreen = new Intent(NavigationDrawer.this, ProfileScreen.class);
                startActivity(profileScreen);

            }
        });
        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

acceptscreen =new Intent(NavigationDrawer.this , AfterAccepting.class);
                startActivity(acceptscreen);
                acceptbtn.setVisibility(View.GONE);
                afteraccept_layout.setVisibility(View.VISIBLE);


            }
        });


    }

  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


  @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.payments) {

Toast.makeText(this, "payment", Toast.LENGTH_LONG).show();
        } else if (id == R.id.ridehistory) {

Toast.makeText(this, "ride history", Toast.LENGTH_LONG).show();
        } else if (id == R.id.about) {
            ;
        } else if (id == R.id.logout) {

Toast.makeText(this, "log out", Toast.LENGTH_LONG).show();
        }
        return false;
    }

}
