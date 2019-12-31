package com.example.moments.Data;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moments.Model.Moment;
import com.example.moments.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MomentRecyclerAdapter extends RecyclerView.Adapter<MomentRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Moment> MomentList;

    public MomentRecyclerAdapter(Context context, List<Moment> momentList) {
        this.context = context;
        MomentList = momentList;
    }

    // ====================== ViewHolder ======================
    @NonNull
    @Override
    public MomentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);


        return new ViewHolder(view, context);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Moment moment = MomentList.get(position);

        String imageUrl = null;
        String profImageUrl = null;

        holder.title.setText(moment.getTitle());
        holder.desc.setText(moment.getDescription());
        holder.posterName.setText(moment.getUserName());

        // Access to "MUsers by using the userId >>>????

        DateFormat dateFormat = DateFormat.getDateInstance();

//        //date ago
//        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(moment
//                .getTimestamp()
//                .getSeconds() * 1000);
        String formattedDate = dateFormat.format(new Date(new Date(Long.valueOf(moment.getTimestamp())).getTime()));
        holder.timestamp.setText(formattedDate);

        // =========== post Image ============
        if(moment.getImage() != "null"){
            imageUrl = moment.getImage();
            //Use Picasso library to load image
            Picasso.with(context)
                    .load(imageUrl)
                    .into(holder.image);
        }

        // =========== profile picture ============
        profImageUrl = moment.getProfileImgUrl();
        Picasso.with(context)
                .load(profImageUrl).
                into(holder.profileImg);

    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public int getItemCount() {
        return MomentList.size();
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++
    // =============== cardView with Image ================
    // =========== Setting app the card Items ===========
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImg;
        public TextView  posterName;
        public TextView  timestamp;
        //===============================
        public ImageView image;
        public TextView  title;
        public TextView  desc;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;

            profileImg = (ImageView) itemView.findViewById(R.id.profileImgID);
            posterName = (TextView)  itemView.findViewById(R.id.fullNameID);
            timestamp  = (TextView)  itemView.findViewById(R.id.timestampList);
            //===============================
            image      = (ImageView) itemView.findViewById(R.id.crdImageViewID);
            title      = (TextView)  itemView.findViewById(R.id.cardPostTitleID);
            desc       = (TextView)  itemView.findViewById(R.id.cardPostDescID);



        }

    }
    // =============== cardView without Image ================

}
