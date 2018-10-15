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
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import java.util.HashMap;
import java.util.Map;

import br.com.sportize.app.model.Group;
import br.com.sportize.app.model.User;

public class UserDetailActivity extends SalesforceActivity implements AppCompatCallback {

    private RestClient client;
    private AppCompatDelegate delegate;

    // Extras: dados do grupo
    private User user;

    // View
    EditText editName;
    EditText editEmail;
    EditText editPassword;
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
        delegate.setContentView(R.layout.activity_user_detail);

        // Configura toolbar
        Toolbar toolbar = findViewById(R.id.user_detail_toolbar);

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
        editName = findViewById(R.id.user_detail_name);
        editEmail = findViewById(R.id.user_detail_email);
        editPassword = findViewById(R.id.user_detail_password);
        editState = findViewById(R.id.user_detail_state);
        editCity = findViewById(R.id.user_detail_city);
        editNeighborhood = findViewById(R.id.user_detail_neighbor);
        editAddress = findViewById(R.id.user_detail_address);

        btnUpdate = findViewById(R.id.user_detail_btn_update);
        btnRemove = findViewById(R.id.user_detail_btn_remove);

        // Extras
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            user = new User(
                    extra.getString("id"),
                    extra.getString("name"),
                    extra.getString("email"),
                    extra.getString("password"),
                    extra.getString("address"),
                    extra.getString("neighborhood"),
                    extra.getString("city"),
                    extra.getString("state")
            );
        }

        editName.setText(user.getName());
        editEmail.setText(user.getEmail());
        editPassword.setText(user.getPassword());
        editState.setText(user.getState());
        editCity.setText(user.getCity());
        editNeighborhood.setText(user.getNeighborhood());
        editAddress.setText(user.getAddress());

        // Listeners
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> fields = new HashMap<String, Object>();

                fields.put("Name", editName.getText().toString());
                fields.put("email__c", editEmail.getText().toString());
                fields.put("password__c", editPassword.getText().toString());
                fields.put("state__c", editState.getText().toString());
                fields.put("city__c", editCity.getText().toString());
                fields.put("neighborhood__c", editNeighborhood.getText().toString());
                fields.put("address__c", editAddress.getText().toString());

                saveUser(user.getId(), fields);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUser(user);
            }
        });
    }

    private void saveUser(String id, final Map<String, Object> fields)  {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForUpdate(
                    getString(R.string.api_version),
                    "player__c", id, fields
            );
        } catch (Exception e) {
            Log.d("==> saveUser: ", e.getMessage());
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
                                String message = String.format("Dados do jogador \"%s\" atualizados com sucesso!", fields.get("Name"));

                                Toast.makeText(UserDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                UserDetailActivity.this.finish();
                            } catch (Exception e) {
                                Toast.makeText(UserDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("==> sendAsync: ", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(UserDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("==> onError: ", e.getMessage());
            }
        });
    }

    private void removeUser(final User user) {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForDelete(
                    getString(R.string.api_version),
                    "player__c",
                    user.getId()
            );
        } catch (Exception e) {
            Log.d("==> deleteUser: ", e.getMessage());
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
                                String message = String.format("Grupo \"%s\" removido com sucesso!", user.getName());
                                Toast.makeText(UserDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                                UserDetailActivity.this.finish();
                            } catch (Exception e) {
                                Toast.makeText(UserDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("==> sendAsync: ", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(UserDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("==> onError: ", e.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        // Hide everything until we are logged in
        findViewById(R.id.user_detail_constraint).setVisibility(View.INVISIBLE);

        super.onResume();
    }

    @Override
    public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

        // Show everything
        findViewById(R.id.user_detail_constraint).setVisibility(View.VISIBLE);
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
