package com.example.gugu.download;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class  MainActivity extends AppCompatActivity {
    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder=(DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    private void initView() {
        TextView startDownload = (TextView) findViewById(R.id.startDownload);
        TextView cancelDownload = (TextView) findViewById(R.id.cancelDownload);
        TextView pauseDownload = (TextView) findViewById(R.id.pauseDownload);

        Intent intent = new Intent(this,DownloadService.class);
        startService(intent);
         bindService(intent,connection,BIND_AUTO_CREATE);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        startDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://gdown.baidu.com/data/wisegame/f73fc503fad6a503/weixin_1180.apk";
                if(downloadBinder==null){
                    return;
                }
                downloadBinder.startDownload(url);
            }
        });
        cancelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downloadBinder==null){
                    return;
                }
             downloadBinder.cancelDownload();
            }
        });
        pauseDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downloadBinder==null){
                    return;
                }
             downloadBinder.pauseDownload();
            }
        });
    }
    /**
     * 用户选择允许或拒绝后,会回调onRequestPermissionsResult
     * @param requestCode  请求码
     * @param permissions
     * @param grantResults  授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
