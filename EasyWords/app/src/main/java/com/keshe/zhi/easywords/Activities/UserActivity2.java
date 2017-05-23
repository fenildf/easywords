package com.keshe.zhi.easywords.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.keshe.zhi.easywords.fragments.FindFragment;
import com.keshe.zhi.easywords.fragments.HomePage;
import com.keshe.zhi.easywords.fragments.ProfileFragment;
import com.keshe.zhi.easywords.fragments.RegByMail;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

public class UserActivity2 extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener, FindFragment.OnFragmentInteractionListener {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, new HomePage(), "homepage");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, new FindFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, new ProfileFragment());
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
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, new HomePage(), "homepage");
        fragmentTransaction.commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
//        WilddogApp.initializeApp(this, options);
//        SyncReference ref = WilddogSync.getInstance().getReference();

//        System.out.println(System.currentTimeMillis() / 1000);
//        System.out.println(System.currentTimeMillis() / 1000 + 30 * 24 * 60 * 60);


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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void upFile(String filename){
        String endpoint = "oss-cn-qingdao.aliyuncs.com";

// 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAISmZ5K4cvAngh", "yMByasHsdluCD5zPcNZotxRpoGH82j");

        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);


        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest("easyword", filename, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/"+filename);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                System.out.println("上传成功");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

//    public void upData() {
//        String appid = "1253765734";
//        Context context = getApplicationContext();
//        String peristenceId = "持久化Id";
//        //创建COSClientConfig对象，根据需要修改默认的配置参数
//        COSClientConfig config = new COSClientConfig();
//        //如设置园区
//        config.setEndPoint(COSEndPoint.COS_TJ);
//        COSClient cos = new COSClient(context, appid, config, null);
//        String bucket = "easyword";
//        String cosPath = "/test.jpg";
//        String srcPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/test.jpg";
//        String sign = "M2I3YjViZDA0ZTk0ZTYyOGU3MzEzYmEwNGM4MjMzZWNhY2JkM2VkMWE9MTI1Mzc2NTczNCZiPWVhc3l3b3JkJms9QUtJRFdxeHJYMDhINWYxOTJodlBTSHJETEZ1TVhSNFI3UzNPJmU9MTQ5Nzg4MzIzMCZ0PTE0OTUyOTEyMzAmcj0xMjM0NTY3OCZmPSUyZjEyNTM3NjU3MzQlMmZlYXN5d29yZCUyZnRlc3QuanBn";
//        PutObjectRequest putObjectRequest = new PutObjectRequest();
//        putObjectRequest.setBucket(bucket);
//        putObjectRequest.setCosPath(cosPath);
//        HashMap<String, String> map = new HashMap<>();
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

//    public String hmacSha1(String base, String key){
//
//        String type = "HmacSHA1";
//
//        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
//
//        Mac mac = null;
//        try {
//            mac = Mac.getInstance(type);
//            mac.init(secret);
//        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
//            e.printStackTrace();
//        }
//
//
//        byte[] digest = mac.doFinal(base.getBytes());
//        return Base64.encodeToString(digest, Base64.DEFAULT);
//    }
}
