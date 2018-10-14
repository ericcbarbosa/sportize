package br.com.sportize.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.sportize.app.R;
import br.com.sportize.app.model.Group;

public class GroupAdapater extends ArrayAdapter<Group> {
    private Context context;
    private ArrayList<Group> groupsList;

    public GroupAdapater(Context context, ArrayList<Group> groupsList) {
        super(context, 0, groupsList);
        this.context = context;
        this.groupsList = groupsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está vazia
        if (groupsList != null) {
            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.group_item, parent, false);

            // recupera elemento para exibição
            TextView groupName = view.findViewById(R.id.group_out_name);
            TextView groupDescription = view.findViewById(R.id.group_out_description);

            Group group = groupsList.get(position);

            Log.d("group: ", group.toString());

            groupName.setText(group.getName());
            groupDescription.setText(group.getDescription());
        }

        return view;
    }

}