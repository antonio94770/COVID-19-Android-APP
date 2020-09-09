package com.guastafeste.covid_19;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Gestione dell'adapter della lista di province, agisce da bridge tra l'arraylist e la recyclerView .
 */
public class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.ProvinceViewHolder> {

    private ArrayList<ProvinceItem> mProvinceList;

    /**
     * Gestione di un singolo viewholder della provincia.
     */
    public static class ProvinceViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ProvinceViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewProvincia);
            mTextView1 = itemView.findViewById(R.id.textViewNomeProvincia);
            mTextView2 = itemView.findViewById(R.id.textViewDescrizioneProvincia);

        }
    }

    /**
     * Funzione per settare passare la lista di province alla ProvinceActivity.
     * @param provinceList
     */
    public ProvinceAdapter(ArrayList<ProvinceItem> provinceList)
    {
        mProvinceList = provinceList;
    }

    /**
     * Richiamo la classe ProvinceViewHolder e passo la view.
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ProvinceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.province_item, parent, false);
        ProvinceViewHolder provinceViewHolder = new ProvinceViewHolder(v);
        return provinceViewHolder;
    }

    /**
     * Setto i parametri in base alla posizione passata come parametro.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ProvinceViewHolder holder, int position) {
        ProvinceItem currentItem = mProvinceList.get(position);

        holder.mImageView.setImageResource(currentItem.getrImage());
        holder.mTextView1.setText(currentItem.getrNomeProvincia());
        holder.mTextView2.setText(currentItem.getrInfo());
    }


    /**
     * Ritorno la dimensione della lista di elementi delle province.
     * @return
     */
    @Override
    public int getItemCount() {
        return mProvinceList.size();
    }
}
