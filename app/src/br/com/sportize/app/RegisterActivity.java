package br.com.sportize.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import br.com.sportize.app.model.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText address;
    private EditText neighbor;
    private EditText city;
    private EditText state;

    private Button btnCancel;
    private Button btnAddRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.user_register_name);
        email = (EditText) findViewById(R.id.user_register_email);
        password = (EditText) findViewById(R.id.user_register_password);
        address = (EditText) findViewById(R.id.user_register_address);
        neighbor = (EditText) findViewById(R.id.user_register_neighbor);
        city = (EditText) findViewById(R.id.user_register_city);
        state = (EditText) findViewById(R.id.user_register_state);

        btnAddRegister = (Button) findViewById(R.id.user_register_btn_add);
        btnCancel = (Button) findViewById(R.id.user_register_btn_cancel);

        btnAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUser(
                        name.getText().toString(),
                        email.getText().toString(),
                        password.getText().toString(),
                        address.getText().toString(),
                        neighbor.getText().toString(),
                        city.getText().toString(),
                        state.getText().toString()
                );
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();
            }
        });
    }

    public void AddUser(String name, String email, String password, String address, String neighborhood, String city, String state) {
        Random rand = new Random();
//        User user = new User(rand.nextInt(100) + 5, name, email, password, address, neighborhood, city, state);
//        String message = "Usuário cadastrado com sucesso!\n\n" +
//                "Nome: " + user.getName() + "\n" +
//                "E-mail: " + user.getEmail() + "\n" +
//                "Endereço: " + user.getAddress() + "\n" +
//                "Bairro: " + user.getNeighborhood() + "\n" +
//                "Cidade: " + user.getCity() + "\n" +
//                "Estado: " + user.getState() + "\n";

//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        goToLoginPage();
    }

    public void goToLoginPage() {
//        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
    }
}
