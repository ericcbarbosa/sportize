package br.com.sportize.app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.sportize.app.MyLongClickListener;
import br.com.sportize.app.R;

public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener {
    public TextView name, description;
    public Button btnRemover;

    MyLongClickListener longClickListener;

    public GroupViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.group_out_name);
        description = (TextView) itemView.findViewById(R.id.group_out_description);
        btnRemover = itemView.findViewById(R.id.group_btn_remove);

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