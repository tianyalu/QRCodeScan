package com.example.qrcodescan;

/**
 * Created by shity on 2017/9/6/0006.
 */

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;
//颜色表示：oxAABBGGRR -->AA表示透明度(00,透明;FF,不透明),
/**
 * 生成二维码
 */
public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();
    /**
     * 生成一个二维码图像
     * @param url 传入的字符串，通常是一个URL
     * @param QR_WIDTH 宽度（像素px）
     * @param QR_HEIGHT 高度（像素px）
     * @return
     */
    public static final Bitmap create2DCoderBitmap(String url, int QR_WIDTH, int QR_HEIGHT){
        try{
            //判断URL的合法性
            if(url == null || "".equals(url) || url.length() < 1){
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面按照二维码的算法，逐个生成二维码的图片
            //两个for循环是图像行列扫码的结果
            for(int y = 0; y < QR_HEIGHT; y++){
                for(int x = 0; x < QR_WIDTH; x++){
                    if(bitMatrix.get(x, y)){
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }else{
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }

            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            /**
             * pixels: 接收位图颜色值的数组
             * offset: 写到pixels[]中的第一个像素索引值
             * stride: pixels[]中的行间距个数值（必须大于等于位图宽度），可以为负值
             * x:      从位图中读取的第一个像素的x坐标值
             * y:      从位图中读取的第一个像素的y坐标值
             * width:  从每一行中读取的像素宽度
             * height: 读取的行数
             */
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        }catch (WriterException e){
            Log.i(TAG, "生成二维码错误:" + e.getMessage());
            return null;
        }
    }


    /**
     * 生成一个二维码图像
     * @param url 传入的字符串，通常是一个URL
     * @param widthAndHeight 图像的宽高
     * @return
     */
    public static Bitmap createQRCode(String url, int widthAndHeight){
        try{
            //判断URL的合法性
            if(url == null || "".equals(url) || url.length() < 1){
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE,
                    widthAndHeight, widthAndHeight, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    if(bitMatrix.get(x, y)){
                        pixels[y * width + x] = 0xff000000;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }catch (WriterException e){
            Log.i(TAG, "生成二维码错误：" + e.getMessage());
            return null;
        }
    }

    /**
     * 用给定的内容生成一维条码，注：当所给内容中有中文时会报错，需要修改底层jar包
     * @param content
     * @return
     */
    public static Bitmap createOneDCode(String content){
        try {
            //判断URL的合法性
            if(content == null || "".equals(content) || content.length() < 1){
                return null;
            }
            //检查中文
            for(int i = 0; i < content.length(); i++){
                int c = content.charAt(i);
                if((19968 <= c && c < 40623)){
                    Log.i(TAG, "一维条码信息中不能包含中文");
                    return null;
                }
            }

            //生成一维条码，编码时指定大小，不要生成图片后再进行缩放，导致图像模糊而识别失败
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, 500, 200);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    if(matrix.get(x, y)){
                        pixels[y * width + x] = 0xff000000;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            //通过像素数组生成bitmap
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }catch (WriterException e){
            Log.i(TAG, "生成一维条码错误：" + e.getMessage());
            return null;
        }
    }
}
