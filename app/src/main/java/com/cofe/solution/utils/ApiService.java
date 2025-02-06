package com.cofe.solution.utils;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("getconfig/")
    Call<ResponseBody> getDeviceConfiguration(
            @Header("Authorization") String authorization,
            @Header("DeviceAuthorization") String deviceAuthorization,
            @Header("Accept-Language") String acceptLanguage,
            @Header("Content-Type") String contentType,
            @Body RequestBody body
    );
}
