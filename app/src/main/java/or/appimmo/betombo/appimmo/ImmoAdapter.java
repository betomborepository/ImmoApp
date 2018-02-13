package or.appimmo.betombo.appimmo;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.*;

/**
 * Created by hp on 28/01/2018.
 */

public class ImmoAdapter extends RecyclerView.Adapter<ImmoAdapter.ImmoViewHolder>
{

    private java.util.List<ImmoObject> ImmoList ;
    public ImmoAdapter(java.util.List <ImmoObject>  immodata)
    {
        ImmoList = immodata;
    }

    @Override
    public ImmoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater inflator = LayoutInflater.from(viewGroup.getContext());
        View view = inflator.inflate( R.layout.list_item_template, viewGroup, false);
        ImmoViewHolder viewHolder = new ImmoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImmoViewHolder immoViewHolder, int i) {
        immoViewHolder.updateView(ImmoList.get(i));
    }

    @Override
    public int getItemCount() {
        return ImmoList.size();
    }

    class ImmoViewHolder extends RecyclerView.ViewHolder
    {
        public  ImmoViewHolder(View itemView)
        {
            super(itemView);
        }



        public void updateView (ImmoObject object)
        {
            TextView v = (TextView) itemView.findViewById(R.id.immo_name);
            v.setText(object.getName());



            v = (TextView) itemView.findViewById(R.id.immo_contact);
            v.setText(object.getContact());


            RatingBar ratingBar = itemView.findViewById(R.id.immo_rating);
            ratingBar.setRating(object.getRating());


            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + object.getImage());
            ImageView img = (ImageView) itemView.findViewById(R.id.immo_image);

            TextView id = itemView.findViewById(R.id.immo_id_receiver);
            id.setText(object.getID());

            Glide.with(v.getContext()).using(new FirebaseImageLoader()).load(ref).into(img);
        }
    }
}
