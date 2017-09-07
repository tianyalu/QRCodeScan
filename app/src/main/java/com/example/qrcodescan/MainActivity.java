package com.example.qrcodescan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SCANNIN_GREQUEST_CODE = 1;
    private static final String STR_URL = "170905135645042180";
    private static final String URL = "http://www.baidu.com";

    /**
     * 显示扫描结果
     */
    private TextView mTextView ;
    /**
     * 显示扫描拍的图片
     */
    private ImageView mImageView;

    private Button mButton;
    private Button btn2;
    private Button btn3;
    private Button btn4;

    private Handler mHandler;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Log.i(TAG, "Thread id1:-->" + Thread.currentThread().getId());
        initViews();
        setListeners();
    }

    private void initViews(){
        mHandler = new Handler();

        mTextView = (TextView) findViewById(R.id.result);
        mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);

        mButton = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
    }

    private void setListeners(){
        //点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
        //扫描完了之后调到该界面
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });

        //显示二维码一种方式
        btn2.setOnClickListener(new OnClickListener() { //在子线程中改变UI
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showQRCode2();
                    }
                }).start();
            }
        });

        //显示二维码另一种方式
        btn3.setOnClickListener(new OnClickListener() { //在主线程中改变UI
            @Override
            public void onClick(View view) {
                showQRCode3();
            }
        });

        //显示一维条码
        btn4.setOnClickListener(new OnClickListener() { //在主线程中改变UI
            @Override
            public void onClick(View view) {
                showOneDCode();
            }
        });
    }

    //显示二维码一种方式
    private void showQRCode2(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(STR_URL);
                mImageView.setImageBitmap(BitmapUtil.create2DCoderBitmap(STR_URL, 400, 400));
                Log.i(TAG, "Thread id 3:-->" + Thread.currentThread().getId());
            }
        });
    }

    //显示二维码另一种方式
    private void showQRCode3(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(URL);
                mImageView.setImageBitmap(BitmapUtil.createQRCode(URL, 400));
            }
        });
    }

    //显示一维条码(条码信息中不能包含中文,有在底层检查)
    private void showOneDCode(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(STR_URL);
                mImageView.setImageBitmap(BitmapUtil.createOneDCode(STR_URL));
            }
        });
    }

    //扫描条码/二维码得到的结果处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    mTextView.setText(bundle.getString("result"));
                    //显示
                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }

}