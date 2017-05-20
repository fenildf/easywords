package com.keshe.zhi.easywords.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.keshe.zhi.easywords.fragments.FindFragment;
import com.keshe.zhi.easywords.fragments.HomePage;
import com.keshe.zhi.easywords.fragments.ProfileFragment;
import com.keshe.zhi.easywords.fragments.RegByMail;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import org.w3c.dom.Comment;

public class UserActivity2 extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener,FindFragment.OnFragmentInteractionListener {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HomePage home_page;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    System.out.println(home_page);
                    fragmentTransaction.replace(R.id.content, home_page, "homepage");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content,new FindFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content,new ProfileFragment());
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);
        home_page = new HomePage();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, home_page, "homepage");
        fragmentTransaction.commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        SyncReference ref = WilddogSync.getInstance().getReference();

//        upData();
//        HashMap<String,String> anew = new HashMap<>();
//        anew.put("user","libai");
//        anew.put("data","some");
//        String key = ref.child("users").push().getKey();//获取新用户的节点名
//        HashMap<String,String> user_key = new HashMap<>();
//        user_key.put(anew.get("user"),key);
//        ref.child("user_key").setValue(user_key);//将用户名-节点名存到user_key节点中
//
//        ref.child("users").child(key).setValue(anew);
    }

    public void danciben_clk(View view) {
        System.out.println("单词本");
    }

    public void begin_study_clk(View view) {
        System.out.println("开始学习");
        Intent intent = new Intent(this, ReciteWordActivity.class);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

//    public void upData() {
//        String appid = "1253765734";
//        Context context = getApplicationContext();
//        String peristenceId = "持久化Id";
//
//        //创建COSClientConfig对象，根据需要修改默认的配置参数
//        COSClientConfig config = new COSClientConfig();
//        //如设置园区
//        config.setEndPoint(COSEndPoint.COS_TJ);
//
//        COSClient cos = new COSClient(context, appid, config, null);
//
//        String bucket = "filestore";
//        String cosPath = "/";
//        String srcPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()+"/test.jpg";
//        String sign ="ZTI1YmFmYjBmNzM1M2Q2OGE2MmMxNjkxNjhiZWJmZDVlMWVhOTExMmE9MTI1Mzc2NTczNCZiPWZpbGVzdG9yZSZrPUFLSURXcXhyWDA4SDVmMTkyaHZQU0hyRExGdU1YUjRSN1MzTyZlPTE0OTc3ODgwODMmdD0xNDk1MTk2MDIwJnI9MTIzNDU2NzgmZj0lMmYxMjUzNzY1NzM0JTJmZmlsZXN0b3JlJTJm";
//
//        PutObjectRequest putObjectRequest = new PutObjectRequest();
//        putObjectRequest.setBucket(bucket);
//        putObjectRequest.setCosPath(cosPath);
//        HashMap<String,String> map = new HashMap<>();
//        map.put("op","upload");
//        putObjectRequest.setHeaders(map);
//        putObjectRequest.setSrcPath(srcPath);
//        putObjectRequest.setSign(sign);
//        putObjectRequest.setListener(new IUploadTaskListener() {
//            @Override
//            public void onCancel(COSRequest cosRequest, COSResult cosResult) {
//
//            }
//
//            @Override
//            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
//
//                PutObjectResult result = (PutObjectResult) cosResult;
//                if (result != null) {
//                    StringBuilder stringBuilder = new StringBuilder();
//                    stringBuilder.append(" 上传结果： ret=" + result.code + "; msg =" + result.msg + "\n");
//                    stringBuilder.append(" access_url= " + result.access_url == null ? "null" : result.access_url + "\n");
//                    stringBuilder.append(" resource_path= " + result.resource_path == null ? "null" : result.resource_path + "\n");
//                    stringBuilder.append(" url= " + result.url == null ? "null" : result.url);
//                    Log.w("TEST", stringBuilder.toString());
//                }
//            }
//
//            @Override
//            public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
//                Log.w("TEST", "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg);
//            }
//
//            @Override
//            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
//                float progress = (float) currentSize / totalSize;
//                progress = progress * 100;
//                Log.w("TEST", "进度：  " + (int) progress + "%");
//            }
//        });
//        PutObjectResult result = cos.putObject(putObjectRequest);
//    }
//
//    public void downloadData() {
//        COSClientConfig config = new COSClientConfig();
//        config.setEndPoint(COSEndPoint.COS_TJ);
//        Context context = getApplicationContext();
//        String appid = "1046051";
//
//        COSClient client = new COSClient(context, appid, config, null);
//
//        String downloadURl = "http://filestore-1253765734.costj.myqcloud.com/1112845.jpg?sign=MAE+f7jb5DNFTS6hw0s8r4EZCl1hPTEyNTM3NjU3MzQmaz1BS0lEV3F4clgwOEg1ZjE5Mmh2UFNIckRMRnVNWFI0UjdTM08mZT0xNDk3NzgyMjY2JnQ9MTQ5NTE5MDI2NiZyPTk2NjAzMjk1NyZmPS8xMTEyODQ1LmpwZyZiPWZpbGVzdG9yZQ==";
//        String savePath = getFilesDir().getPath();
//        String sign = "开启token防盗链了，则需要签名；否则，不需要";
//
//        GetObjectRequest getObjectRequest = new GetObjectRequest(downloadURl, savePath);
//        getObjectRequest.setLocalFileName("test.jpg");
//        getObjectRequest.setSign(null);
//        getObjectRequest.setListener(new IDownloadTaskListener() {
//
//            @Override
//            public void onCancel(COSRequest cosRequest, COSResult cosResult) {
//
//            }
//
//            @Override
//            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
//                float progress = currentSize / (float) totalSize;
//                progress = progress * 100;
//                Log.w("TEST", "progress =" + (int) (progress) + "%");
//            }
//
//            @Override
//            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
//                Log.w("TEST", "code =" + cosResult.code + "; msg =" + cosResult.msg);
//            }
//
//            @Override
//            public void onFailed(COSRequest COSRequest, COSResult cosResult) {
//                Log.w("TEST", "code =" + cosResult.code + "; msg =" + cosResult.msg);
//            }
//        });
//
////        GetObjectResult getObjectResult = client.getObject(getObjectRequest);
//    }

}
