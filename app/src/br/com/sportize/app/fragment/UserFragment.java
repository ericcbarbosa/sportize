package br.com.sportize.app.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import br.com.sportize.app.R;
import br.com.sportize.app.adapter.UserAdapter;
import br.com.sportize.app.model.User;

public class UserFragment extends Fragment {
    private RecyclerView recycler;
    private ArrayList<User> usersList;
    private UserAdapter adapter;
    View myView;

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText address;
    private EditText neighbor;
    private EditText city;
    private EditText state;

    private Button btnCancel;
    private Button btnAddRegister;
    private FloatingActionButton btnFloatingRegister;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Toast.makeText(getActivity(),"Context Menu - Novo", Toast.LENGTH_LONG);
                displayDialog();
                break;
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.user_fragment, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        btnFloatingRegister = myView.findViewById(R.id.user_floating_register);
//        recycler = myView.findViewById(R.id.user_recycler);
//        recycler.setLayoutManager(linearLayoutManager);

        usersList = new ArrayList<User>();
//        usersList.add(new User(
//                1,
//                "Eric",
//                "eric@fiap.com.br",
//                "123",
//                "rua jaime de oliveira, 60",
//                "Portal dos Gramados",
//                "Guarulhos",
//                "SP"
//        ));
//        usersList.add(new User(
//                2,
//                "João",
//                "joao@fiap.com.br",
//                "123",
//                "rua Dr Amancio de Carvalho, 182",
//                "Vila Mariana",
//                "São Paulo",
//                "SP"
//        ));
//        usersList.add(new User(
//                3,
//                "Mariana",
//                "mariana@fiap.com.br",
//                "123",
//                "Av Lins, 1000",
//                "Vila Mariana",
//                "São Paulo",
//                "SP"
//        ));

//        adapter = new UserAdapter(getActivity(), usersList);
//        recycler.setAdapter(adapter);
//
//        btnFloatingRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayDialog();
//            }
//        });

        return myView;
    }

    public void displayDialog() {
        final Dialog dialog = new Dialog(getContext());

        dialog.setTitle("Cadastrar usuário");
        dialog.setContentView(R.layout.user_register);
        dialog.setCancelable(true);

        name = (EditText) dialog.findViewById(R.id.user_register_name);
        email = (EditText) dialog.findViewById(R.id.user_register_email);
        password = (EditText) dialog.findViewById(R.id.user_register_password);
        address = (EditText) dialog.findViewById(R.id.user_register_address);
        neighbor = (EditText) dialog.findViewById(R.id.user_register_neighbor);
        city = (EditText) dialog.findViewById(R.id.user_register_city);
        state = (EditText) dialog.findViewById(R.id.user_register_state);

        btnAddRegister = (Button) dialog.findViewById(R.id.user_register_btn_add);
        btnCancel = (Button) dialog.findViewById(R.id.user_register_btn_cancel);

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

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersList();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void AddUser(String name, String email, String password, String address, String neighborhood, String city, String state) {
        Random rand = new Random();

//        usersList.add( new User(rand.nextInt(100) + 5, name, email, password, address, neighborhood, city, state) );
        adapter.notifyDataSetChanged();
    }

    private void getUsersList() {
        adapter = new UserAdapter(getContext(), usersList);
//        recycler.setAdapter(adapter);
    }
}
