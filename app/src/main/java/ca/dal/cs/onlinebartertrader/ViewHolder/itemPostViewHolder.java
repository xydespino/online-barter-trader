package ca.dal.cs.onlinebartertrader.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ca.dal.cs.onlinebartertrader.Interface.ItemClickListener;
import ca.dal.cs.onlinebartertrader.R;

public class itemPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtItemName;
    public TextView txtDescription;
    public TextView sellerUsername;
    public TextView quantity;
    public TextView distance;
    public ImageView imageView;
    public RatingBar QRating;
    public ItemClickListener listener;
    public LinearLayout root;


    public itemPostViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.postImage);
        root = itemView.findViewById(R.id.list_root);
        txtItemName = (TextView) itemView.findViewById(R.id.item_name);
        txtDescription = (TextView) itemView.findViewById(R.id.item_description);
        distance = (TextView) itemView.findViewById(R.id.item_distance);
    }
    public void setTxtItemName(String string) {
        txtItemName.setText(string);
    }
    public void setTxtDesc(String string) {
        txtDescription.setText(string);
    }
    public void setDistance (int dist) { distance.setText(dist + "km away from you"); }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
