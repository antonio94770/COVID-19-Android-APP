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
 * Gestione dell'adapter della lista di regioni.
 */
public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.RegionViewHolder> {

    private ArrayList<RegionItem> mRegionList;
    private OnItemClickListener mListener;

    /**
     * Ottengo la posizione quando clicco su un elemento della recyclerView.
     */
    public interface OnItemClickListener
    {
        void OnItemClick(int position);
    }

    /**
     * Setto il listener
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    /**
     * Gestione di un singolo viewholder delle regioni.
     */
    public static class RegionViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public RegionViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewRegione);
            mTextView1 = itemView.findViewById(R.id.textViewNomeRegione);
            mTextView2 = itemView.findViewById(R.id.textViewDescrizioneRegione);

            //Gestisco l'OnCLickListener per ogni elemento.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            listener.OnItemClick(pos);
                        }
                    }
                }
            });

        }
    }

    /**
     * Funzione per settare passare la lista di regioni al fragment RegionFragment.
     * @param regionList
     */
    public RegionAdapter(ArrayList<RegionItem> regionList)
    {
        mRegionList = regionList;
    }

    /**
     * Richiamo la classe RegionViewHolder e passo la view.
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RegionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.region_item, parent, false);
        RegionViewHolder regionViewHolder = new RegionViewHolder(v, mListener);
        return regionViewHolder;
    }

    /**
     * Setto i parametri in base alla posizione passata come parametro.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RegionViewHolder holder, int position) {
        RegionItem currentItem = mRegionList.get(position);

        holder.mImageView.setImageResource(currentItem.getrImage());
        holder.mTextView1.setText(currentItem.getrTitle());
        holder.mTextView2.setText(currentItem.getrDescription());
    }


    /**
     * Ritorno la dimensione della lista di elementi delle regioni.
     * @return
     */
    @Override
    public int getItemCount() {
        return mRegionList.size();
    }
}
