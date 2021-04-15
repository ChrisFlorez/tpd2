package tdp2.app;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tdp2.model.BankATM;

// gets the bankAtm info from the service
public class DataManager {
    List<BankATM> bankATMS = new ArrayList<>();
    BankATMService service;
    MapsActivity caller;

    public List<BankATM> getBankATMS() {
        return bankATMS;
    }

    public DataManager(MapsActivity calling) {
        caller = calling;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://bubbles.easytech.com.ar:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(BankATMService.class);

        Call<BankATM[]> bankATMCall = service.getATMBanks();

        bankATMCall.enqueue(new Callback<BankATM[]>() {
            @Override
            public void onResponse(Call<BankATM[]> call, Response<BankATM[]> response) {
                BankATM[] bankATM= response.body();

                bankATMS = new ArrayList<>();

                for (int i=0; i < bankATM.length; i++) {
                    bankATMS.add(bankATM[i]);
                }

                caller.onDataManagerFinish();
            }

            @Override
            public void onFailure(Call<BankATM[]> call, Throwable t) {
                Log.println(Log.ERROR, "DATAMANAGER", "Unable to perform request.");
            }
        });
    }

    protected String getFakeJSONFile() {
        String fakeJSON = "[{ \"id\": 11073, \"long\": -58.3709017854754, \"lat\": -34.605812942035, \"banco\": \"NUEVO BANCO DE SANTA FE S.A.\", \"red\": \"LINK\", \"ubicacion\": \"25 De Mayo 168\", \"localidad\": \"CABA\", \"terminales\": 1, \"no_vidente\": \"False\", \"dolares\": \"False\", \"calle\": \"25 De Mayo\", \"altura\": 168, \"calle2\": \"Calle falsa 123\", \"barrio\": \"San Nicolas\", \"comuna\": \"Comuna 1\", \"codigo_postal\": 1002, \"codigo_postal_argentino\": \"C1002ABD\"}, { \"id\": 11059, \"long\": -58.3709757833981, \"lat\": -34.6050839250446, \"banco\": \"BANCO DE LA NACION ARGENTINA\", \"red\": \"LINK\", \"ubicacion\": \"25 De Mayo 230\", \"localidad\": \"CABA\", \"terminales\": 1, \"no_vidente\": \"False\", \"dolares\": \"False\", \"calle\": \"25 De Mayo\", \"altura\": 230, \"calle2\": \"\", \"barrio\": \"San Nicolas\", \"comuna\": \"Comuna 1\", \"codigo_postal\": 1002, \"codigo_postal_argentino\": \"C1002ABF\" }]";
        return fakeJSON;
    }
}
