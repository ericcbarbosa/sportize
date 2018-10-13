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

            groupName.setText(group.getName());
            groupDescription.setText(group.getDescription());
        }

        return view;
    }

//    @NonNull
//    @Override
//    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
//        GroupViewHolder vh = new GroupViewHolder(v);
//
//        return vh;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final GroupViewHolder holder, int position) {
//        Group group = groupsList.get(position);
//
//        holder.name.setText( group.getName() );
//        holder.description.setText( group.getDescription() );
//
//        holder.btnRemover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int pos = holder.getAdapterPosition();
//
//                if (pos != RecyclerView.NO_POSITION) {
//                    deleteItem(pos);
//                }
//            }
//        });
//
//        holder.setLongClickListener(new MyLongClickListener() {
//            @Override
//            public void onLongClick(int pos) {
//                selectedPos = pos;
//            }
//        });
//    }

//    @Override
//    public int getItemCount() {
//        return groupsList != null? groupsList.size() : 0;
//    }
//
//    public void deleteItem(int position) {
//        Group group = groupsList.get(position);
//
//        if(groupsList.remove(group)) {
//            Toast.makeText(context,"O grupo " + group.getName() + " foi removido com sucesso", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context,"Não foi possível remover o grupo" + group.getName(), Toast.LENGTH_SHORT).show();
//        }
//
//        this.notifyItemRemoved(position);
//    }

}