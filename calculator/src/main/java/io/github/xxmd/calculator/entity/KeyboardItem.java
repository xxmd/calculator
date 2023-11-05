package io.github.xxmd.calculator.entity;

import android.widget.Toast;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.github.xxmd.calculator.R;
import io.github.xxmd.calculator.util.ComputeUtil;

public enum KeyboardItem {
    PERCENT(R.drawable.function_mod, ItemType.FUNCTION, state -> {
        String lastFactor = state.realExpression.size() == 0 ? "0" : state.realExpression.get(0);
        // 暂时不考虑+-和*/的区别
        state.curResult = ComputeUtil.eval(String.format("%s*%s/100", lastFactor, state.curResult));
    }),
    CE(R.drawable.instruction_ce, ItemType.FUNCTION, state -> {
        state.curResult = "0";
    }),
    C(R.drawable.instruction_c, ItemType.FUNCTION, state -> {
        state.visualExpression = new ArrayList<>();
        state.realExpression = new ArrayList<>();
        state.curResult = "0";
    }),
    CLEAN(R.drawable.instruction_delete, ItemType.OTHER, state -> {
        if (state.curResult.length() == 1) {
            state.nextReplaceResult = true;
            state.curResult = "0";
        } else {
            state.nextReplaceResult = false;
            String newResult = state.curResult.substring(0, state.curResult.length() - 1);
            state.curResult = newResult;
        }
    }),
    RECIPROCAL(R.drawable.function_reciprocal, ItemType.FUNCTION, state -> {
        state.visualExpression.add(String.format("1/%s", state.curResult));

        String expression = String.format("1/%s", state.curResult);
        String newResult = ComputeUtil.eval(expression);
        state.realExpression.add(newResult);
        state.curResult = newResult;
    }),
    POW2(R.drawable.function_square, ItemType.FUNCTION, state -> {
        state.visualExpression.add(String.format("sqr(%s)", state.curResult));
        double pow = Math.pow(Double.valueOf(state.curResult), 2);
        String newResult = ComputeUtil.removeMultipleZero(String.valueOf(pow));
        state.realExpression.add(newResult);
        state.curResult = newResult;
    }),
    SQRT(R.drawable.function_sqrt, ItemType.FUNCTION, state -> {
        state.visualExpression.add(String.format("√(%s)", state.curResult));

        String newResult = String.valueOf(Math.sqrt(Double.valueOf(state.curResult)));
        state.realExpression.add(newResult);
        state.curResult = newResult;
    }),
    DIVIDE(R.drawable.function_divide, ItemType.FUNCTION, state -> {
        if (state.realExpression.size() == 0 && state.curResult.equals(Constant.ZERO)) {
            return;
        }
        if (state.nextReplaceResult) {
            replacePreOperator(state, "÷", "/");
            return;
        }
        if (state.curResult.equals(Constant.ZERO)) {
            throw new IllegalArgumentException("The dividend cannot be zero");
        }
        state.realExpression.add(state.curResult);
        String expression = state.realExpression.stream().collect(Collectors.joining());
        String newResult = ComputeUtil.eval(expression);
        state.visualExpression.clear();
        state.visualExpression.add(newResult);
        state.visualExpression.add("÷");

        state.realExpression.clear();
        state.realExpression.add(newResult);
        state.realExpression.add("/");
        state.curResult = newResult;
    }),
    SEVEN(R.drawable.symbol_7, 7),
    EIGHT(R.drawable.symbol_8, 8),
    NINE(R.drawable.symbol_9, 9),
    MULTIPLE(R.drawable.function_multiply, "×", "*"),
    FOUR(R.drawable.symbol_4, 4),
    FIVE(R.drawable.symbol_5, 5),
    SIX(R.drawable.symbol_6, 6),
    MINUS(R.drawable.function_minus, "-", "-"),
    ONE(R.drawable.symbol_1, 1),
    TWO(R.drawable.symbol_2, 2),
    THREE(R.drawable.symbol_3, 3),
    ADD(R.drawable.function_add, "+", "+"),
    INVERT(R.drawable.function_invert, ItemType.FUNCTION, state -> {
        state.curResult = ComputeUtil.eval(String.format("%s*-1", state.curResult));
    }),
    ZERO(R.drawable.symbol_0, ItemType.OTHER, state -> {
        if (state.nextReplaceResult) {
            state.curResult = Constant.ZERO;
            state.nextReplaceResult = true;
        } else {
            if (!state.curResult.equals(Constant.ZERO)) {
                state.curResult = state.curResult + Constant.ZERO;
            }
        }
    }),
    DOT(R.drawable.symbol_dot, ItemType.OTHER, state -> {
        if (state.nextReplaceResult) {
            state.curResult = "0.";
            state.nextReplaceResult = false;
        } else {
            if (!state.curResult.contains(".")) {
                state.curResult = state.curResult + ".";
            }
        }
    }),
    EQUAL(R.drawable.instruction_equal, ItemType.FUNCTION, state -> {
        // 这里和win10的等于功能有差异
        if (state.visualExpression.size() == 0 && state.realExpression.size() == 0) {
            return;
        }
        String last = state.visualExpression.get(state.visualExpression.size() - 1);
        if (last.equals("=")) {
            return;
        }
        if (state.nextReplaceResult && endWithOperator(state.realExpression)) {
            return;
        }
        if (endWithOperator(state.realExpression)) {
            state.realExpression.add(state.curResult);
            state.visualExpression.add(state.curResult);
        }
        state.visualExpression.add("=");
        String expression = state.realExpression.stream().collect(Collectors.joining());
        String newResult = ComputeUtil.eval(expression);

        state.realExpression.clear();
        state.realExpression.add(newResult);
        state.curResult = newResult;
    });

    private static void replacePreOperator(CalculatorState state, String visualOperator, String realOperator) {
        state.visualExpression.remove(state.visualExpression.size() - 1);
        if (state.visualExpression.size() > 1) {
            state.visualExpression.clear();
            state.visualExpression.add(state.curResult);
        }
        if (endWithOperator(state.realExpression)) {
            state.realExpression.remove(state.visualExpression.size() - 1);
        }
        state.visualExpression.add(visualOperator);
        state.realExpression.add(realOperator);
    }

    @DrawableRes
    public int icon;
    public ItemType type;
    public Consumer<CalculatorState> modifyState;

    KeyboardItem(int icon, ItemType type, Consumer<CalculatorState> modifyState) {
        this.icon = icon;
        this.type = type;
        this.modifyState = new Consumer<CalculatorState>() {
            @Override
            public void accept(CalculatorState state) {
                modifyState.accept(state);
                if (type.equals(ItemType.FUNCTION)) {
                    state.nextReplaceResult = true;
                }
                if (type.equals(ItemType.SYMBOL)) {
                    state.nextReplaceResult = false;
                }
                state.preInputItem = KeyboardItem.this;
            }
        };
    }

    KeyboardItem(int icon, int number) {
        this(icon, ItemType.SYMBOL, new Consumer<CalculatorState>() {
            @Override
            public void accept(CalculatorState state) {
                if (state.nextReplaceResult) {
                    state.curResult = String.valueOf(number);
                } else {
                    state.curResult = state.curResult + number;
                }
            }
        });
    }

    KeyboardItem(int icon, String visualOperator, String realOperator) {
        this(icon, ItemType.FUNCTION, new Consumer<CalculatorState>() {
            @Override
            public void accept(CalculatorState state) {
                if (state.realExpression.size() == 0 && state.curResult.equals(Constant.ZERO)) {
                    return;
                }
                if (state.nextReplaceResult && endWithOperator(state.realExpression)) {
                    replacePreOperator(state, visualOperator, realOperator);
                    return;
                }
                if (state.visualExpression.size() == 0 && state.realExpression.size() == 0) {
                    state.visualExpression.add(state.curResult);
                    state.visualExpression.add(visualOperator);

                    state.realExpression.add(state.curResult);
                    state.realExpression.add(realOperator);
                    return;
                }
                if (endWithOperator(state.realExpression)) {
                    state.realExpression.add(state.curResult);
                }
                String expression = state.realExpression.stream().collect(Collectors.joining());
                String newResult = ComputeUtil.eval(expression);
                state.visualExpression.clear();
                state.visualExpression.add(newResult);
                state.visualExpression.add(visualOperator);

                state.realExpression.clear();
                state.realExpression.add(newResult);
                state.realExpression.add(realOperator);
                state.curResult = newResult;
            }
        });
    }

    public static final boolean endWithOperator(List<String> expression) {
        if (expression.size() == 0) {
            return false;
        }
        String last = expression.get(expression.size() - 1);
        return last.equals("+") || last.equals("-") || last.equals("*") || last.equals("/");

    }
}
