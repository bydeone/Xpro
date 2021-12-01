package com.deone.xpro.adapters;

import static com.deone.xpro.tools.Constants.FORMAT_DATE;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.xpro.R;
import com.deone.xpro.models.Article;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyHolder>{

    private final Context context;
    private XClicklistener listener;
    private final List<Article> articleList;

    public ArticlesAdapter(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String cover = articleList.get(position).getaCover();
        String titre = articleList.get(position).getaTitre();
        String description = articleList.get(position).getaDescription();
        String vues = articleList.get(position).getAnVues();
        String date = articleList.get(position).getaDate();

        Picasso.with(context).load(cover).fit().centerCrop()
                .placeholder(R.drawable.ic_action_logo)
                .error(R.drawable.ic_action_logo_error)
                .into(holder.imArticleLogo);
        holder.tvTitre.setText(titre);
        holder.tvDescription.setText(description);
        holder.tvDetails.setText(context.getResources()
                .getString(Long.parseLong(vues) <= 1 ? R.string.item_details : R.string.item_details_pluriel, timestampToString(date), vues));

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public void setListener(XClicklistener listener) {
        this.listener = listener;
    }

    private String timestampToString(String date){
        Locale current = context.getResources().getConfiguration().locale;
        Calendar cal = Calendar.getInstance(current == Locale.FRANCE ? Locale.FRANCE : Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(date));
        return DateFormat.format(FORMAT_DATE, cal).toString();
    }

    public class MyHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            View.OnLongClickListener {

        ImageView imArticleLogo;
        TextView tvTitre;
        TextView tvDescription;
        TextView tvDetails;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imArticleLogo = itemView.findViewById(R.id.imArticleLogo);
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null){
                listener.onItemClick(v, position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null){
                listener.onLongItemClick(v, position);
            }
            return true;
        }
    }

}
