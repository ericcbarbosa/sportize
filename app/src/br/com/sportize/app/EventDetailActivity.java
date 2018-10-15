package br.com.sportize.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import java.util.HashMap;
import java.util.Map;

import br.com.sportize.app.model.Event;

public class EventDetailActivity extends SalesforceActivity implements AppCompatCallback {

    private RestClient client;
    private AppCompatDelegate delegate;

    // Extras: dados do Evento
    private Event event;
    
    EditText editName;
    EditText txtDescription;
    EditText txtDate;
    EditText txtTime;
    EditText editAddress;
    EditText editCity;
    EditText editState;
    EditText editNeighborhood;

    Button btnRemove;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Delegate
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.event_detail_update);

        // Configura toolbar
        Toolbar toolbar = findViewById(R.id.event_detail_toolbar);

        toolbar.setTitle("Detalhes do Jogador");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);

        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        this.setContent();
    }

    private void setContent() {
        editName = findViewById(R.id.event_detail_name);
        txtDescription = findViewById(R.id.event_detail_description);
        txtDate = findViewById(R.id.event_detail_date);
        txtTime = findViewById(R.id.event_detail_time);
        editState = findViewById(R.id.event_detail_state);
        editCity = findViewById(R.id.event_detail_city);
        editNeighborhood = findViewById(R.id.event_detail_neighbor);
        editAddress = findViewById(R.id.event_detail_address);

        btnUpdate = findViewById(R.id.event_detail_btn_update);
        btnRemove = findViewById(R.id.event_detail_btn_remove);

        // Extras
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            event = new Event(
                    extra.getString("id"),
                    extra.getString("groupId"),
                    extra.getString("name"),
                    extra.getString("description"),
                    extra.getString("date"),
                    extra.getString("time"),
                    extra.getString("address"),
                    extra.getString("neighborhood"),
                    extra.getString("city"),
                    extra.getString("state")
            );
        }

        editName.setText(event.getName());
        txtDescription.setText(event.getDescription());
        txtDate.setText(event.getOccurrenceDate());
        txtTime.setText(event.getOccurrenceTime());
        editAddress.setText(event.getAddress());
        editCity.setText(event.getCity());
        editState.setText(event.getState());
        editNeighborhood.setText(event.getNeighborhood());

        // Listeners
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> fields = new HashMap<String, Object>();

                fields.put("Name", editName.getText().toString());
                fields.put("description__c", txtDescription.getText().toString());
                fields.put("event_date__c", txtDate.getText().toString());
                fields.put("event_time__c", txtTime.getText().toString());
                fields.put("event_city__c", editCity.getText().toString());
                fields.put("event_neighborhood__c", editNeighborhood.getText().toString());
                fields.put("event_address__c", editAddress.getText().toString());
                fields.put("state__c", editState.getText().toString());

                saveEvent(event.getId(), fields);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEvent(event);
            }
        });
    }

    private void saveEvent(String id, final Map<String, Object> fields)  {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForUpdate(
                    getString(R.string.api_version),
                    "event__c", id, fields
            );
        } catch (Exception e) {
            Log.d("==> saveEvent: ", e.getMessage());
            return;
        }

        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {

                // Consume before going back to main thread
                // Not required if you don't do main (UI) thread tasks here
                result.consumeQuietly();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Network component doesn’t report app layer status.
                        // Use the Mobile SDK RestResponse.isSuccess() method to check
                        // whether the REST request itself succeeded.
                        if (result.isSuccess()) {
                            try {
                                String message = String.format("Dados do evento \"%s\" atualizados com sucesso!", fields.get("Name"));

                                Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                EventDetailActivity.this.finish();
                            } catch (Exception e) {
                                Toast.makeText(EventDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("==> sendAsync: ", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EventDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("==> onError: ", e.getMessage());
            }
        });
    }

    private void removeEvent(final Event event) {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForDelete(
                    getString(R.string.api_version),
                    "event__c",
                    event.getId()
            );
        } catch (Exception e) {
            Log.d("==> deleteUEvent: ", e.getMessage());
            return;
        }

        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {

                // Consume before going back to main thread
                // Not required if you don't do main (UI) thread tasks here
                result.consumeQuietly();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Network component doesn’t report app layer status.
                        // Use the Mobile SDK RestResponse.isSuccess() method to check
                        // whether the REST request itself succeeded.
                        if (result.isSuccess()) {
                            try {
                                String message = String.format("Evento \"%s\" removido com sucesso!", event.getName());
                                Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                                EventDetailActivity.this.finish();
                            } catch (Exception e) {
                                Toast.makeText(EventDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("==> sendAsync: ", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EventDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("==> onError: ", e.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        // Hide everything until we are logged in
        findViewById(R.id.event_detail_constraint).setVisibility(View.INVISIBLE);

        super.onResume();
    }

    @Override
    public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

        // Show everything
        findViewById(R.id.event_detail_constraint).setVisibility(View.VISIBLE);
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
