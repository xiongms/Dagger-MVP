/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiongms.libcore.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;


// TODO: Auto-generated Javadoc

/**
 * © 2012 amsoft.cn
 * 名称：ImageUtil.java
 * 描述：图片处理类.
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-01-17 下午11:52:13
 */
public class ImageUtil {

    private final static String TAG = "ImageUtil";

    /**
     * 图片处理：裁剪.
     */
    public static final int CUTIMG = 0;

    /**
     * 图片处理：缩放.
     */
    public static final int SCALEIMG = 1;

    /**
     * 图片处理：不处理.
     */
    public static final int ORIGINALIMG = 2;

    /**
     * 图片最大宽度.
     */
    public static final int MAX_WIDTH = 4096 / 2;

    /**
     * 图片最大高度.
     */
    public static final int MAX_HEIGHT = 4096 / 2;

    /**
     * 从互联网上获取原始大小图片.
     *
     * @param url           要下载文件的网络地址
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        URLConnection con = null;
        InputStream is = null;
        try {
            URL imageURL = new URL(url);
            con = imageURL.openConnection();
            con.setDoInput(true);
            con.connect();
            is = con.getInputStream();
            // 获取资源图片
            bitmap = BitmapFactory.decodeStream(is, null, null);
        } catch (Exception e) {
            Log.d(ImageUtil.class.getSimpleName(), "" + e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 描述：获取原图.
     *
     * @param file File对象
     * @return Bitmap 图片
     */
    public static Bitmap getBitmap(File file) {
        Bitmap resizeBmp = null;
        try {
            resizeBmp = BitmapFactory.decodeFile(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resizeBmp;
    }

    /**
     * 从互联网上获取指定大小的图片.
     *
     * @param url           要下载文件的网络地址
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmap(String url, int desiredWidth, int desiredHeight) {
        Bitmap bitmap = null;
        URLConnection con = null;
        InputStream is = null;
        try {
            URL imageURL = new URL(url);
            con = imageURL.openConnection();
            con.setDoInput(true);
            con.connect();
            is = con.getInputStream();
            bitmap = getBitmap(is, desiredWidth, desiredHeight);
            //超出的裁掉
            if (bitmap.getWidth() > desiredWidth || bitmap.getHeight() > desiredHeight) {
                bitmap = getCutBitmap(bitmap, desiredWidth, desiredHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(ImageUtil.class.getSimpleName(), "" + e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 从流中获取指定大小的图片.
     *
     * @param inputStream
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap getBitmap(InputStream inputStream, int desiredWidth, int desiredHeight) {
        Bitmap bitmap = null;
        try {
            byte[] data = StreamUtil.stream2bytes(inputStream);
            bitmap = getBitmap(data, desiredWidth, desiredHeight);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(ImageUtil.class.getSimpleName(), "" + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmap(byte[] data) {
        return getBitmap(data, 0, 0);
    }

    /**
     * 从流中获取指定大小的图片.
     *
     * @param data
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public static Bitmap getBitmap(byte[] data, int desiredWidth, int desiredHeight) {
        Bitmap resizeBmp = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            // 获取图片的原始宽度高度
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
            desiredWidth = size[0];
            desiredHeight = size[1];

            // 默认为ARGB_8888.
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            // 以下两个字段需一起使用：
            // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
            opts.inPurgeable = true;
            // 位图可以共享一个参考输入数据(inputstream、阵列等)
            opts.inInputShareable = true;
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            int sampleSize = findBestSampleSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
            opts.inSampleSize = sampleSize;

            // 创建内存
            opts.inJustDecodeBounds = false;
            // 使图片不抖动
            opts.inDither = false;
            resizeBmp = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

            if (resizeBmp != null) {
                resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(ImageUtil.class.getSimpleName(), "" + e.getMessage());
        }
        return resizeBmp;
    }


    /**
     * 描述：缩放图片.
     *
     * @param file          File对象
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getScaleBitmap(File file, int desiredWidth, int desiredHeight) {

        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), opts);

        // 获取图片的原始宽度高度
        int srcWidth = opts.outWidth;
        int srcHeight = opts.outHeight;

        //需要的尺寸重置
        int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        desiredWidth = size[0];
        desiredHeight = size[1];

        // 默认为ARGB_8888.
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        // 以下两个字段需一起使用：
        // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
        opts.inPurgeable = true;
        // 位图可以共享一个参考输入数据(inputstream、阵列等)
        opts.inInputShareable = true;
        // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
        int sampleSize = findBestSampleSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        opts.inSampleSize = sampleSize;
        // 创建内存
        opts.inJustDecodeBounds = false;
        // 使图片不抖动
        opts.inDither = false;

        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);

        // 缩放的比例
        float scale = getMinScale(resizeBmp.getWidth(), resizeBmp.getHeight(), desiredWidth, desiredHeight);
        if (scale < 1) {
            // 缩小
            resizeBmp = scaleBitmap(resizeBmp, scale);
        }

        //超出的裁掉
        if (resizeBmp.getWidth() > desiredWidth || resizeBmp.getHeight() > desiredHeight) {
            resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
        }
        return resizeBmp;
    }

    /**
     * 描述：缩放图片.
     *
     * @param file          File对象
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getScaleBitmap(File file, int desiredWidth, int desiredHeight, boolean isScaling) {

        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), opts);

        // 获取图片的原始宽度高度
        int srcWidth = opts.outWidth;
        int srcHeight = opts.outHeight;

        //需要的尺寸重置
        int[] size;
        if (isScaling) {
            size = resizeToScalingSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        } else {
            size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        }
        desiredWidth = size[0];
        desiredHeight = size[1];

        // 默认为ARGB_8888.
        opts.inPreferredConfig = Config.RGB_565;
        // 以下两个字段需一起使用：
        // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
        opts.inPurgeable = true;
        // 位图可以共享一个参考输入数据(inputstream、阵列等)
        opts.inInputShareable = true;
        // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
        int sampleSize = findBestSampleSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        opts.inSampleSize = sampleSize;
        // 创建内存
        opts.inJustDecodeBounds = false;
        // 使图片不抖动
        opts.inDither = false;

        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);

        // 缩放的比例
        float scale = getMinScale(resizeBmp.getWidth(), resizeBmp.getHeight(), desiredWidth, desiredHeight);
        if (scale < 1) {
            // 缩小
            resizeBmp = scaleBitmap(resizeBmp, scale);
        }

        //超出的裁掉
        if (resizeBmp.getWidth() > desiredWidth || resizeBmp.getHeight() > desiredHeight) {
            resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
        }
        return resizeBmp;
    }


    private static int[] resizeToScalingSize(int srcWidth, int srcHeight,
                                             int desiredWidth, int desiredHeight) {
        int[] size = new int[2];
        if (desiredWidth <= 0) {
            desiredWidth = srcWidth;
        }
        if (desiredHeight <= 0) {
            desiredHeight = srcHeight;
        }
        if (desiredWidth > MAX_WIDTH) {
            desiredWidth = MAX_WIDTH;
        }

        if (desiredHeight > MAX_HEIGHT) {
            desiredHeight = MAX_HEIGHT;
        }


        if (srcWidth > desiredWidth) {
            // 重新计算大小
            float scaleWidth = (float) desiredWidth / srcWidth;
            desiredHeight = (int) (srcHeight * scaleWidth);
        }

        if (srcHeight > desiredHeight) {
            // 重新计算大小
            float scaleHeight = (float) desiredHeight / srcHeight;
            desiredWidth = (int) (srcWidth * scaleHeight);
        }

        size[0] = desiredWidth;
        size[1] = desiredHeight;
        return size;
    }

    /**
     * 描述：缩放图片.
     *
     * @param bitmap        the bitmap
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getScaleBitmap(Bitmap bitmap, int desiredWidth, int desiredHeight) {

        if (!checkBitmap(bitmap)) {
            return null;
        }
        Bitmap resizeBmp = null;

        // 获得图片的宽高
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();

        int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        desiredWidth = size[0];
        desiredHeight = size[1];

        float scale = getMinScale(srcWidth, srcHeight, desiredWidth, desiredHeight);
        resizeBmp = scaleBitmap(bitmap, scale);
        //超出的裁掉
        if (resizeBmp.getWidth() > desiredWidth || resizeBmp.getHeight() > desiredHeight) {
            resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
        }
        return resizeBmp;
    }


    /**
     * 描述：裁剪图片.
     *
     * @param file          File对象
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getCutBitmap(File file, int desiredWidth, int desiredHeight) {

        Bitmap resizeBmp = null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), opts);

        // 获取图片的原始宽度
        int srcWidth = opts.outWidth;
        // 获取图片原始高度
        int srcHeight = opts.outHeight;

        int[] size = resizeToMaxSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        desiredWidth = size[0];
        desiredHeight = size[1];

        // 默认为ARGB_8888.
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        // 以下两个字段需一起使用：
        // 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问，如果Bitmap已经decode，那么将被自动重新解码
        opts.inPurgeable = true;
        // 位图可以共享一个参考输入数据(inputstream、阵列等)
        opts.inInputShareable = true;
        // 缩放的比例，缩放是很难按准备的比例进行缩放的，通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
        int sampleSize = findBestSampleSize(srcWidth, srcHeight, desiredWidth, desiredHeight);
        opts.inSampleSize = sampleSize;
        // 创建内存
        opts.inJustDecodeBounds = false;
        // 使图片不抖动
        opts.inDither = false;
        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);

        if (resizeBmp != null) {
            resizeBmp = getCutBitmap(resizeBmp, desiredWidth, desiredHeight);
        }
        return resizeBmp;
    }

    /**
     * 描述：裁剪图片.
     *
     * @param bitmap        the bitmap
     * @param desiredWidth  新图片的宽
     * @param desiredHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getCutBitmap(Bitmap bitmap, int desiredWidth, int desiredHeight) {

        if (!checkBitmap(bitmap)) {
            return null;
        }

        if (!checkSize(desiredWidth, desiredHeight)) {
            return null;
        }

        Bitmap resizeBmp = null;

        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int offsetX = 0;
            int offsetY = 0;

            if (width > desiredWidth) {
                offsetX = (width - desiredWidth) / 2;
            } else {
                desiredWidth = width;
            }

            if (height > desiredHeight) {
                offsetY = (height - desiredHeight) / 2;
            } else {
                desiredHeight = height;
            }

            resizeBmp = Bitmap.createBitmap(bitmap, offsetX, offsetY, desiredWidth, desiredHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resizeBmp != bitmap) {
                bitmap.recycle();
            }
        }
        return resizeBmp;
    }

    /**
     * 描述：根据等比例缩放图片.
     *
     * @param bitmap the bitmap
     * @param scale  比例
     * @return Bitmap 新图片
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {

        if (!checkBitmap(bitmap)) {
            return null;
        }

        if (scale == 1) {
            return bitmap;
        }

        Bitmap resizeBmp = null;
        try {
            // 获取Bitmap资源的宽和高
            int bmpW = bitmap.getWidth();
            int bmpH = bitmap.getHeight();

            // 注意这个Matirx是android.graphics底下的那个
            Matrix matrix = new Matrix();
            // 设置缩放系数，分别为原来的0.8和0.8
            matrix.postScale(scale, scale);
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resizeBmp != bitmap) {
                bitmap.recycle();
            }
        }
        return resizeBmp;
    }

    /**
     * 描述：获取图片尺寸
     *
     * @param file File对象
     * @return Bitmap 新图片
     */
    public static float[] getBitmapSize(File file) {
        float[] size = new float[2];
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), opts);
        // 获取图片的原始宽度高度
        size[0] = opts.outWidth;
        size[1] = opts.outHeight;
        return size;
    }

    /**
     * 获取缩小的比例.
     *
     * @param srcWidth
     * @param srcHeight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private static float getMinScale(int srcWidth, int srcHeight, int desiredWidth,
                                     int desiredHeight) {
        // 缩放的比例
        float scale = 0;
        // 计算缩放比例，宽高的最小比例
        float scaleWidth = (float) desiredWidth / srcWidth;
        float scaleHeight = (float) desiredHeight / srcHeight;
        if (scaleWidth > scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }

        return scale;
    }

    private static int[] resizeToMaxSize(int srcWidth, int srcHeight,
                                         int desiredWidth, int desiredHeight) {
        int[] size = new int[2];
        if (desiredWidth <= 0) {
            desiredWidth = srcWidth;
        }
        if (desiredHeight <= 0) {
            desiredHeight = srcHeight;
        }
        if (desiredWidth > MAX_WIDTH) {
            // 重新计算大小
            desiredWidth = MAX_WIDTH;
            float scaleWidth = (float) desiredWidth / srcWidth;
            desiredHeight = (int) (desiredHeight * scaleWidth);
        }

        if (desiredHeight > MAX_HEIGHT) {
            // 重新计算大小
            desiredHeight = MAX_HEIGHT;
            float scaleHeight = (float) desiredHeight / srcHeight;
            desiredWidth = (int) (desiredWidth * scaleHeight);
        }
        size[0] = desiredWidth;
        size[1] = desiredHeight;
        return size;
    }

    private static boolean checkBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(ImageUtil.class.getSimpleName(), "原图Bitmap为空了");
            return false;
        }

        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            Log.e(ImageUtil.class.getSimpleName(), "原图Bitmap大小为0");
            return false;
        }
        return true;
    }

    private static boolean checkSize(int desiredWidth, int desiredHeight) {
        if (desiredWidth <= 0 || desiredHeight <= 0) {
            Log.e(ImageUtil.class.getSimpleName(), "请求Bitmap的宽高参数必须大于0");
            return false;
        }
        return true;
    }

    /**
     * Drawable转Bitmap.
     *
     * @param drawable 要转化的Drawable
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap对象转换Drawable对象.
     *
     * @param bitmap 要转化的Bitmap对象
     * @return Drawable 转化完成的Drawable对象
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        BitmapDrawable mBitmapDrawable = null;
        try {
            if (bitmap == null) {
                return null;
            }
            mBitmapDrawable = new BitmapDrawable(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    /**
     * Bitmap对象转换TransitionDrawable对象.
     *
     * @param bitmap 要转化的Bitmap对象 imageView.setImageDrawable(td);
     *               td.startTransition(200);
     * @return Drawable 转化完成的Drawable对象
     */
    public static TransitionDrawable bitmapToTransitionDrawable(Bitmap bitmap) {
        TransitionDrawable mBitmapDrawable = null;
        try {
            if (bitmap == null) {
                return null;
            }
            mBitmapDrawable = new TransitionDrawable(new Drawable[]{
                    new ColorDrawable(Color.TRANSPARENT),
                    new BitmapDrawable(bitmap)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    /**
     * Drawable对象转换TransitionDrawable对象.
     *
     * @param drawable 要转化的Drawable对象 imageView.setImageDrawable(td);
     *                 td.startTransition(200);
     * @return Drawable 转化完成的Drawable对象
     */
    public static TransitionDrawable drawableToTransitionDrawable(
            Drawable drawable) {
        TransitionDrawable mBitmapDrawable = null;
        try {
            if (drawable == null) {
                return null;
            }
            mBitmapDrawable = new TransitionDrawable(new Drawable[]{
                    new ColorDrawable(Color.TRANSPARENT), drawable});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    /**
     * 将Bitmap转换为byte[].
     *
     * @param bitmap          the bitmap
     * @param mCompressFormat 图片格式 Bitmap.CompressFormat.JPEG,CompressFormat.PNG
     * @param needRecycle     是否需要回收
     * @return byte[] 图片的byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap,
                                      Bitmap.CompressFormat mCompressFormat, final boolean needRecycle) {
        byte[] result = null;
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();
            bitmap.compress(mCompressFormat, 100, output);
            result = output.toByteArray();
            if (needRecycle) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 获取Bitmap大小.
     *
     * @param bitmap          the bitmap
     * @param mCompressFormat 图片格式 Bitmap.CompressFormat.JPEG,CompressFormat.PNG
     * @return 图片的大小
     */
    public static int getByteCount(Bitmap bitmap,
                                   Bitmap.CompressFormat mCompressFormat) {
        int size = 0;
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();
            bitmap.compress(mCompressFormat, 100, output);
            byte[] result = output.toByteArray();
            size = result.length;
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    /**
     * 描述：将byte[]转换为Bitmap.
     *
     * @param b 图片格式的byte[]数组
     * @return bitmap 得到的Bitmap
     */
    public static Bitmap bytes2Bimap(byte[] b) {
        Bitmap bitmap = null;
        try {
            if (b.length != 0) {
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将ImageView转换为Bitmap.
     *
     * @param view 要转换为bitmap的View
     * @return byte[] 图片的byte[]
     */
    public static Bitmap imageView2Bitmap(ImageView view) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将View转换为Drawable.需要最上层布局为Linearlayout
     *
     * @param view 要转换为Drawable的View
     * @return BitmapDrawable Drawable
     */
    public static Drawable view2Drawable(View view) {
        BitmapDrawable mBitmapDrawable = null;
        try {
            Bitmap newbmp = view2Bitmap(view);
            if (newbmp != null) {
                mBitmapDrawable = new BitmapDrawable(newbmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    /**
     * 将View转换为Bitmap.需要最上层布局为Linearlayout
     *
     * @param view 要转换为bitmap的View
     * @return byte[] 图片的byte[]
     */
    public static Bitmap view2Bitmap(View view) {
        Bitmap bitmap = null;
        try {
            if (view != null) {
                view.setDrawingCacheEnabled(true);
                view.measure(
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(),
                        view.getMeasuredHeight());
                view.buildDrawingCache();
                bitmap = view.getDrawingCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将View转换为byte[].
     *
     * @param view           要转换为byte[]的View
     * @param compressFormat the compress format
     * @return byte[] View图片的byte[]
     */
    public static byte[] view2Bytes(View view,
                                    Bitmap.CompressFormat compressFormat) {
        byte[] b = null;
        try {
            Bitmap bitmap = ImageUtil.view2Bitmap(view);
            b = ImageUtil.bitmap2Bytes(bitmap, compressFormat, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 描述：旋转Bitmap为一定的角度.
     *
     * @param bitmap  the bitmap
     * @param degrees the degrees
     * @return the bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Bitmap mBitmap = null;
        try {
            Matrix m = new Matrix();
            m.setRotate(degrees % 360);
            mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    /**
     * 描述：旋转Bitmap为一定的角度并四周暗化处理.
     *
     * @param bitmap  the bitmap
     * @param degrees the degrees
     * @return the bitmap
     */
    public static Bitmap rotateBitmapTranslate(Bitmap bitmap, float degrees) {
        Bitmap mBitmap = null;
        int width;
        int height;
        try {
            Matrix matrix = new Matrix();
            if ((degrees / 90) % 2 != 0) {
                width = bitmap.getWidth();
                height = bitmap.getHeight();
            } else {
                width = bitmap.getHeight();
                height = bitmap.getWidth();
            }
            int cx = width / 2;
            int cy = height / 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(degrees);
            matrix.postTranslate(cx, cy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    /**
     * 转换图片转换成圆形.
     *
     * @param bitmap 传入Bitmap对象
     * @return the bitmap
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 转换图片转换成圆角.
     *
     * @param bitmap 传入Bitmap对象
     * @return the bitmap
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap, int roundPx) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        top = 0;
        left = 0;
        right = width;
        bottom = height;
        dst_top = 0;
        dst_left = 0;
        dst_right = width;
        dst_bottom = height;

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 转换图片转换成镜面效果的图片.
     *
     * @param bitmap 传入Bitmap对象
     * @return the bitmap
     */
    public static Bitmap toReflectionBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        try {
            int reflectionGap = 1;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);

            // Create a Bitmap with the flip matrix applied to it.
            // We only want the bottom half of the image
            Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                    width, height / 2, matrix, false);

            // Create a new bitmap with same width but taller to fit
            // reflection
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                    (height + height / 2), Config.ARGB_8888);

            // Create a new Canvas with the bitmap that's big enough for
            // the image plus gap plus reflection
            Canvas canvas = new Canvas(bitmapWithReflection);
            // Draw in the original image
            canvas.drawBitmap(bitmap, 0, 0, null);
            // Draw in the gap
            Paint deafaultPaint = new Paint();
            canvas.drawRect(0, height, width, height + reflectionGap,
                    deafaultPaint);
            // Draw in the reflection
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
            // Create a shader that is a linear gradient that covers the
            // reflection
            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0, bitmap.getHeight(),
                    0, bitmapWithReflection.getHeight() + reflectionGap,
                    0x70ffffff, 0x00ffffff, TileMode.CLAMP);
            // Set the paint to use this shader (linear gradient)
            paint.setShader(shader);
            // Set the Transfer mode to be porter duff and destination in
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            // Draw a rectangle using the paint with our linear gradient
            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                    + reflectionGap, paint);

            bitmap = bitmapWithReflection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 释放Bitmap对象.
     *
     * @param bitmap 要释放的Bitmap
     */
    public static void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                if (!bitmap.isRecycled()) {
                    Log.d(ImageUtil.class.getSimpleName(),
                            "Bitmap释放" + bitmap.toString());
                    bitmap.recycle();
                }
            } catch (Exception e) {
            }
            bitmap = null;
        }
    }

    /**
     * 释放Bitmap数组.
     *
     * @param bitmaps 要释放的Bitmap数组
     */
    public static void releaseBitmapArray(Bitmap[] bitmaps) {
        if (bitmaps != null) {
            try {
                for (Bitmap bitmap : bitmaps) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        Log.d(ImageUtil.class.getSimpleName(),
                                "Bitmap释放" + bitmap.toString());
                        bitmap.recycle();
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 描述：图像的特征值颜色分布 将颜色分4个区，0,1,2,3 区组合共64组，计算每个像素点属于哪个区.
     *
     * @param bitmap the bitmap
     * @return the color histogram
     */
    public static int[] getColorHistogram(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 区颜色分布
        int[] areaColor = new int[64];

        // 获取色彩数组。
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixels = bitmap.getPixel(i, j);
                int alpha = (pixels >> 24) & 0xFF;
                int red = (pixels >> 16) & 0xFF;
                int green = (pixels >> 8) & 0xFF;
                int blue = (pixels) & 0xFF;
                int redArea = 0;
                int greenArea = 0;
                int blueArea = 0;
                // 0-63 64-127 128-191 192-255
                if (red >= 192) {
                    redArea = 3;
                } else if (red >= 128) {
                    redArea = 2;
                } else if (red >= 64) {
                    redArea = 1;
                } else if (red >= 0) {
                    redArea = 0;
                }

                if (green >= 192) {
                    greenArea = 3;
                } else if (green >= 128) {
                    greenArea = 2;
                } else if (green >= 64) {
                    greenArea = 1;
                } else if (green >= 0) {
                    greenArea = 0;
                }

                if (blue >= 192) {
                    blueArea = 3;
                } else if (blue >= 128) {
                    blueArea = 2;
                } else if (blue >= 64) {
                    blueArea = 1;
                } else if (blue >= 0) {
                    blueArea = 0;
                }
                int index = redArea * 16 + greenArea * 4 + blueArea;
                // 加入
                areaColor[index] += 1;
            }
        }
        return areaColor;
    }

    /**
     * 计算"汉明距离"（Hamming distance）。
     * 如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。.
     *
     * @param sourceHashCode 源hashCode
     * @param hashCode       与之比较的hashCode
     * @return the int
     */
    public static int hammingDistance(String sourceHashCode, String hashCode) {
        int difference = 0;
        int len = sourceHashCode.length();
        for (int i = 0; i < len; i++) {
            if (sourceHashCode.charAt(i) != hashCode.charAt(i)) {
                difference++;
            }
        }
        return difference;
    }

    /**
     * 灰度值计算.
     *
     * @param pixels 像素
     * @return int 灰度值
     */
    private static int rgbToGray(int pixels) {
        // int _alpha = (pixels >> 24) & 0xFF;
        int _red = (pixels >> 16) & 0xFF;
        int _green = (pixels >> 8) & 0xFF;
        int _blue = (pixels) & 0xFF;
        return (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
    }

    /**
     * 找到最合适的SampleSize
     *
     * @param width
     * @param height
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private static int findBestSampleSize(int width, int height, int desiredWidth, int desiredHeight) {
        double wr = (double) width / desiredWidth;
        double hr = (double) height / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    /**
     * 将yuv格式转换成灰度bitmap.
     *
     * @param yuvData
     * @param width
     * @param height
     * @return
     */
    public Bitmap yuv2GrayBitmap(byte[] yuvData, int width, int height) {
        int[] pixels = new int[width * height];
        byte[] yuv = yuvData;
        int inputOffset = 0;
        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            for (int x = 0; x < width; x++) {
                int grey = yuv[inputOffset + x] & 0xff;
                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
            }
            inputOffset += width;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 将yuv格式转换成bitmap
     * ImageFormat.NV21 || format == ImageFormat.YUY2
     *
     * @return RGB565 format bitmap
     */
    public static byte[] bitmap2YUV(Bitmap bitmap) {
        // 该方法来自QQ空间
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int len = width * height;
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = pixels[i * width + j] & 0x00FFFFFF;
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;

                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);

                yuv[i * width + j] = (byte) y;
                // yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                // yuv[len + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

    /**
     * 将yuv格式转换成bitmap
     * ImageFormat.NV21 || format == ImageFormat.YUY2
     *
     * @param width
     * @param height
     * @return RGB565 format bitmap
     */
    public static Bitmap yuv2Bitmap(byte[] data, int width, int height) {
        final YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if (!image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
            return null;
        }
        byte[] tmp = os.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
        return bitmap;
    }

    /**
     * TODO
     * ImageFormat.NV21 || format == ImageFormat.YUY2.
     *
     * @param data
     * @param width
     * @param height
     * @param rect
     * @return
     */
    public static Bitmap cropYuv2Bitmap(byte[] data, int width, int height, Rect rect) {
        int w = rect.width();
        int h = rect.height();
        int frameSize = width * height;
        int[] pixels = new int[w * h];
        byte[] yuv = data;
        int yOffset = rect.top * width + rect.left;
        int uvOffset = (rect.top / 2) * width + (rect.left / 2) * 2 + frameSize;
        int y, u, v, k;

        for (int i = 0; i < h; ++i) {
            int outputOffset = i * w;
            for (int j = 0; j < w; ++j) {
                y = (0xff & yuv[yOffset + j]) - 16;

                k = ((j >> 1) << 1);
                v = (0xff & yuv[uvOffset + k]) - 128;
                u = (0xff & yuv[uvOffset + k + 1]) - 128;

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

                pixels[outputOffset + j] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
            yOffset += width;
            if (((rect.top + i) & 1) == 1) {
                uvOffset += width;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * 保存yuv为文件.
     *
     * @param data   the data
     * @param width  the width
     * @param height the height
     * @return the string
     */
    public static String savetoJPEG(byte[] data, int width, int height, String file) {
        Rect frame = new Rect(0, 0, width, height);
        YuvImage img = new YuvImage(data, ImageFormat.NV21, width, height, null);
        OutputStream os = null;
        File jpgfile = new File(file);
        try {
            os = new FileOutputStream(jpgfile);
            img.compressToJpeg(frame, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jpgfile.getPath();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        // System.out.println(getHashCode(""));
    }

}
