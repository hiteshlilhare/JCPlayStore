/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;

/**
 *
 * @author Hitesh
 */
public class Util {

    public static final String SERVER_URL = "http://localhost:8010/jcpss";
    public static final String APP_LIST_SERVICE = SERVER_URL + "/getVerifiedReleasedAppsDetail";
    public static final String GET_APP_SERVICE = SERVER_URL + "/getApp";

    // post request media type
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    
    /**
     * Do post request to given url with json as payload. 
     * @param url
     * @param json
     * @return
     * @throws IOException 
     */
    public static String doPostRequest(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Do get request to the given url. 
     * @param url
     * @return
     * @throws IOException 
     */
    public static String doGetRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
