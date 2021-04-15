package tdp2.app;

import retrofit2.Call;
import retrofit2.http.GET;
import tdp2.model.BankATM;

public interface BankATMService {
    @GET("cajeros-automaticos.json")
    Call<BankATM[]> getATMBanks();
}
