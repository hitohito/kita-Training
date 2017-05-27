package com.example.tsunehitokita.anbayasiroulette;

import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

//import RecyclerView.ViewHolder;

/**
 * Created by tsunehitokita on 2017/05/24.
 */

public class AnbayasiViewHolder extends RecyclerView.ViewHolder {

    View base;
    TextView textViewNumber;
    TextView textViewComment;


    public AnbayasiViewHolder(View v){
        super(v);
        this.base = v;
        this.textViewNumber = (TextView) v.findViewById(R.id.number);
        this.textViewComment = (TextView) v.findViewById(R.id.comment);
    }
}
