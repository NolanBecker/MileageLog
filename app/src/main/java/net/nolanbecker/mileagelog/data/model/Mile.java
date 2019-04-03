package net.nolanbecker.mileagelog.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mile {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("miles")
    @Expose
    private Integer miles;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getMiles() {
        return miles;
    }

    public void setMiles(Integer miles) {
        this.miles = miles;
    }

}
