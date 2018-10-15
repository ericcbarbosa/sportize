package br.com.sportize.app.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.sportize.app.MyLongClickListener;
import br.com.sportize.app.R;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener {
    public TextView name, email, password, address, neighbor, city, state;
    public Button btnRemover;

    MyLongClickListener longClickListener;

    public UserViewHolder(View itemView) {
        super(itemView);
//        name = (TextView) itemView.findViewById(R.id.user_out_name);
//        email = (TextView) itemView.findViewById(R.id.user_out_email);
//        address = (TextView) itemView.findViewById(R.id.user_out_address);
//        neighbor = (TextView) itemView.findViewById(R.id.user_out_neighboor);
//        city = (TextView) itemView.findViewById(R.id.user_out_city);
//        state = (TextView) itemView.findViewById(R.id.user_out_state);
//        btnRemover = itemView.findViewById(R.id.user_btn_remove);

        itemView.setOnLongClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("AÇÕES");
        menu.add(0,0, getAdapterPosition(),"Adicionar");
    }

    public void setLongClickListener(MyLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public boolean onLongClick(View v) {
        this.longClickListener.onLongClick(getLayoutPosition());
        return false;
    }
}
