package net.nolanbecker.mileagelog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.nolanbecker.mileagelog.data.model.Mile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Mile> miles;
    private Context context;
    private PostItemListener itemListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView date;
        public TextView miles;
        PostItemListener itemListener;

        public ViewHolder(View itemView, PostItemListener itemListener) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            miles = (TextView) itemView.findViewById(R.id.miles);

            this.itemListener = itemListener;
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Mile mile = getMile(getAdapterPosition());
            this.itemListener.onPostClick(mile.getMiles(), mile.getDate(), context);
            return false;
        }
    }

    public Adapter(Context context, List<Mile> miles, PostItemListener itemListener) {
        this.miles = miles;
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.list, viewGroup, false);

        ViewHolder viewHolder= new ViewHolder(postView, this.itemListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Mile mile = miles.get(i);
        TextView date = viewHolder.date;
        TextView miles = viewHolder.miles;
        String newDate = mile.getDate();

        try {
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(mile.getDate());
            String tmpDate = new SimpleDateFormat("dd").format(day);
            String pattern = "";
            if (tmpDate.endsWith("1")) {
                pattern = "d'st'";
            } else if (tmpDate.endsWith("2")) {
                pattern = "d'nd'";
            } else if (tmpDate.endsWith("3")) {
                pattern = "d'rd'";
            } else {
                pattern = "d'th'";
            }
            if (!MainActivity.SORT.equals("month"))
                pattern = "MMMM " + pattern;
            else
                pattern = "EEE, " + pattern;
            newDate = new SimpleDateFormat(pattern).format(day);
        } catch (Exception e) {
            e.printStackTrace();
        }

        date.setText(newDate);
        miles.setText(String.valueOf(mile.getMiles()));

    }

    @Override
    public int getItemCount() {
        return miles.size();
    }

    public void updateMiles(List<Mile> miles) {
        this.miles = miles;
        notifyDataSetChanged();
    }

    private Mile getMile(int adapterPosition) {
        return miles.get(adapterPosition);
    }

    public interface PostItemListener {
        void onPostClick(int miles, String date, Context context);
    }

}