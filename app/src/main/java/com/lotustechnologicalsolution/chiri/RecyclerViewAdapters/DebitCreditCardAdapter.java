package com.lotustechnologicalsolution.chiri.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ModelClasses.CardModel;
import com.lotustechnologicalsolution.databinding.ItemDebitCreditCardBinding;

import java.util.ArrayList;

public class DebitCreditCardAdapter extends RecyclerView.Adapter<DebitCreditCardAdapter.MyViewHolder> {

    private ArrayList<CardModel> objects;
    private CardItemSelectListener listener;

    public DebitCreditCardAdapter(ArrayList<CardModel> objects, CardItemSelectListener listener) {
        this.objects = objects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDebitCreditCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_debit_credit_card, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardModel object = objects.get(position);
        holder.setData(object);
    }

    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0;
    }

    public interface CardItemSelectListener {
        void onCardSelect(int itemPosition, Object object);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemDebitCreditCardBinding binding;
        private Context context;

        public MyViewHolder(@NonNull ItemDebitCreditCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = this.binding.getRoot().getContext();

            if (listener == null)
                listener = (CardItemSelectListener) context;
        }

        @SuppressLint("SetTextI18n")
        public void setData(CardModel cardModel) {

            binding.rootLayout.setOnClickListener(v -> listener.onCardSelect(getLayoutPosition(), cardModel));

            binding.tvCardNumber.setText(cardModel.getCardNumber());
            binding.tvCardYear.setText(cardModel.getExpiryMonth() + "/" + cardModel.getExpiryYear());

            if (cardModel.isSelected())
                binding.ivCardSelection.setImageDrawable(context.getDrawable(R.drawable.ic_selected_card));
            else
                binding.ivCardSelection.setImageDrawable(context.getDrawable(R.drawable.ic_unselected_card));

            binding.executePendingBindings();
        }
    }
}
