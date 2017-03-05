package com.hackupc2017w.motocare.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hussein on 04/03/2017.
 */
public class UserDTO {
    int id;
    @SerializedName("user_name")
    String name;
    @SerializedName("user_id") String dni;

    public UserDTO(String name, String dni) {
        this.name = name;
        this.dni = dni;
    }

    public int getId() {
        return id;
    }
}
