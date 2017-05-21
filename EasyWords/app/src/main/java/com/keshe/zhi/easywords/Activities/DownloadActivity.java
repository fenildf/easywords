package com.keshe.zhi.easywords.Activities;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DownloadActivity extends AppCompatActivity {
    ImageView down_siji_core;
    ImageView down_liuji;
    ImageView down_kaoyan;
    ImageView down_siji;
    TextView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        edit = (TextView) findViewById(R.id.textView72);

        down_kaoyan = (ImageView) findViewById(R.id.imageView25);
        down_liuji = (ImageView) findViewById(R.id.imageView23);
        down_siji = (ImageView) findViewById(R.id.imageView24);
        down_siji_core = (ImageView) findViewById(R.id.imageView22);

        final TextView tv_1 = (TextView) findViewById(R.id.textView76);
        final TextView tv_2 = (TextView) findViewById(R.id.textView78);
        final TextView tv_3 = (TextView) findViewById(R.id.textView80);
        final TextView tv_4 = (TextView) findViewById(R.id.textView82);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "cet-4-core.txt");
        if (file.exists()) {
            tv_1.setText("已下载");
            down_siji_core.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
            down_siji_core.setEnabled(false);
        }
        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "cet-6.txt");
        if (file2.exists()) {
            tv_2.setText("已下载");
            down_liuji.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
            down_liuji.setEnabled(false);
        }
        File file3 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "cet-4.txt");
        if (file3.exists()) {
            tv_3.setText("已下载");
            down_siji.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
            down_siji.setEnabled(false);
        }
        File file4 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "postgraduate.txt");
        if (file4.exists()) {
            tv_4.setText("已下载");
            down_kaoyan.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
            down_kaoyan.setEnabled(false);
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String textview = data.getString("textview");
                switch (textview) {
                    case "tv_1":
                        tv_1.setText("下载完成");
                        down_siji_core.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        break;
                    case "tv_2":
                        tv_2.setText("下载完成");
                        down_liuji.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        break;
                    case "tv_3":
                        tv_3.setText("下载完成");
                        down_siji.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        break;
                    case "tv_4":
                        tv_4.setText("下载完成");
                        down_kaoyan.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        break;

                }
            }
        };

        down_liuji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_2.setText("下载中");
                String endpoint = "oss-cn-qingdao.aliyuncs.com";
                // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
                OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAISmZ5K4cvAngh", "yMByasHsdluCD5zPcNZotxRpoGH82j");
                OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
                GetObjectRequest get = new GetObjectRequest("easyword", "cet-6.txt");
                final OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
                    @Override
                    public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                        // 请求成功
                        InputStream inputStream = result.getObjectContent();
                        byte[] buffer = new byte[2048];
                        int len;
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), request.getObjectKey());
                        if (file.exists()) {
                            return;
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            while ((len = inputStream.read(buffer)) != -1) {
                                // 处理下载的数据
                                out.write(buffer, 0, len);
                            }
                            out.close();
                            inputStream.close();
                            System.out.println("下载成功");
                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putString("textview", "tv_2");
                            data.putString("imageview", "down_liuji");
                            message.setData(data);
                            handler.sendMessage(message);
                            down_siji_core.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        System.out.println("下载失败");
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
        });

        down_siji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_3.setText("下载中");
                String endpoint = "oss-cn-qingdao.aliyuncs.com";
                // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
                OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAISmZ5K4cvAngh", "yMByasHsdluCD5zPcNZotxRpoGH82j");
                OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
                GetObjectRequest get = new GetObjectRequest("easyword", "cet-4.txt");
                final OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
                    @Override
                    public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                        // 请求成功
                        InputStream inputStream = result.getObjectContent();
                        byte[] buffer = new byte[2048];
                        int len;
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), request.getObjectKey());
                        if (file.exists()) {
                            return;
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            while ((len = inputStream.read(buffer)) != -1) {
                                // 处理下载的数据
                                out.write(buffer, 0, len);
                            }
                            out.close();
                            inputStream.close();
                            System.out.println("下载成功");
                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putString("textview", "tv_3");
                            data.putString("imageview", "down_siji");
                            message.setData(data);
                            handler.sendMessage(message);
                            down_siji_core.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        System.out.println("下载失败");
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
        });

        down_kaoyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_4.setText("下载中");
                String endpoint = "oss-cn-qingdao.aliyuncs.com";
                // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
                OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAISmZ5K4cvAngh", "yMByasHsdluCD5zPcNZotxRpoGH82j");
                OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
                GetObjectRequest get = new GetObjectRequest("easyword", "postgraduate.txt");
                final OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
                    @Override
                    public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                        // 请求成功
                        InputStream inputStream = result.getObjectContent();
                        byte[] buffer = new byte[2048];
                        int len;
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), request.getObjectKey());
                        if (file.exists()) {
                            return;
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            while ((len = inputStream.read(buffer)) != -1) {
                                // 处理下载的数据
                                out.write(buffer, 0, len);
                            }
                            out.close();
                            inputStream.close();
                            System.out.println("下载成功");
                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putString("textview", "tv_4");
                            data.putString("imageview", "down_kaoyan");
                            message.setData(data);
                            handler.sendMessage(message);
                            down_siji_core.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        System.out.println("下载失败");
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
        });

        down_siji_core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_1.setText("下载中");
                String endpoint = "oss-cn-qingdao.aliyuncs.com";
                // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
                OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAISmZ5K4cvAngh", "yMByasHsdluCD5zPcNZotxRpoGH82j");
                OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
                GetObjectRequest get = new GetObjectRequest("easyword", "cet-4-core.txt");
                final OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
                    @Override
                    public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                        // 请求成功
                        InputStream inputStream = result.getObjectContent();
                        byte[] buffer = new byte[2048];
                        int len;
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), request.getObjectKey());
                        if (file.exists()) {
                            return;
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            while ((len = inputStream.read(buffer)) != -1) {
                                // 处理下载的数据
                                out.write(buffer, 0, len);
                            }
                            out.close();
                            inputStream.close();
                            System.out.println("下载成功");
                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putString("textview", "tv_1");
                            data.putString("imageview", "down_siji_core");
                            message.setData(data);
                            handler.sendMessage(message);
                            down_siji_core.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        System.out.println("下载失败");
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
        });
    }
}
