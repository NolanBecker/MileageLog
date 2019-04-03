package net.nolanbecker.mileagelog.data.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("miles")
    @Expose
    private List<Mile> miles = null;
    @SerializedName("user")
    @Expose
    private List<User> user = null;

    public List<Mile> getMiles() {
        return miles;
    }

    public void setMiles(List<Mile> miles) {
        this.miles = miles;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

}