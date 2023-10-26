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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.xxmd.calculator.adapter.ItemAdapter;
import io.github.xxmd.calculator.entity.GridSpacingItemDecoration;
import io.github.xxmd.calculator.entity.Item;

public class StandardCalculator extends FrameLayout {
    private List<Item> itemList = new ArrayList<>();
    private View rootView;
    private TextView tvVisualExpression;
    private TextView tvResult;
    private RecyclerView recyclerView;
    private int spanCount = 4;
    private ItemAdapter itemAdapter;
    private List<String> visualExpression;
    private Double result;

    public void setVisualExpression(List<String> visualExpression) {
        this.visualExpression = visualExpression;
        tvVisualExpression.setText(visualExpression.stream().collect(Collectors.joining()));
    }

    private List<String> realExpression;


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
        switch (item.type) {
            case FUNCTION:
                executeFunction(item);
                break;
            case INSTRUCTION:
                executeInstruction(item);
                break;
            case BINARY_FUNCTION:
                executeBinaryFunction(item);
                break;
            case SYMBOL:
                tvResult.setText(tvResult.getText() + item.label);
                break;

        }
    }

    private void executeBinaryFunction(Item item) {
        if (containBinarySymbol()) {
            visualExpression.add(String.valueOf(result));
            String expression = visualExpression.stream().collect(Collectors.joining());
            Double result = computeExpression(expression);
            visualExpression.clear();
            visualExpression.add(String.valueOf(result));
            visualExpression.add(item.label);
            realExpression.clear();
            realExpression.add(String.valueOf(result));
        }
        visualExpression.add(item.label);
        setVisualExpression(visualExpression);
        realExpression.add(item.label);
    }

    private Double computeExpression(String expression) {
        org.mozilla.javascript.Context context = org.mozilla.javascript.Context.enter(); //
        context.setOptimizationLevel(-1); // this is required[2]
        Scriptable scope = context.initStandardObjects();
        Object result = context.evaluateString(scope, expression, "<cmd>", 1, null);
        return Double.parseDouble(result.toString());
    }

    private boolean containBinarySymbol() {
        for (String str : realExpression) {
            if (!NumberUtils.isCreatable(str)) {
                return true;
            }
        }
        return false;
    }

    private void executeInstruction(Item item) {
        item.instruction.accept(visualExpression, realExpression, tvResult);
        setVisualExpression(visualExpression);
    }

    private void executeFunction(Item item) {
        Double argument = Double.valueOf(tvResult.getText().toString());
        Double result = item.function.apply(argument);
        String last = visualExpression.get(visualExpression.size() - 1);
        if (StringUtils.isEmpty(item.label)) {
            last = String.valueOf(result);
        } else {
            last = String.format(item.label, result);
        }
        visualExpression.set(visualExpression.size() - 1, last);
        setVisualExpression(visualExpression);
        realExpression.set(realExpression.size() - 1, String.valueOf(result));
        tvResult.setText(String.valueOf(result));
    }

    private void initData() {
        initItemList();
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.standard_calculator, this);
        tvVisualExpression = rootView.findViewById(R.id.tv_visual_expression);
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
        itemList.add(new Item(R.drawable.function_mod, "", (pre, post) -> pre % post));
        itemList.add(new Item(R.drawable.instruction_ce, (visualExpression, realExpression, tvResult) -> {
            tvResult.setText("");
            if (visualExpression.size() >= 3) {
                visualExpression.remove(visualExpression.size() - 1);
                setVisualExpression(visualExpression);
                realExpression.remove(visualExpression.size() - 1);
            }
        }));
        itemList.add(new Item(R.drawable.instruction_c, (visualExpression, realExpression, result) -> {
            visualExpression = new ArrayList<>();
            setVisualExpression(visualExpression);
            realExpression = new ArrayList<>();
            tvResult.setText("");
        }));
        itemList.add(new Item(R.drawable.instruction_delete, (visualExpression, realExpression, result) -> {
            if (visualExpression.size() < 3) {
                tvResult.setText(tvResult.getText().subSequence(0, tvResult.getText().length() - 1));
                if (tvResult.getText().length() == 0) {
                    tvResult.setText(String.valueOf(0));
                }
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
        itemList.add(new Item(R.drawable.symbol_dot, "."));
        Item itemEqual = new Item(R.drawable.instruction_equal, "=", (visualExpression, realExpression, result) -> {
//            visualExpression
//            computeExpression()
        });
        itemEqual.backgroundColor = Color.parseColor("#196aa7");
        itemEqual.labelColor = Color.WHITE;
        itemList.add(itemEqual);
    }
}
