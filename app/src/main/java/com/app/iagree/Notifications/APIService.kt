package com.app.iagree.Notifications

import retrofit2.Call;
import retrofit2.http.Headers
import retrofit2.http.POST
import com.app.iagree.Notifications.MyResponse
import com.app.iagree.Notifications.Sender
import retrofit2.http.Body
import retrofit2.http.Header

public interface APIService {


    @Headers(
        "Content-Type:application/json",
        "Authorization: key=AAAAPC_Gdbg:APA91bHqBrz_HKTdHXd3Xk4q2dNjAKtKCWQNv1CcdlUOTIzjtlA7iVkVyugAbGBZkF3KQSWHnCJC3rTPiVfEY8_NpjmhBJJJ07LmOrmZZ_DTahFU8M3xrz3bdm1UcDJzGvPpx2G27BkV"
    )

    @POST("fcm/send")
    fun noti(@Body s: Sender): Call<MyResponse>

}