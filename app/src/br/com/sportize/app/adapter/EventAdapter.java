package br.com.sportize.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.sportize.app.R;
import br.com.sportize.app.model.Event;
import br.com.sportize.app.model.User;

public class EventAdapter extends ArrayAdapter<Event>  {
    private Context context;
    private ArrayList<Event> eventList;

    public EventAdapter(Context context, ArrayList<Event> eventList) {
        super(context, 0, eventList);
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está vazia
        if (eventList != null) {
            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.event_item, parent, false);

            // recupera elemento para exibição
            TextView txtName = view.findViewById(R.id.event_out_name);
            TextView txtDescription = view.findViewById(R.id.event_out_description);

            Event event = eventList.get(position);

            txtName.setText(event.getName());
            txtDescription.setText(event.getDescription());
        }

        return view;
    }
}
