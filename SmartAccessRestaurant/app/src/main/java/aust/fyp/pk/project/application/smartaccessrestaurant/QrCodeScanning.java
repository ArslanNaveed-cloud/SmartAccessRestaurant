package aust.fyp.pk.project.application.smartaccessrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

public class QrCodeScanning extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanning);
        getSupportActionBar().hide();
        Toast.makeText(this, "Place Camera Above Qr Code On Your Table", Toast.LENGTH_LONG).show();
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.getText().trim()==null || result.getText().trim().isEmpty()){}
                        else{

                            String qrcode = result.getText().trim();

                            try {
                                JSONObject details = new JSONObject(qrcode);
                                if(details.getString("hotelname")==null ||details.getString("hotelname").isEmpty()){
                                    Toast.makeText(QrCodeScanning.this, "Requested Qr Code Is Not Correct", Toast.LENGTH_SHORT).show();
                                    mCodeScanner.startPreview();
                                }else{
                                    String username = "user_" + ((int) (Math.random() * 9000) + 1000);
                                    String Hotelname = details.getString("hotelname");
                                    String TableNumber = details.getString("tablenumber");
                                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("hotelname",Hotelname);
                                    editor.putString("tablenumber",TableNumber);
                                    // Toast.makeText(QrCodeScanning.this, "Hotel Name: "+Hotelname+"\nTable Numebr: "+TableNumber, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(QrCodeScanning.this,DashBoard.class);
                                    intent.putExtra("username",username);
                                    intent.putExtra("hotelname",Hotelname);
                                    intent.putExtra("tablenumber",TableNumber);
                                    startActivity(intent);

                                }
                                 } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

}