package com.kimoo.android.bildirimler;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAkZG3m-0:APA91bFighRAISQmerzA4MNOM3GtTxBNDUWOYv_QGvggOiyjbm3hO-HRUhuYkHh5RTfPHc4uLIwqfKoe_MQ3fL4lwgHfT78Emck-1tZDEYEF6dNuKgmMaE8I7EDrKZHK-43AH--MyF3w"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
