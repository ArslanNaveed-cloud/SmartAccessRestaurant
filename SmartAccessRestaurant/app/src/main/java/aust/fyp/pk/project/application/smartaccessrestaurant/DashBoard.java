package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private int count = 0;
    private boolean isuserloggedin;
    private String hotelname,tablenumber;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        hotelname = intent.getStringExtra("hotelname");
        tablenumber = intent.getStringExtra("tablenumber");

        if(intent.getStringExtra("identifier") !=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MyOrders()).commit();
        }
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hotelname",hotelname);
        editor.putString("tablenumber",tablenumber);
        if(!isuserloggedin){
            username = intent.getStringExtra("username");
            editor.putString("username",username);

        }
        editor.apply();


        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("hotelname", hotelname );
            bundle.putString("tablenumber",tablenumber);
            HomeFragment guideFragment = new HomeFragment();
            guideFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container,guideFragment);
            transaction.commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(count == 0){
                Toast.makeText(this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
                count++;
            }else{
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("hotelname", hotelname );
                bundle.putString("tablenumber",tablenumber);
                HomeFragment guideFragment = new HomeFragment();
                guideFragment.setArguments(bundle);
                transaction.replace(R.id.fragment_container,guideFragment);
                transaction.commit();
                break;
            case R.id.nav_order:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyOrders()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Profile()).commit();
                break;

            case R.id.nav_logout:
                if(isuserloggedin){
                    buildDialog(DashBoard.this,"Are Your Sure","You will be logged out of your account").show();
                }else{
                    Toast.makeText(this, "Create Your Account First", Toast.LENGTH_SHORT).show();
                }
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isuserloggedin",false);
                editor.putString("username",null);
                editor.apply();

                finish();


            }
        });

        return builder;
    }

}