package tech.progarden.world.web_requests.kulture;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by brajan on 10/5/2017.
 */

public interface KultureInterface {
    @POST("parametri.php?action=listaKulturaPoSenzoru")
    Call<KultureResponse> getKulture(
            @Body KulutureParams id);
}
