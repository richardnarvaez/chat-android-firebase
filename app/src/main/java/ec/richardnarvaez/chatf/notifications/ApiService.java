package ec.richardnarvaez.chatf.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAcr_mGIQ:APA91bFhJ8br2rBc2hSD-sejT7DCYJNckueA-Zvtx6YpWc6Fo9HGtRtZTumUDYtezEXTkwEVBf7w9scgOvyc4LBCoeoKgCacged8mo-ydUimsW9KoggvoLGW7lL5XhfePEs1i2OvpueR"
    })

    @POST("fcm/send")
    Call<Response> sendNotify(@Body Sender body);
}
