package br.com.sportize.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.sportize.app.MyLongClickListener;
import br.com.sportize.app.R;
import br.com.sportize.app.fragment.UserFragment;
import br.com.sportize.app.model.Group;
import br.com.sportize.app.model.User;
import br.com.sportize.app.viewholder.UserViewHolder;

public class UserAdapter extends ArrayAdapter<User>  {
    private Context context;
    private ArrayList<User> userList;

    public UserAdapter(Context context, ArrayList<User> userList) {
        super(context, 0, userList);
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está vazia
        if (userList != null) {
            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.user_item, parent, false);

            // recupera elemento para exibição
            TextView txtName = view.findViewById(R.id.user_out_name);
            TextView txtEmail = view.findViewById(R.id.user_out_email);
//            TextView txtState = view.findViewById(R.id.user_out_state);
//            TextView txtCity = view.findViewById(R.id.user_out_city);
//            TextView txtNeighboor = view.findViewById(R.id.user_out_neighboor);
//            TextView txtAddress = view.findViewById(R.id.user_out_address);
//            TextView txtPass = view.findViewById(R.id.user_out_password);

            User user = userList.get(position);

            txtName.setText(user.getName());
            txtEmail.setText(user.getEmail());
//            txtState.setText(user.getState());
//            txtCity.setText(user.getCity());
//            txtNeighboor.setText(user.getNeighborhood());
//            txtAddress.setText(user.getAddress());
//            txtPass.setText(user.getPassword());
        }

        return view;
    }
}
