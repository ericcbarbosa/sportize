package br.com.sportize.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import br.com.sportize.app.model.Group;

public class GroupDetailActivity extends SalesforceActivity implements AppCompatCallback {

    private RestClient client;
    private AppCompatDelegate delegate;

    // Extras: dados do grupo
    private Group group;

    // View
    TextView name;
    TextView description;
    Button btnUpdate;
    Button btnRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Delegate
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_group_detail);

        // Configura toolbar
        Toolbar toolbar = findViewById(R.id.group_detail_toolbar);

        toolbar.setTitle("Detalhes do Grupo");
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
        name = findViewById(R.id.group_detail_edit_name);
        description = findViewById(R.id.group_detail_edit_description);
        btnUpdate = findViewById(R.id.group_detail_edit_update);
        btnRemove = findViewById(R.id.group_detail_edit_remove);

        // Extras
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            group = new Group(
                    extra.getString("id"),
                    extra.getString("name"),
                    extra.getString("description")
            );
        }

        name.setText(group.getName());
        description.setText(group.getDescription());

        // Listeners
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> fields = new HashMap<String, Object>();

                fields.put("Name", name.getText().toString());
                fields.put("group_description__c", description.getText().toString());

                saveGroup(group.getId(), fields);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGroup(group);
            }
        });
    }

    private void saveGroup(String id, final Map<String, Object> fields)  {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForUpdate(
                    getString(R.string.api_version),
                    "group__c", id, fields
            );
        } catch (Exception e) {
            Log.d("==> saveGroup: ", e.getMessage());
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
                                String message = String.format("Dados do grupo \"%s\" atualizados com sucesso!", fields.get("Name"));

                                Toast.makeText(GroupDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                GroupDetailActivity.this.finish();
                            } catch (Exception e) {
                                Toast.makeText(GroupDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("==> sendAsync: ", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(GroupDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("==> onError: ", e.getMessage());
            }
        });
    }

    private void removeGroup(final Group group) {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForDelete(
                    getString(R.string.api_version),
                    "group__c",
                    group.getId()
            );
        } catch (Exception e) {
            Log.d("==> deleteGroup: ", e.getMessage());
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
                                String message = String.format("Grupo \"%s\" removido com sucesso!", group.getName());
                                Toast.makeText(GroupDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                                GroupDetailActivity.this.finish();
                            } catch (Exception e) {
                                Toast.makeText(GroupDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("==> sendAsync: ", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(GroupDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("==> onError: ", e.getMessage());
            }
        });
    }


    @Override
    public void onResume() {
        // Hide everything until we are logged in
        findViewById(R.id.group_detail_constraint).setVisibility(View.INVISIBLE);

        super.onResume();
    }

    @Override
    public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

        // Show everything
        findViewById(R.id.group_detail_constraint).setVisibility(View.VISIBLE);
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
