package com.lihua.test.crashhandler;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private final static int REQUEST_EXTERN_STORAGE =0x01;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verityStoragePermiss(MainActivity.this);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == btn){
                    throw new RuntimeException("自定义异常：这是自己抛出来的异常！！！");
                }
            }
        });
    }

    public static void verityStoragePermiss(AppCompatActivity activity){

        int permission = ActivityCompat.checkSelfPermission(activity,"android.permission.WRITE_EXTERNAL_STORAGE");
        //Log.d("wanlihua","wanlihua debug verityStoragePermiss ");
        if (permission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERN_STORAGE);
            Log.d("wanlihua","wanlihua debug verityStoragePermiss requestPermissions ");
        }
        //Log.d("wanlihua","wanlihua debug verityStoragePermiss sucess ");
    }

}
