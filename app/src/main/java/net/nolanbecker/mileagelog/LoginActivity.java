package net.nolanbecker.mileagelog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.nolanbecker.mileagelog.data.PrefManager;
import net.nolanbecker.mileagelog.data.model.Entry;
import net.nolanbecker.mileagelog.data.model.User;
import net.nolanbecker.mileagelog.data.remote.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText txtEmail, txtPasswd;
    User user;

    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        service = ApiUtils.getService();

        btnLogin = (Button) findViewById(R.id.btnLogin);

        txtEmail = (EditText) findViewById(R.id.editEmail);
        txtPasswd = (EditText) findViewById(R.id.editPassword);

        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                checkUser(txtEmail.getText().toString(), txtPasswd.getText().toString());
                break;
            default:
                break;
        }
    }

    private void checkUser(String email, String password) {

        service.getUser(email, password).enqueue(new Callback<Entry>() {
            @Override
            public void onResponse(Call<Entry> call, Response<Entry> response) {
                if (response.isSuccessful()) {
                    user = response.body().getUser().get(0);
                    Toast.makeText(getApplicationContext(), "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
                    signIn();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Entry> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        new PrefManager(this).saveLoginInfo(txtEmail.getText().toString(), txtPasswd.getText().toString(), user.getId());
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
