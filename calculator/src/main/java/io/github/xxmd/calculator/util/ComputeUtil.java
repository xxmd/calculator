package io.github.xxmd.calculator.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.apache.commons.lang3.math.NumberUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://github.com/mozilla/rhino
 * usage: http://web.mit.edu/javascript/src/mozilla/js/rhino/docs/tutorial.html
 */
public class ComputeUtil {
    private static NumberFormat instance = NumberFormat.getInstance();
    public static final String eval(String expression) {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        ScriptableObject scope = context.initStandardObjects();
        Object result = context.evaluateString(scope, expression, "<cmd>", 1, null);
        String doubleResult = result.toString();
        return removeMultipleZero(doubleResult);
    }

    public static final String eval(List<String> list) {
        String expression = list.stream().collect(Collectors.joining());
        return eval(expression);
    }

    public static String removeMultipleZero(String srcResult) {
        int length = srcResult.length();
        if (srcResult.contains(".") && srcResult.endsWith("0")) {
            String[] split = srcResult.split("");
            int i = length - 2;
            while (split[i].equals("0")) {
                i--;
            }
            if (split[i].equals(".")) {
                i--;
            }
            srcResult = srcResult.substring(0, i + 1);
        }
        return srcResult;
    }

    public static String format(String result) {
        if (!NumberUtils.isParsable(result) || Double.valueOf(result) < 1) {
            return result;
        }

        if (result.contains(".")) {
            String[] split = result.split("\\.");
            String format = instance.format(Double.valueOf(split[0]));
            return format + "." + split[1];
        } else {
            return instance.format(Double.valueOf(result));
        }
    }
}
