package com.xiongms.widget.qr_scan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.CameraPreview;

/**
 * 扫描控件，集成相机管理、弱光检测
 * @author xiongms
 * @time 2018-08-21 13:59
 */
public class WeakLightZXingView extends QRCodeView {
    private static final String TAG = WeakLightZXingView.class.getSimpleName();


    public static final Map<DecodeHintType, Object> HINTS = new EnumMap<>(DecodeHintType.class);

    static {
        List<BarcodeFormat> allFormats = new ArrayList<>();
        allFormats.add(BarcodeFormat.CODABAR);
        allFormats.add(BarcodeFormat.CODE_39);
        allFormats.add(BarcodeFormat.CODE_93);
        allFormats.add(BarcodeFormat.CODE_128);
        allFormats.add(BarcodeFormat.DATA_MATRIX);
        allFormats.add(BarcodeFormat.EAN_13);
        allFormats.add(BarcodeFormat.ITF);
        allFormats.add(BarcodeFormat.MAXICODE);
        allFormats.add(BarcodeFormat.QR_CODE);
        allFormats.add(BarcodeFormat.UPC_A);
        allFormats.add(BarcodeFormat.UPC_E);
        allFormats.add(BarcodeFormat.UPC_EAN_EXTENSION);

//        allFormats.add(BarcodeFormat.AZTEC);
//        allFormats.add(BarcodeFormat.EAN_8);
//        allFormats.add(BarcodeFormat.PDF_417);
//        allFormats.add(BarcodeFormat.RSS_14);
//        allFormats.add(BarcodeFormat.RSS_EXPANDED);

        HINTS.put(DecodeHintType.TRY_HARDER, BarcodeFormat.QR_CODE);
        HINTS.put(DecodeHintType.POSSIBLE_FORMATS, allFormats);
        HINTS.put(DecodeHintType.CHARACTER_SET, "utf-8");
    }

    private MultiFormatReader mMultiFormatReader;

    private WeakLightListener mWeakLightListener;

    private boolean isWeakLight = false;

    public WeakLightZXingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WeakLightZXingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMultiFormatReader();

        mPreview.setZOrderOnTop(true);
        mPreview.setZOrderMediaOverlay(true);
    }

    public CameraPreview getPreview() {
        return mPreview;
    }

    private void initMultiFormatReader() {
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(HINTS);
    }

    @Override
    public String processData(byte[] data, int width, int height, boolean isRetry) {
        String result = null;
        Result rawResult = null;

        try {
            PlanarYUVLuminanceSource source = null;
            Rect rect = mScanBoxView.getScanBoxAreaRect(height);
            if (rect != null) {
                source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
            } else {
                source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            }
            rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mMultiFormatReader.reset();
        }


        try {
            analysisColor(data, width, height);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (rawResult != null) {
            result = rawResult.getText();
        }
        return result;
    }

    //分析预览帧中图片的arg 取平均值
    private void analysisColor(byte[] data, int width, int height) {
        int[] rgb = decodeYUV420SP(data, width / 8, height / 8);
        Bitmap bmp = Bitmap.createBitmap(rgb, width / 8, height / 8, Bitmap.Config.ARGB_8888);
        if (bmp != null) {
            //取以中心点宽高10像素的图片来分析
            Bitmap resizeBitmap = Bitmap.createBitmap(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2, 10, 10);
            float color = (float) getAverageColor(resizeBitmap);
            DecimalFormat decimalFormat1 = new DecimalFormat("0.00");
            String percent = decimalFormat1.format(color / -16777216);
            float floatPercent = Float.parseFloat(percent);
            boolean isWeakLight = floatPercent == 1.00;

            if(this.isWeakLight != isWeakLight) {
                this.isWeakLight = isWeakLight;
                if(mWeakLightListener != null) {
                    mWeakLightListener.onWeakLight(this.isWeakLight);
                }
            }

            if (null != resizeBitmap) {
                resizeBitmap.recycle();
            }
            bmp.recycle();
        }
    }

    private int getAverageColor(Bitmap bitmap) {
        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
            }
        }

        int averageColor = Color.rgb(redBucket / pixelCount, greenBucket
                / pixelCount, blueBucket / pixelCount);
        return averageColor;
    }

    private int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        int rgb[] = new int[width * height];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) &
                        0xff00) | ((b >> 10) & 0xff);


            }
        }
        return rgb;
    }

    public void setWeakLightListener(WeakLightListener mWeakLightListener) {
        this.mWeakLightListener = mWeakLightListener;
    }

    public interface WeakLightListener {
        void onWeakLight(boolean isWeakLight);
    }
}
