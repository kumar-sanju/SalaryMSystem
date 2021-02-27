package com.sanju.salarymsystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by jmprathab on 23/10/15.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    ArrayList<Data> data = new ArrayList<>();


    public MyAdapter(ArrayList<Data> data) {
        this.data = data;
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        holder.date.setText(data.get(position).getDateForView());
        holder.inTime.setText(data.get(position).getInTimeForView());
        holder.outTime.setText(data.get(position).getOutTimeForView());
        holder.pay.setText("Pay : " + String.valueOf(data.get(position).getTotalPay()));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView date, inTime, outTime, pay;
        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            inTime = (TextView) itemView.findViewById(R.id.in);
            outTime = (TextView) itemView.findViewById(R.id.out);
            pay = (TextView) itemView.findViewById(R.id.pay);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, Information.class);
            intent.putExtra("ID", data.get(getPosition()).getId());
            context.startActivity(intent);
        }
    }
}
