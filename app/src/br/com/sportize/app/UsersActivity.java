/*
 * Copyright (c) 2012-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.sportize.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.sportize.app.adapter.UserAdapter;
import br.com.sportize.app.model.User;

public class UsersActivity extends SalesforceActivity implements AppCompatCallback {
    private RestClient client;
    private ArrayAdapter<User> listAdapter;
    private ArrayList<User> userList;
    private AppCompatDelegate delegate;

    View view;

    private EditText txtName;
    private EditText txtPassword;
    private EditText txtEmail;
    private EditText txtAddress;
    private EditText txtNeighbor;
    private EditText txtCity;
    private EditText txtState;

    private Button btnCancel;
    private Button btnAddRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.user_main);

        // Delegate
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.user_main);

        // Configura toolbar
        Toolbar toolbar = findViewById(R.id.user_home_toolbar);

        toolbar.setTitle("Jogadores");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton btnAddUser = findViewById(R.id.user_fab_add);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog();
            }
        });
	}

	@Override
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);

		userList = new ArrayList<User>();
		listAdapter = new UserAdapter(UsersActivity.this, userList);

		ListView listView = findViewById(R.id.users_list);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User user = userList.get(position);

				Intent intent = new Intent(UsersActivity.this, UserDetailActivity.class);

				intent.putExtra("id", user.getId());
				intent.putExtra("name", user.getName());
				intent.putExtra("email", user.getEmail());
				intent.putExtra("password", user.getPassword());
                intent.putExtra("address", user.getAddress());
                intent.putExtra("neighborhood", user.getNeighborhood());
				intent.putExtra("city", user.getCity());
				intent.putExtra("state", user.getState());

				startActivity(intent);
			}
		});

		super.onResume();
	}

	@Override
	public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

		// Show everything
		findViewById(R.id.root).setVisibility(View.VISIBLE);

		// Get users
        tryToLoadUsers();
	}


    // DIALOG
    public void displayDialog() {
        final Dialog dialog = new Dialog(UsersActivity.this);

        dialog.setTitle("Cadastrar Grupo");
        dialog.setContentView(R.layout.user_register);
        dialog.setCancelable(false);

        txtName = dialog.findViewById(R.id.user_register_name);
        txtEmail = dialog.findViewById(R.id.user_register_email);
        txtPassword = dialog.findViewById(R.id.user_register_password);
        txtAddress = dialog.findViewById(R.id.user_register_address);
        txtNeighbor = dialog.findViewById(R.id.user_register_neighbor);
        txtCity = dialog.findViewById(R.id.user_register_city);
        txtState = dialog.findViewById(R.id.user_register_state);

        btnAddRegister = dialog.findViewById(R.id.user_register_btn_add);
        btnCancel = dialog.findViewById(R.id.user_register_btn_cancel);

        btnAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> fields = new HashMap<String, Object>();

                if (txtName.getText().toString() != null && txtEmail.getText().toString() != null) {
                    fields.put("Name", txtName.getText().toString());
                    fields.put("email__c", txtEmail.getText().toString());
                    fields.put("password__c", txtPassword.getText().toString());
                    fields.put("address__c", txtAddress.getText().toString());
                    fields.put("neighborhood__c", txtNeighbor.getText().toString());
                    fields.put("city__c", txtCity.getText().toString());
                    fields.put("state__c", txtState.getText().toString());

                    tryToAddNewUser(fields);

                    dialog.dismiss();
                } else {
                    Toast.makeText(UsersActivity.this, "Informe os dados do jogador para cadastrar.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                tryToLoadUsers();
            }
        });

        dialog.show();
    }

    // API REQUESTS
    private void tryToLoadUsers() {
        try {
            loadUsers();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() throws UnsupportedEncodingException {
        RestRequest restRequest = RestRequest.getRequestForQuery(
                ApiVersionStrings.getVersionNumber(this),
                "SELECT Id, Name, email__c, password__c, state__c, city__c, address__c, neighborhood__c FROM player__c\t"
        );

        client.sendAsync(restRequest, new AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {
                result.consumeQuietly(); // consume before going back to main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listAdapter.clear();
                            JSONArray records = result.asJSONObject().getJSONArray("records");

                            // TODO: Extrair dados do JSON e transformar em User
                            for (int i = 0; i < records.length(); i++) {

                                JSONObject record = records.getJSONObject(i);

                                User user = new User(
                                        record.getString("Id"),
                                        record.getString("Name"),
                                        record.getString("email__c"),
                                        record.getString("password__c"),
                                        record.getString("address__c"),
                                        record.getString("neighborhood__c"),
                                        record.getString("city__c"),
                                        record.getString("state__c")
                                );

                                userList.add(user);
                            }
                        } catch (Exception e) {
                            onError(e);
                        }
                    }
                });
            }

            @Override
            public void onError(final Exception exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UsersActivity.this,
                                UsersActivity.this.getString(
                                        SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),
                                        exception.toString()
                                ),
                                Toast.LENGTH_LONG).show();

                        Log.i("==> LoadUser:", exception.toString());
                    }
                });
            }
        });
    }

    private void tryToAddNewUser(Map<String, Object> fields) {
        try {
            addNewUser(fields);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void addNewUser(Map<String, Object> fields) throws UnsupportedEncodingException {
        RestRequest restRequest = RestRequest.getRequestForCreate(
                ApiVersionStrings.getVersionNumber(this),
                "player__c",
                fields
        );

        client.sendAsync(restRequest, new AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {
                result.consumeQuietly(); // consume before going back to main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.clear();
                        tryToLoadUsers();
                    }
                });
            }

            @Override
            public void onError(final Exception exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UsersActivity.this,
                                UsersActivity.this.getString(
                                        SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),
                                        exception.toString()
                                ),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    // OVERRIDES
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {}

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {}

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
