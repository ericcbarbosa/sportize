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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.sportize.app.adapter.GroupAdapater;
import br.com.sportize.app.model.Group;

public class GroupsActivity extends SalesforceActivity implements AppCompatCallback {
    private RestClient client;
    private FloatingActionButton btnAddGroup;
    private ArrayAdapter<Group> listAdapter;
    private ArrayList<Group> groupList;
    private AppCompatDelegate delegate;

    View view;

    private EditText name;
    private EditText description;

    private Button btnCancel;
    private Button btnAddRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);

        // Delegate
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.main);

        // Configura toolbar
        Toolbar toolbar = findViewById(R.id.home_toolbar);

        toolbar.setTitle("Grupos");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddGroup = findViewById(R.id.group_fab_add);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupsActivity.this, "Clicou que eu vi", Toast.LENGTH_LONG).show();
                displayDialog();
            }
        });
	}

	@Override
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);

		groupList = new ArrayList<>();
		listAdapter = new GroupAdapater(GroupsActivity.this, groupList);

		ListView listView = findViewById(R.id.contacts_list);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Group group = groupList.get(position);

				Intent intent = new Intent(GroupsActivity.this, GroupDetailActivity.class);

				intent.putExtra("id", group.getId());
				intent.putExtra("name", group.getName());
				intent.putExtra("description", group.getDescription());

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

		// Get Groups
        tryToLoadGroups();
	}


    // DIALOG
    public void displayDialog() {
        final Dialog dialog = new Dialog(GroupsActivity.this);

        dialog.setTitle("Cadastrar Grupo");
        dialog.setContentView(R.layout.group_register);
        dialog.setCancelable(false);

        name = dialog.findViewById(R.id.group_register_name);
        description = dialog.findViewById(R.id.group_register_description);

        btnAddRegister = dialog.findViewById(R.id.user_register_btn_add);
        btnCancel = dialog.findViewById(R.id.group_register_btn_cancel);

        btnAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> fields = new HashMap<String, Object>();

                if (name.getText().toString() != null && description.getText().toString() != null) {
                    fields.put("Name", name.getText().toString());
                    fields.put("group_description__c", description.getText().toString());

                    tryToAddNewGroup(fields);

                    dialog.dismiss();
                } else {
                    Toast.makeText(GroupsActivity.this, "Informe os dados do grupo para cadastrar.", Toast.LENGTH_SHORT).show();
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
                tryToLoadGroups();
            }
        });

        dialog.show();
    }

    // API REQUESTS
    private void tryToLoadGroups() {
        try {
            loadGroups();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void loadGroups() throws UnsupportedEncodingException {
        RestRequest restRequest = RestRequest.getRequestForQuery(
                ApiVersionStrings.getVersionNumber(this),
                "SELECT Id, Name, group_description__c FROM group__c\t"
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

                                // TODO: Passar um User para o adapter
                                String groupId = records.getJSONObject(i).getString("Id");
                                String groupName = records.getJSONObject(i).getString("Name");
                                String groupDescription = records.getJSONObject(i).getString("group_description__c");

                                Group g = new Group(groupId, groupName, groupDescription);
                                groupList.add(g);
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
                        Toast.makeText(GroupsActivity.this,
                                GroupsActivity.this.getString(
                                        SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),
                                        exception.toString()
                                ),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void tryToAddNewGroup(Map<String, Object> fields) {
        try {
            addNewGroup(fields);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void addNewGroup(Map<String, Object> fields) throws UnsupportedEncodingException {
        RestRequest restRequest = RestRequest.getRequestForCreate(
                ApiVersionStrings.getVersionNumber(this),
                "group__c",
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
                        tryToLoadGroups();
                    }
                });
            }

            @Override
            public void onError(final Exception exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GroupsActivity.this,
                                GroupsActivity.this.getString(
                                        SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),
                                        exception.toString()
                                ),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}

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
