package io.github.xxmd.calculator.adapter;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

import io.github.xxmd.calculator.R;
import io.github.xxmd.calculator.entity.Item;
import io.github.xxmd.calculator.entity.ItemType;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_button);
        }
    }

    private Consumer<Item> onItemClick;

    public void setOnItemClick(Consumer<Item> onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.keyboard_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Item item = itemList.get(position);
        viewHolder.imageView.setImageResource(item.icon);
        if (item.labelColor != 0) {
            viewHolder.imageView.setColorFilter(item.labelColor);
        }
        if (item.backgroundColor != 0) {
            GradientDrawable backgroundDrawable = (GradientDrawable) viewHolder.imageView.getBackground();
            backgroundDrawable.setColor(item.backgroundColor);
            viewHolder.imageView.setBackground(backgroundDrawable);
        }
        viewHolder.itemView.setOnClickListener(view -> {
            if (onItemClick != null) {
                onItemClick.accept(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
