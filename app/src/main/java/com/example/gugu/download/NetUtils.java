package com.example.gugu.download;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by gugu on 2017/12/10.
 */

public class NetUtils {

   public static long getContentLength(String url){
        if(url==null){
            return 0;
        }
       OkHttpClient client = new OkHttpClient();
       Request request = new Request.Builder().url(url).build();
       try {
           Response response = client.newCall(request).execute();
           if(response!=null&&response.isSuccessful()){
               long contentLength = response.body().contentLength();
               response.body().close();
               return contentLength;
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       return 0;
   }


}
