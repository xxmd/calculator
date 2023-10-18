package io.github.xxmd.calculator.entity;

import androidx.annotation.DrawableRes;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Item {
    @DrawableRes
    public int icon;
    public String label;
    public ItemType type;
    BiFunction<Double, Double, Double> binaryFunction;
    BiConsumer<List<String>, String> instruction;
    Function<Double, Double> function;
    public int backgroundColor;
    public int labelColor;

    public Item(int icon, String label, BiFunction<Double, Double, Double> binaryFunction) {
        this.icon = icon;
        this.label = label;
        this.type = ItemType.BINARY_FUNCTION;
        this.binaryFunction = binaryFunction;
    }

    public Item(int icon, BiConsumer<List<String>, String> instruction) {
        this.icon = icon;
        this.type = ItemType.INSTRUCTION;
        this.instruction = instruction;
    }

    public Item(int icon, String label, BiConsumer<List<String>, String> instruction) {
        this.icon = icon;
        this.label = label;
        this.type = ItemType.INSTRUCTION;
        this.instruction = instruction;
    }

    public Item(int icon, String label, Function<Double, Double> function) {
        this.icon = icon;
        this.label = label;
        this.type = ItemType.FUNCTION;
        this.function = function;
    }

    public Item(int icon, Function<Double, Double> function) {
        this.icon = icon;
        this.type = ItemType.FUNCTION;
        this.function = function;
    }

    public Item(int icon, String label) {
        this.icon = icon;
        this.label = label;
        this.type = ItemType.SYMBOL;
    }

    public Item(int icon, int number) {
        this.icon = icon;
        this.label = String.valueOf(number);
        this.type = ItemType.SYMBOL;
    }
}
