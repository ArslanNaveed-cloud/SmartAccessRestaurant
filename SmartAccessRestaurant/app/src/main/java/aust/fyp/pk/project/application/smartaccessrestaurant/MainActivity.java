package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.rbddevs.splashy.Splashy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        setSplashy();
        waitforsometime();
    }

    private void waitforsometime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                checkPermissions();

            }
        }, 3000);

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,
            }, 101);
        } else {
            Intent intent = new Intent(MainActivity.this,QrCodeScanning.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (requestCode == 100 && (grantResults[i] == PackageManager.PERMISSION_GRANTED)) {

                Intent intent = new Intent(MainActivity.this,QrCodeScanning.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please Give Permissions To Use Use Smart Access Restaurant App", Toast.LENGTH_LONG).show();
                checkPermissions();
            }
        }

    }
    private void setSplashy() {
        new Splashy(this) 		 // For JAVA : new Splashy(this)
                .setLogo(R.drawable.logo)
                .setTitle("Smart Access Hotel")
                .setTitleColor("#FFFFFF")
                .setClickToHide(true)
                .setSubTitle("A new & hygienic way to order food at your table")
                .setSubTitleColor(R.color.white)
                .setAnimation(Splashy.Animation.SLIDE_IN_LEFT_RIGHT,800)
                .setProgressColor(R.color.white)
                .showProgress(true)
                .setBackgroundResource(R.drawable.background)
                .setFullScreen(true)
                .setTime(3000)
                .show();

    }
}