package io.github.xxmd.calculator;

import android.content.Context;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.xxmd.calculator.adapter.ItemAdapter;
import io.github.xxmd.calculator.entity.CalculatorState;
import io.github.xxmd.calculator.entity.GridSpacingItemDecoration;
import io.github.xxmd.calculator.entity.KeyboardItem;
import io.github.xxmd.calculator.util.ComputeUtil;

public class StandardCalculator extends FrameLayout {
    private List<KeyboardItem> itemList = new ArrayList<>();
    private TextView tvVisualExpression;
    private TextView tvResult;
    private RecyclerView recyclerView;
    private int spanCount = 4;
    private ItemAdapter itemAdapter;

    private CalculatorState calculatorState = new CalculatorState();

    public void onCalculatorStateChange() {
        tvVisualExpression.setText(calculatorState.visualExpression.stream().collect(Collectors.joining()));
        tvResult.setText(ComputeUtil.format(calculatorState.curResult));
    }


    public StandardCalculator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initData();
        initView();
        bindEvent();
    }

    private void initView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, (int) spacing, false));
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
        setResult("0");
    }

    private void setResult(String result) {
    }

    private String getResult() {
        String formatResult = tvResult.getText().toString();
        formatResult = formatResult.replaceAll(",", "");
        return formatResult;
    }

    private void initData() {
        initItemList();

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.standard_calculator, this);
        tvVisualExpression = rootView.findViewById(R.id.tv_visual_expression);
        tvResult = rootView.findViewById(R.id.tv_result);
        recyclerView = rootView.findViewById(R.id.recycler);
    }

    private void initItemList() {
        itemList.add(KeyboardItem.PERCENT);
        itemList.add(KeyboardItem.CE);
        itemList.add(KeyboardItem.C);
        itemList.add(KeyboardItem.CLEAN);
        itemList.add(KeyboardItem.RECIPROCAL);
        itemList.add(KeyboardItem.POW2);
        itemList.add(KeyboardItem.SQRT);
        itemList.add(KeyboardItem.DIVIDE);
        itemList.add(KeyboardItem.SEVEN);
        itemList.add(KeyboardItem.EIGHT);
        itemList.add(KeyboardItem.NINE);
        itemList.add(KeyboardItem.MULTIPLE);
        itemList.add(KeyboardItem.FOUR);
        itemList.add(KeyboardItem.FIVE);
        itemList.add(KeyboardItem.SIX);
        itemList.add(KeyboardItem.MINUS);
        itemList.add(KeyboardItem.ONE);
        itemList.add(KeyboardItem.TWO);
        itemList.add(KeyboardItem.THREE);
        itemList.add(KeyboardItem.ADD);
        itemList.add(KeyboardItem.INVERT);
        itemList.add(KeyboardItem.ZERO);
        itemList.add(KeyboardItem.DOT);
        itemList.add(KeyboardItem.EQUAL);
    }

    private void bindEvent() {
        itemAdapter.setOnItemClick(this::handleItemClick);
    }

    private void handleItemClick(KeyboardItem item) {
        item.modifyState.accept(calculatorState);
        onCalculatorStateChange();
    }
}
