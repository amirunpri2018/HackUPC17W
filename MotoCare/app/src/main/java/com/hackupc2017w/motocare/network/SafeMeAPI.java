package com.hackupc2017w.motocare.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by hussein on 04/03/2017.
 */

public interface SafeMeAPI {
    @POST("/users/")
    Call<UserDTO> createUser(@Body UserDTO user);
 }
