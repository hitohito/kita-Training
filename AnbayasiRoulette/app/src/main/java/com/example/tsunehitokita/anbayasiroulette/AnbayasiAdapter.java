package com.example.tsunehitokita.anbayasiroulette;

//import RecyclerView.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by tsunehitokita on 2017/05/24.
 */

public class AnbayasiAdapter extends RecyclerView.Adapter<AnbayasiViewHolder> {
    private ArrayList<AnbayasiData> rouletteDataSet;

    public AnbayasiAdapter(ArrayList<AnbayasiData> roulette) {

        this.rouletteDataSet = roulette;

    }

    //新しいViewを作成する
    //レイアウトマネージャーにより起動される
    @Override
    public AnbayasiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //parentはRecyclerView
        //public View inflate (int resource, ViewGroup root, boolean attachToRoot)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        return new AnbayasiViewHolder(view);
    }
    //Viewの内容を交換する(リサイクルする)
    //レイアウトマネージャーにより起動される
    @Override
    public void onBindViewHolder(final AnbayasiViewHolder holder, final int listPosition){

        holder.textViewNumber.setText(rouletteDataSet.get(listPosition).getNumber()+ "本");
        holder.textViewComment.setText(rouletteDataSet.get(listPosition).getComment());
        holder.base.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //vはCardView
                Toast.makeText(v.getContext(),"おまけ" + rouletteDataSet.get(listPosition).getAddition() + "本", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount(){
        return rouletteDataSet.size();
    }
}
