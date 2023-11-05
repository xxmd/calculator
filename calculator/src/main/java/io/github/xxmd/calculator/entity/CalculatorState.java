package io.github.xxmd.calculator.entity;

import java.util.ArrayList;
import java.util.List;

public class CalculatorState {
    public List<String> visualExpression = new ArrayList<>();
    public List<String> realExpression = new ArrayList<>();
    public String curResult = "0";
    public KeyboardItem preInputItem;
    public boolean nextReplaceResult = true;
}
