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

import br.com.sportize.app.adapter.EventAdapter;
import br.com.sportize.app.model.Event;

public class EventsActivity extends SalesforceActivity implements AppCompatCallback {
    private RestClient client;
    private ArrayAdapter<Event> listAdapter;
    private ArrayList<Event> eventList;
    private AppCompatDelegate delegate;

    View view;

    private EditText txtName;
    private EditText txtDescription;
    private EditText txtDate;
    private EditText txtTime;
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
		setContentView(R.layout.event_main);

        // Delegate
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.event_main);

        // Configura toolbar
        Toolbar toolbar = findViewById(R.id.event_home_toolbar);

        toolbar.setTitle("Eventos");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton btnAddEvent = findViewById(R.id.event_fab_add);

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
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

		eventList = new ArrayList<Event>();
		listAdapter = new EventAdapter(EventsActivity.this, eventList);

		ListView listView = findViewById(R.id.events_list);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Event event = eventList.get(position);

				Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);

				intent.putExtra("id", event.getId());
				intent.putExtra("groupId", event.getGroupId());
				intent.putExtra("name", event.getName());
				intent.putExtra("description", event.getDescription());
				intent.putExtra("date", event.getOccurrenceDate());
				intent.putExtra("time", event.getOccurrenceTime());
				intent.putExtra("state", event.getState());
				intent.putExtra("city", event.getCity());
                intent.putExtra("neighborhood", event.getNeighborhood());
                intent.putExtra("address", event.getAddress());

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

		// Get Events
        tryToLoadEvents();
	}


    // DIALOG
    public void displayDialog() {
        final Dialog dialog = new Dialog(EventsActivity.this);

        dialog.setTitle("Cadastrar Evento");
        dialog.setContentView(R.layout.event_register);
        dialog.setCancelable(false);

//        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
//        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
//
//        dialog.getWindow().setLayout(width, height);

        txtName = dialog.findViewById(R.id.event_register_name);
        txtDescription = dialog.findViewById(R.id.event_register_description);
        txtDate = dialog.findViewById(R.id.event_register_date);
        txtTime = dialog.findViewById(R.id.event_register_time);
        txtAddress = dialog.findViewById(R.id.event_register_address);
        txtNeighbor = dialog.findViewById(R.id.event_register_neighbor);
        txtCity = dialog.findViewById(R.id.event_register_city);
        txtState = dialog.findViewById(R.id.event_register_state);

        btnAddRegister = dialog.findViewById(R.id.event_register_btn_add);
        btnCancel = dialog.findViewById(R.id.event_register_btn_cancel);

        btnAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> fields = new HashMap<String, Object>();

                if (txtName.getText() != null && txtDate.getText() != null) {
                    fields.put("Name", txtName.getText().toString());
                    fields.put("description__c", txtDescription.getText().toString());
                    fields.put("event_date__c", txtDate.getText().toString());
                    fields.put("event_time__c", txtTime.getText().toString());
                    fields.put("event_address__c", txtAddress.getText().toString());
                    fields.put("event_neighborhood__c", txtNeighbor.getText().toString());
                    fields.put("event_city__c", txtCity.getText().toString());
                    fields.put("state__c", txtState.getText().toString());
                    fields.put("event_group__c", "a045A00001PR7JfQAL");

                    tryToAddNewEvent(fields);

                    dialog.dismiss();
                } else {
                    Toast.makeText(EventsActivity.this, "Informe os dados do jogador para cadastrar.", Toast.LENGTH_SHORT).show();
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
                tryToLoadEvents();
            }
        });

        dialog.show();
    }

    // API REQUESTS
    private void tryToLoadEvents() {
        try {
            loadEvents();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void loadEvents() throws UnsupportedEncodingException {
        RestRequest restRequest = RestRequest.getRequestForQuery(
                ApiVersionStrings.getVersionNumber(this),
                "SELECT Id, Name, description__c, event_group__c, event_date__c, event_time__c, state__c, event_city__c, event_address__c, event_neighborhood__c FROM event__c\t"
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

                            // TODO: Extrair dados do JSON e transformar em Evento
                            for (int i = 0; i < records.length(); i++) {

                                JSONObject record = records.getJSONObject(i);

                                Event event = new Event(
                                        record.getString("Id"),
                                        record.getString("event_group__c"),
                                        record.getString("Name"),
                                        record.getString("description__c"),
                                        record.getString("event_date__c"),
                                        record.getString("event_time__c"),
                                        record.getString("event_address__c"),
                                        record.getString("event_neighborhood__c"),
                                        record.getString("event_city__c"),
                                        record.getString("state__c")
                                );

                                eventList.add(event);
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
                        Toast.makeText(EventsActivity.this,
                                EventsActivity.this.getString(
                                        SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(),
                                        exception.toString()
                                ),
                                Toast.LENGTH_LONG).show();

                        Log.i("==> LoadEvent:", exception.toString());
                    }
                });
            }
        });
    }

    private void tryToAddNewEvent(Map<String, Object> fields) {
        try {
            addNewEvent(fields);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void addNewEvent(Map<String, Object> fields) throws UnsupportedEncodingException {
        RestRequest restRequest = RestRequest.getRequestForCreate(
                ApiVersionStrings.getVersionNumber(this),
                "event__c",
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
                        tryToLoadEvents();

                        if (result.isSuccess()) {
                            try {
                                String reqMessage = result.asJSONObject().getString("message");

                                if (reqMessage != null) {
                                    Toast.makeText(EventsActivity.this, reqMessage, Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Toast.makeText(EventsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("==> sendAsync: ", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(final Exception exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EventsActivity.this,
                                EventsActivity.this.getString(
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
