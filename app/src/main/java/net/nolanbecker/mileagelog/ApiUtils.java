package net.nolanbecker.mileagelog;

import net.nolanbecker.mileagelog.data.remote.RetrofitClient;
import net.nolanbecker.mileagelog.data.remote.Service;

public class ApiUtils {

    public static final String BASE_URL = "http://mileagelog.nolanbecker.net/";

    public static Service getService() {
        return RetrofitClient.getClient(BASE_URL).create(Service.class);
    }

}
