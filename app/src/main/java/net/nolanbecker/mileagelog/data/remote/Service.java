package net.nolanbecker.mileagelog.data.remote;

import net.nolanbecker.mileagelog.data.model.Entry;

import java.util.Dictionary;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Service {

    @GET("/miles/{id}")
    Call<Entry> getMiles(@Path("id") int id);

    @FormUrlEncoded
    @POST("/getuser")
    Call<Entry> getUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/miles/{id}")
    Call<Entry> setMiles(@Path("id") int id, @Field("miles") int miles, @Field("date") String date);

    @FormUrlEncoded
    @POST("/updatemiles/{id}")
    Call<Entry> updateMiles(@Path("id") int id, @Field("miles") int miles, @Field("date") String date);

    @FormUrlEncoded
    @POST("/delmiles/{id}")
    Call<Entry> delMiles(@Path("id") int id, @Field("date") String date);

}