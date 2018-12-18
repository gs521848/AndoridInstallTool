package com.example.administrator.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity  {
    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (copyApkFromAssets(MainActivity.this,"sqss.apk",ToolsUtil.getApkDir()+ "/sqss.apk"))
                {
                    checkIsAndroidO();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startRequestPermission();
            try {
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }

            // 检查该权限是否已经获取

        if (copyApkFromAssets(this,"sqss.apk",ToolsUtil.getApkDir()+ "/sqss.apk"))
        {
            checkIsAndroidO();
        }


    }


    private void showDialogTipUserRequestPermission() {

                 new AlertDialog.Builder(this)
                         .setTitle("存储权限不可用")
                         .setMessage("点击开启权限后点击屏幕继续游戏")
                         .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                                         startRequestPermission();
                                     }
                 })
                         .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                                         finish();
                                     }
                  }).setCancelable(false).show();
    }

    private void startRequestPermission() {
                ActivityCompat.requestPermissions(this, permissions, 321);
             }


    private void checkIsAndroidO() {
        if (Build.VERSION.SDK_INT >= 26&&this.getApplicationInfo().targetSdkVersion>=26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                ToolsUtil.installApk(MainActivity.this, ToolsUtil.getApkDir()+ "/sqss.apk");
                ;//安装应用的逻辑(写自己的就可以)
            } else {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10010);
            }
        } else {
            ToolsUtil.installApk(MainActivity.this, ToolsUtil.getApkDir() + "/sqss.apk");
        }

    }
    public boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            Toast.makeText(MainActivity.this,"发生未知错误"+e,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return copyIsFinish;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10010:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToolsUtil.installApk(MainActivity.this, ToolsUtil.getApkDir()+ "/sqss.apk");
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent, 10012);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10012:
                checkIsAndroidO();
                break;

            default:
                break;
        }
    }
}
