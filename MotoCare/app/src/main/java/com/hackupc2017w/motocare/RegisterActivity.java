package com.hackupc2017w.motocare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hackupc2017w.motocare.network.SafeMeAPI;
import com.hackupc2017w.motocare.network.UserDTO;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText etUsername;

    @BindView(R.id.userId)
    EditText etUserId;

    @BindView(R.id.loading)
    View loadingView;

    @BindView(R.id.form)
    View formView;
    private SafeMeAPI service;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(App.APP_PREFS, Context.MODE_PRIVATE);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://safeme-backend-app.scalingo.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(SafeMeAPI.class);

    }

    @OnClick(R.id.register_button)
    public void register(final View view) {
        boolean isValid = true;
        String username = etUsername.getText().toString();
        String dni = etUserId.getText().toString();

        if (username.isEmpty()) {
            etUsername.setError("Please fill your user name");
            isValid = false;
        }
        if (dni.isEmpty() || dni.length() != 9) {
            etUserId.setError("Your DNI (id) has wrong format");
            isValid = false;
        }

        if (isValid) {
            formView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            UserDTO user = new UserDTO(username, dni);
            Call newUser = service.createUser(user);
            newUser.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        Snackbar.make(view, "Your use has been created successfully", Snackbar.LENGTH_LONG)
                                .show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("userId", ((UserDTO) response.body()).getId());
                        editor.apply();
                        finish();
                    } else {
                        formView.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
                        Snackbar.make(view, "You are already registered please login", Snackbar.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    formView.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.GONE);

                    Snackbar.make(view, "There is a network problem, please try again later", Snackbar.LENGTH_LONG)
                            .show();


                }
            });
        }


    }
}
