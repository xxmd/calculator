package io.github.xxmd.calculator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.xxmd.calculator.adapter.ItemAdapter;
import io.github.xxmd.calculator.entity.GridSpacingItemDecoration;
import io.github.xxmd.calculator.entity.Item;

public class StandardCalculator extends FrameLayout {
    private List<Item> itemList = new ArrayList<>();
    private View rootView;
    private TextView tvBuffer;
    private TextView tvResult;
    private RecyclerView recyclerView;
    private int spanCount = 4;
    private ItemAdapter itemAdapter;


    public StandardCalculator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initData();
        initView();
        bindEvent();
    }

    private void bindEvent() {
        itemAdapter.setOnItemClick(this::handleItemClick);
    }

    private void handleItemClick(Item item) {

    }

    private void initData() {
        initItemList();
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.standard_calculator, this);
        tvBuffer = rootView.findViewById(R.id.tv_buffer);
        tvResult = rootView.findViewById(R.id.tv_result);
        recyclerView = rootView.findViewById(R.id.recycler);
    }


    private void initView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, (int) spacing, false));
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
    }

    private void initItemList() {
        itemList.add(new Item(R.drawable.function_mod, "%", (pre, post) -> pre % post));
        itemList.add(new Item(R.drawable.instruction_ce, (buffer, result) -> {
            result = "";
        }));
        itemList.add(new Item(R.drawable.instruction_c, (buffer, result) -> {
            buffer = new ArrayList<>();
            result = "";
        }));
        itemList.add(new Item(R.drawable.instruction_delete, (buffer, result) -> {
            if (result.length() > 0) {
                result = result.substring(0, result.length() - 1);
            } else {
                result = "0";
            }
        }));

        itemList.add(new Item(R.drawable.function_reciprocal, "1/(%s)", input -> 1 / input));
        itemList.add(new Item(R.drawable.function_square, "sqr(%s)", input -> input * input));
        itemList.add(new Item(R.drawable.function_sqrt, "√(%s)", input -> Math.sqrt(input)));
        itemList.add(new Item(R.drawable.function_divide, "÷", (pre, post) -> pre / post));

        itemList.add(new Item(R.drawable.symbol_7, 7));
        itemList.add(new Item(R.drawable.symbol_8, 8));
        itemList.add(new Item(R.drawable.symbol_9, 9));
        itemList.add(new Item(R.drawable.function_multiply, "×", (pre, post) -> pre * post));

        itemList.add(new Item(R.drawable.symbol_4, 4));
        itemList.add(new Item(R.drawable.symbol_5, 5));
        itemList.add(new Item(R.drawable.symbol_6, 6));
        itemList.add(new Item(R.drawable.function_minus, "-", (pre, post) -> pre - post));

        itemList.add(new Item(R.drawable.symbol_1, 1));
        itemList.add(new Item(R.drawable.symbol_2, 2));
        itemList.add(new Item(R.drawable.symbol_3, 3));
        itemList.add(new Item(R.drawable.function_minus, "+", (pre, post) -> pre + post));

        itemList.add(new Item(R.drawable.function_invert, input -> input * -1));
        itemList.add(new Item(R.drawable.symbol_0, 0));
        itemList.add(new Item(R.drawable.symbol__dot, "."));
        Item itemEqual = new Item(R.drawable.instruction_equal, "=", (buffer, result) -> {
        });
        itemEqual.backgroundColor = Color.parseColor("#196aa7");
        itemEqual.labelColor = Color.WHITE;
        itemList.add(itemEqual);
    }
}
