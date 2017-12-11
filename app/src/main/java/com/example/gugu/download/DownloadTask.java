package com.example.gugu.download;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by gugu on 2017/12/10.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    
    private DownloadListener listener;

    public static final int TYPE_SUCCESS = 0;

    public static final int TYPE_FAILED = 1;

    public static final int TYPE_PAUSED = 2;

    public static final int TYPE_CANCELED = 3;

    private boolean isPaused = false;
    private boolean isCancelled = false;
    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        final File file;
        long downloadLength = 0; //记录已经下载的文件长度
        RandomAccessFile savedFile = null;
        String downloadUrl = params[0];
        //下载文件的名称
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        //下载文件存放的目录
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        //创建一个文件
        file = new File(directory + fileName);
        if (file.exists()) {
            //如果文件存在的话，得到文件的大小
            downloadLength = file.length();
        }
        final long contentLength = NetUtils.getContentLength(downloadUrl);

        if (contentLength == 0) {
            return TYPE_FAILED;
        } else {
            if (contentLength == downloadLength) {
                return TYPE_SUCCESS;
            }
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl)
                .addHeader("RANGE", "bytes=" + downloadLength + "-") //断点续传要用到的，指示下载的区间
                .build();

        final long finalDownloadLength = downloadLength;
        Response response = null;
        InputStream is = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(finalDownloadLength);
                byte[] bytes = new byte[1024];
                int total = 0;
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    if (isCancelled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;

                    } else {
                        total += len;
                        savedFile.write(bytes, 0, len);
                        //计算已经下载的百分比
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        //注意：在doInBackground()中是不可以进行UI操作的，如果需要更新UI,比如说反馈当前任务的执行进度，
                        //可以调用publishProgress()方法完成。
                        publishProgress(progress);
                    }

                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            Log.d("xyc", "doInBackground: e="+e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }

            } catch (Exception e) {

            }
        }

        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];

        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
       switch (status){
           case TYPE_CANCELED:
               listener.onCanceled();
               break;
           case TYPE_SUCCESS:
               listener.onSuccess();
               break;
           case TYPE_FAILED:
               listener.onFailed();
               break;
           case TYPE_PAUSED:
               listener.onPaused();
               break;
           default:
               break;

       }
    }
    public void  pauseDownload(){
        isPaused=true;
    }
    public void cancelDownload(){
        isCancelled=true;
    }

}
