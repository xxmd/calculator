package io.github.xxmd.calculator.adapter;

import android.graphics.Color;
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
import io.github.xxmd.calculator.entity.KeyboardItem;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final int equalBtnBgColor;
    private final int imageTint;
    private List<KeyboardItem> itemList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_button);
        }
    }

    private Consumer<KeyboardItem> onItemClick;

    public void setOnItemClick(Consumer<KeyboardItem> onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ItemAdapter(List<KeyboardItem> itemList) {
        this.itemList = itemList;
        imageTint = Color.parseColor("#1b1b1b");
        equalBtnBgColor = Color.parseColor("#005a9e");
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
        KeyboardItem item = itemList.get(position);
        viewHolder.imageView.setImageResource(item.icon);
        boolean isEqual = item.equals(KeyboardItem.EQUAL);
        viewHolder.imageView.setColorFilter(isEqual ? Color.WHITE : imageTint);
        GradientDrawable backgroundDrawable = (GradientDrawable) viewHolder.itemView.getBackground();
        backgroundDrawable.setColor(isEqual ? equalBtnBgColor : Color.WHITE);
        viewHolder.itemView.setBackground(backgroundDrawable);
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
