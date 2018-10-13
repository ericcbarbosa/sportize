package br.com.sportize.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.sportize.app.MyLongClickListener;
import br.com.sportize.app.R;
import br.com.sportize.app.fragment.UserFragment;
import br.com.sportize.app.model.User;
import br.com.sportize.app.viewholder.UserViewHolder;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{
    private Context context;
    private ArrayList<User> userList;
    int selectedPos;
    UserFragment fragment;

    public UserAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        UserViewHolder vh = new UserViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.name.setText( user.getName() );
        holder.email.setText( user.getName() );
        holder.address.setText( user.getAddress() );
        holder.neighbor.setText( user.getNeighborhood() );
        holder.city.setText( user.getCity() );
        holder.state.setText( user.getState() );

        holder.btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();

                if (pos != RecyclerView.NO_POSITION) {
                    deleteItem(pos);
                }
            }
        });

        holder.setLongClickListener(new MyLongClickListener() {
            @Override
            public void onLongClick(int pos) {
                selectedPos = pos;
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null? userList.size() : 0;
    }

    public void deleteItem(int position) {
        User user = userList.get(position);

        if(userList.remove(user)) {
            Toast.makeText(context,"O usuário " + user.getName() + " foi removido com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"Não foi possível remover o usuário" + user.getName(), Toast.LENGTH_SHORT).show();
        }

        this.notifyItemRemoved(position);
    }
}
