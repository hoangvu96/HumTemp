package com.example.temphum;

public class ApiUtils {
    public static final String BASE_URL = "";

    public static APIService getSOService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
