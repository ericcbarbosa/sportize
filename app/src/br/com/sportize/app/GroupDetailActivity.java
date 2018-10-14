package br.com.sportize.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import br.com.sportize.app.model.Group;

public class GroupDetailActivity extends SalesforceActivity implements AppCompatCallback {

    private RestClient client;
    private Toolbar toolbar;
    private AppCompatDelegate delegate;

    // Extras: dados do grupo
    private String groupId;
    private String groupName = "";
    private String groupDescription = "";

    // View
    TextView name;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        name = findViewById(R.id.group_detail_out_name);
        description = findViewById(R.id.group_detail_out_description);

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            groupId = extra.getString("id");
            groupName = extra.getString("name");
            groupDescription = extra.getString("description");
        }

        name.setText(groupName);
        description.setText(groupDescription);
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        //let's leave this empty, for now
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        // let's leave this empty, for now
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
