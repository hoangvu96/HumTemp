package com.example.temphum;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("localhost")
    Call<Data> getItem();
}
