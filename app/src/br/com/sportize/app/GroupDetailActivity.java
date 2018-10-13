package br.com.sportize.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class GroupDetailActivity extends AppCompatActivity {

    // Extras: dados do grupo
    private Toolbar toolbar;
    private String groupName;
    private String groupDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        toolbar = findViewById(R.id.tb_group_detail);

        TextView name = findViewById(R.id.group_detail_out_name);
        TextView description = findViewById(R.id.group_detail_out_description);

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            groupName = extra.getString("name");
            groupDescription = extra.getString("description");
        }

        // Configura toolbar
        toolbar.setTitle("Detalhes do Grupo");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        name.setText(groupName);
        description.setText(groupDescription);
    }
}
