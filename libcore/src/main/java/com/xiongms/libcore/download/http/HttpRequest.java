package com.xiongms.libcore.download.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.xiongms.libcore.download.DownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 下载网络请求
 *
 * @author xiongms
 * @time 2018-09-12 14:36
 */
public class HttpRequest {

    private static final String TAG = HttpRequest.class.getSimpleName();

    private static OkHttpClient mOkHttpClient;

    private static final int downloadSuccess = 2;
    private static final int downloading = 3;
    private static final int downloadFailure = 4;
    private static DownloadCallback downloadCallback;//下载回调
    private static long timestamp;

    private boolean isDownloading = false;

    private Call mCurrentCall = null;

    private File downloadFile;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            switch (msg.what) {
                //apk文件下载中,1s回调一次
                case downloading:
                    downloadCallback.onProgress(data.getLong("currentLength"), data.getLong("fileLength"));
                    break;
                //apk文件下载成功
                case downloadSuccess:
                    downloadCallback.onDownloadSuccess((File) data.getSerializable("file"));
                    break;
                //apk文件下载失败
                case downloadFailure:
                    downloadCallback.onDownloadFailure((String) msg.obj);
                    break;
            }
        }
    };

    public HttpRequest() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        mOkHttpClient = builder.connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }


    /**
     * 下载专用
     *
     * @param downloadPath 下载地址
     * @param filePath     文件存储路径
     * @param fileName     文件名
     * @param callback     下载回调
     */
    public void download(@NonNull final String downloadPath, @NonNull final String filePath, @NonNull final String fileName, @NonNull final DownloadCallback callback) {
        downloadCallback = callback;

        Request request = new Request.Builder().url(downloadPath).build();

        if(mCurrentCall != null && mCurrentCall.isExecuted()) {
            mCurrentCall.cancel();
        }
        mCurrentCall = mOkHttpClient.newCall(request);
        mCurrentCall.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        Message message = new Message();
                        message.what = downloadFailure;
                        message.obj = e.toString();
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        if(response.isSuccessful()
                                && writeResponseBodyToDisk(response, filePath, fileName)) {
                            Bundle bundle = new Bundle();
                            Message message = new Message();
                            message.what = downloadSuccess;
                            bundle.putSerializable("file", downloadFile);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = downloadFailure;
                            handler.sendMessage(message);
                        }
                    }
                });
    }

    private boolean writeResponseBodyToDisk(okhttp3.Response response, String filePath, String fileName) {
        try {
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {

                int len;
                byte[] buf = new byte[2048];
                inputStream = response.body().byteStream();
                /**
                 * 写入本地文件
                 */
                String responseFileName = getHeaderFileName(response);
                /**
                 *如果服务器没有返回的话,使用自定义的文件名字
                 */
                if (responseFileName != null && responseFileName.length() > 0) {
                    downloadFile = new File(filePath, responseFileName);
                } else {
                    downloadFile = new File(filePath, fileName);
                }

                long fileSize = response.body().contentLength();
                long fileSizeDownloaded = 0;
                fileOutputStream = new FileOutputStream(downloadFile);
                Bundle bundle = new Bundle();
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                    fileSizeDownloaded += len;

                    if (fileSize > 0) {
                        //这里必须要进行消息发送间隔的约束,这里为1s发送一次,不然会发送大量的message,全部都在排着队,
                        //而这造成的后果就是当下载完成时,发送的下载完成message不能及时发送出去,导致
                        //软件已经下载完成,却不能及时回调下载完成方法,从而不能及时进行安装的bug
                        if (fileSizeDownloaded < fileSize) {
                            if (System.currentTimeMillis() - timestamp < 1000) {
                                continue;
                            }
                        }
                        timestamp = System.currentTimeMillis();
                        //这里需要一直new新的message,不然会报错
                        Message message = new Message();
                        message.what = downloading;
                        bundle.putLong("currentLength", fileSizeDownloaded);
                        bundle.putLong("fileLength", fileSize);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }

                fileOutputStream.flush();
                return true;
            } catch (IOException e) {
                Message message = new Message();
                message.what = downloadFailure;
                message.obj = e.toString();
                handler.sendMessage(message);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (dispositionHeader != null && dispositionHeader.length() > 0) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return "";
        }
        return "";
    }

}

