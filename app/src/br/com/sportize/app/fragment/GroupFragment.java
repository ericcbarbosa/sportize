package br.com.sportize.app.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import br.com.sportize.app.GroupDetailActivity;
import br.com.sportize.app.R;
import br.com.sportize.app.adapter.GroupAdapater;
import br.com.sportize.app.model.Group;
import br.com.sportize.app.model.User;

import br.com.sportize.app.GroupDetailActivity;

public class GroupFragment extends Fragment {
    private ListView listview;
    private ArrayList<Group> groupsList;
    private ArrayList<User> membersA;
    private GroupAdapater adapter;
    View view;

    private EditText name;
    private EditText description;

    private Button btnCancel;
    private Button btnAddRegister;
    private FloatingActionButton btnFloatingRegister;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
//                displayDialog();
                break;
        }

        return true;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        try {
//            this.fetchData();
//        } catch (Exception  e) {
//            Log.d("==> Exception", e + "\n\n");
//            e.printStackTrace();
//        }

        groupsList = new ArrayList<>();
        groupsList.add(new Group(
                "a045A00001PR7KDQA1",
                "Sparta",
                "Os matadores de sparta"
        ));
        groupsList.add(new Group(
                "a045A00001PR7KDQA1",
                "Atenas",
                "Os matadores de atenas"
        ));
        groupsList.add(new Group(
                "a045A00001PR7KDQA1",
                "Brazil",
                "Os matadores de brasil"
        ));

        View view = inflater.inflate(R.layout.fragment_group, container, false);

        listview = view.findViewById(R.id.group_listview);

        adapter = new GroupAdapater(getActivity(), groupsList);
        listview.setAdapter(adapter);

        // Detail group
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);

                // recupera dados a serem passados
                Group group = groupsList.get(position);

                Toast.makeText(getActivity(), "ID: " + group.getId(), Toast.LENGTH_LONG).show();

                // Enviando dados para conversa activity
                intent.putExtra("id", group.getId());

                startActivity(intent);
            }
        });

        // Add group
        btnFloatingRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "clicou", Toast.LENGTH_LONG).show();
                displayDialog();
            }
        });

        return view;
    }

    public void displayDialog() {
        final Dialog dialog = new Dialog(getContext());

        dialog.setTitle("Cadastrar Grupo");
        dialog.setContentView(R.layout.group_register);
        dialog.setCancelable(true);

        name = (EditText) dialog.findViewById(R.id.group_register_name);
        description = (EditText) dialog.findViewById(R.id.group_register_description);

        btnAddRegister = (Button) dialog.findViewById(R.id.user_register_btn_add);
        btnCancel = (Button) dialog.findViewById(R.id.group_register_btn_cancel);

        btnAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroup(
                        name.getText().toString(),
                        description.getText().toString()
                );

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGroupsList();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void AddGroup(String name, String description) {
        groupsList.add( new Group("a045A00001PR7KDQA1", name, description) );
        adapter.notifyDataSetChanged();
    }

    private void getGroupsList() {
        adapter = new GroupAdapater(getContext(), groupsList);
        listview.setAdapter(adapter);
    }

    private void fetchData() {
        String url = this.buildUrl();
    }

    private String buildUrl() {
        String url = "https://login.salesforce.com/services/oauth2/authorize?response_type=%s&client_id=%s&redirect_uri=%s";
        String responseType = "code";
        String clientId = "3MVG9vrJTfRxlfl5fRe2VYx1W91T6mW81y9z7zs8SUkeXNGItILvHiNjeh0z3LsKZ1o9yXJr16p8QJ7kBZBpS";
        String redirectUrl = "myapp:///sportize/api/auth//success";

        return String.format(url, responseType, clientId, redirectUrl);
    }
}
