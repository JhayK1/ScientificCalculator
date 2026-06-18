package com.alojeremiah.scientificcalculator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView expressionView;
    private TextView resultView;
    private final StringBuilder expression = new StringBuilder();
    private String lastAnswer = "0";

    private static final int BG = Color.rgb(16, 20, 25);
    private static final int DISPLAY_BG = Color.rgb(27, 34, 43);
    private static final int DIGIT = Color.rgb(36, 48, 60);
    private static final int OPERATOR = Color.rgb(57, 107, 111);
    private static final int FUNCTION = Color.rgb(56, 70, 90);
    private static final int DANGER = Color.rgb(138, 58, 70);
    private static final int EQUAL = Color.rgb(217, 154, 61);
    private static final int TEXT = Color.rgb(246, 247, 249);
    private static final int MUTED = Color.rgb(185, 192, 201);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
    }

    private View createContentView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(14), dp(14), dp(14));
        root.setBackgroundColor(BG);

        TextView title = new TextView(this);
        title.setText("Scientific Calculator BY: ALO JEREMIAH");
        title.setTextColor(TEXT);
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout display = new LinearLayout(this);
        display.setOrientation(LinearLayout.VERTICAL);
        display.setGravity(Gravity.END);
        display.setPadding(dp(12), dp(12), dp(12), dp(12));
        display.setBackgroundColor(DISPLAY_BG);
        root.addView(display, new LinearLayout.LayoutParams(-1, dp(118)));

        expressionView = new TextView(this);
        expressionView.setTextColor(MUTED);
        expressionView.setTextSize(18);
        expressionView.setGravity(Gravity.END);
        expressionView.setSingleLine(false);
        expressionView.setText("0");
        display.addView(expressionView, new LinearLayout.LayoutParams(-1, 0, 1));

        resultView = new TextView(this);
        resultView.setTextColor(TEXT);
        resultView.setTextSize(28);
        resultView.setTypeface(Typeface.DEFAULT_BOLD);
        resultView.setGravity(Gravity.END);
        resultView.setSingleLine(false);
        display.addView(resultView, new LinearLayout.LayoutParams(-1, -2));

        ScrollView scrollView = new ScrollView(this);
        GridLayout keypad = new GridLayout(this);
        keypad.setColumnCount(5);
        keypad.setUseDefaultMargins(true);
        keypad.setPadding(0, dp(2), 0, 0);
        scrollView.addView(keypad, new ScrollView.LayoutParams(-1, -2));
        root.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1));

        String[] labels = {
                "AC", "DEL", "(", ")", "/",
                "sin", "cos", "tan", "sqrt", "^",
                "sinh", "cosh", "tanh", "ln", "log",
                "7", "8", "9", "*", "nPr",
                "4", "5", "6", "-", "nCr",
                "1", "2", "3", "+", "mean",
                "0", ".", ",", "!", "=",
                "sum", "std", "det2", "det3", "det4",
                "abs", "ans", "inv2", "clear", "eval"
        };

        for (String label : labels) {
            keypad.addView(createButton(label));
        }

        return root;
    }

    private Button createButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(TEXT);
        button.setTextSize(label.length() > 4 ? 12 : 16);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(2), 0, dp(2), 0);
        button.setBackgroundColor(buttonColor(label));
        button.setOnClickListener(v -> handleButton(label));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dp(52);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(3), dp(3), dp(3), dp(3));
        button.setLayoutParams(params);
        return button;
    }

    private int buttonColor(String label) {
        if ("AC".equals(label) || "DEL".equals(label) || "clear".equals(label)) {
            return DANGER;
        }
        if ("=".equals(label) || "eval".equals(label)) {
            return EQUAL;
        }
        if ("+-*/^!(),.".contains(label) && label.length() == 1) {
            return OPERATOR;
        }
        if (label.matches("[0-9]")) {
            return DIGIT;
        }
        return FUNCTION;
    }

    private void handleButton(String label) {
        switch (label) {
            case "AC":
            case "clear":
                expression.setLength(0);
                resultView.setText("Ready");
                break;
            case "DEL":
                if (expression.length() > 0) {
                    expression.deleteCharAt(expression.length() - 1);
                }
                break;
            case "=":
            case "eval":
                evaluateExpression();
                return;
            case "ans":
                expression.append(lastAnswer);
                break;
            case "sin":
            case "cos":
            case "tan":
            case "sqrt":
            case "sinh":
            case "cosh":
            case "tanh":
            case "ln":
            case "log":
            case "abs":
            case "mean":
            case "sum":
            case "std":
            case "det2":
            case "det3":
            case "det4":
            case "inv2":
            case "nPr":
            case "nCr":
                expression.append(label).append("(");
                break;
            default:
                expression.append(label);
        }
        refreshExpression();
    }

    private void evaluateExpression() {
        String text = expression.toString();
        if (text.trim().isEmpty()) {
            resultView.setText("0");
            return;
        }

        try {
            String answer = CalculatorEngine.evaluate(text);
            resultView.setText(answer);
            if (!answer.contains("[") && !answer.startsWith("Error")) {
                lastAnswer = answer;
            }
        } catch (RuntimeException ex) {
            resultView.setText("Error: " + ex.getMessage());
        }
        refreshExpression();
    }

    private void refreshExpression() {
        expressionView.setText(expression.length() == 0 ? "0" : expression.toString());
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static final class CalculatorEngine {
        static String evaluate(String input) {
            String cleaned = input.trim();
            if (cleaned.startsWith("inv2(")) {
                return evaluateInverse2(cleaned);
            }
            double value = new Parser(cleaned).parse();
            return format(value);
        }

        private static String evaluateInverse2(String text) {
            if (!text.endsWith(")")) {
                throw new IllegalArgumentException("missing closing parenthesis");
            }
            String inside = text.substring("inv2(".length(), text.length() - 1);
            double[] values = evaluateArguments(inside);
            if (values.length != 4) {
                throw new IllegalArgumentException("inv2 needs 4 values");
            }
            double det = values[0] * values[3] - values[1] * values[2];
            if (Math.abs(det) < 1e-12) {
                throw new IllegalArgumentException("matrix has no inverse");
            }
            double a = values[3] / det;
            double b = -values[1] / det;
            double c = -values[2] / det;
            double d = values[0] / det;
            return "[[" + format(a) + ", " + format(b) + "], [" + format(c) + ", " + format(d) + "]]";
        }

        private static String format(double value) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new IllegalArgumentException("invalid math result");
            }
            BigDecimal decimal = BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
            return decimal.toPlainString();
        }

        private static double[] evaluateArguments(String text) {
            List<String> pieces = splitArguments(text);
            double[] values = new double[pieces.size()];
            for (int i = 0; i < pieces.size(); i++) {
                values[i] = new Parser(pieces.get(i)).parse();
            }
            return values;
        }

        private static List<String> splitArguments(String text) {
            List<String> args = new ArrayList<>();
            int depth = 0;
            int start = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '(') {
                    depth++;
                } else if (c == ')') {
                    depth--;
                } else if (c == ',' && depth == 0) {
                    args.add(text.substring(start, i).trim());
                    start = i + 1;
                }
            }
            String last = text.substring(start).trim();
            if (!last.isEmpty()) {
                args.add(last);
            }
            return args;
        }

        private static double determinant(double[] values, int size) {
            double[][] matrix = new double[size][size];
            int index = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    matrix[row][col] = values[index++];
                }
            }
            return determinant(matrix);
        }

        private static double determinant(double[][] matrix) {
            int size = matrix.length;
            if (size == 1) {
                return matrix[0][0];
            }
            if (size == 2) {
                return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
            }
            double total = 0;
            for (int col = 0; col < size; col++) {
                total += Math.pow(-1, col) * matrix[0][col] * determinant(minor(matrix, 0, col));
            }
            return total;
        }

        private static double[][] minor(double[][] matrix, int skipRow, int skipCol) {
            int size = matrix.length;
            double[][] result = new double[size - 1][size - 1];
            int r = 0;
            for (int row = 0; row < size; row++) {
                if (row == skipRow) {
                    continue;
                }
                int c = 0;
                for (int col = 0; col < size; col++) {
                    if (col == skipCol) {
                        continue;
                    }
                    result[r][c++] = matrix[row][col];
                }
                r++;
            }
            return result;
        }

        private static final class Parser {
            private final String input;
            private int position = -1;
            private int current;

            Parser(String input) {
                this.input = input;
                nextChar();
            }

            double parse() {
                double value = parseExpression();
                if (position < input.length()) {
                    throw new IllegalArgumentException("unexpected '" + (char) current + "'");
                }
                return value;
            }

            private void nextChar() {
                current = (++position < input.length()) ? input.charAt(position) : -1;
            }

            private boolean eat(int charToEat) {
                while (current == ' ') {
                    nextChar();
                }
                if (current == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            private double parseExpression() {
                double value = parseTerm();
                while (true) {
                    if (eat('+')) {
                        value += parseTerm();
                    } else if (eat('-')) {
                        value -= parseTerm();
                    } else {
                        return value;
                    }
                }
            }

            private double parseTerm() {
                double value = parsePower();
                while (true) {
                    if (eat('*')) {
                        value *= parsePower();
                    } else if (eat('/')) {
                        double divisor = parsePower();
                        if (Math.abs(divisor) < 1e-12) {
                            throw new IllegalArgumentException("cannot divide by zero");
                        }
                        value /= divisor;
                    } else {
                        return value;
                    }
                }
            }

            private double parsePower() {
                double value = parseUnary();
                if (eat('^')) {
                    value = Math.pow(value, parsePower());
                }
                return value;
            }

            private double parseUnary() {
                if (eat('+')) {
                    return parseUnary();
                }
                if (eat('-')) {
                    return -parseUnary();
                }
                return parsePostfix();
            }

            private double parsePostfix() {
                double value = parsePrimary();
                while (eat('!')) {
                    value = factorial(value);
                }
                return value;
            }

            private double parsePrimary() {
                if (eat('(')) {
                    double value = parseExpression();
                    if (!eat(')')) {
                        throw new IllegalArgumentException("missing closing parenthesis");
                    }
                    return value;
                }

                if (isDigitOrDot(current)) {
                    int start = position;
                    while (isDigitOrDot(current)) {
                        nextChar();
                    }
                    return Double.parseDouble(input.substring(start, position));
                }

                if (Character.isLetter(current)) {
                    String name = parseName();
                    if (!eat('(')) {
                        throw new IllegalArgumentException(name + " needs parentheses");
                    }
                    String inside = readFunctionBody();
                    return evaluateFunction(name, inside);
                }

                throw new IllegalArgumentException("unexpected '" + (char) current + "'");
            }

            private String parseName() {
                int start = position;
                while (Character.isLetterOrDigit(current)) {
                    nextChar();
                }
                return input.substring(start, position);
            }

            private String readFunctionBody() {
                int start = position;
                int depth = 1;
                while (position < input.length() && depth > 0) {
                    if (current == '(') {
                        depth++;
                    } else if (current == ')') {
                        depth--;
                    }
                    nextChar();
                }
                if (depth != 0) {
                    throw new IllegalArgumentException("missing closing parenthesis");
                }
                return input.substring(start, position - 1);
            }

            private double evaluateFunction(String name, String inside) {
                double[] values = evaluateArguments(inside);
                String normalized = name.toLowerCase(Locale.US);

                switch (normalized) {
                    case "sin":
                        requireCount(values, 1, name);
                        return Math.sin(Math.toRadians(values[0]));
                    case "cos":
                        requireCount(values, 1, name);
                        return Math.cos(Math.toRadians(values[0]));
                    case "tan":
                        requireCount(values, 1, name);
                        return Math.tan(Math.toRadians(values[0]));
                    case "sinh":
                        requireCount(values, 1, name);
                        return Math.sinh(values[0]);
                    case "cosh":
                        requireCount(values, 1, name);
                        return Math.cosh(values[0]);
                    case "tanh":
                        requireCount(values, 1, name);
                        return Math.tanh(values[0]);
                    case "sqrt":
                        requireCount(values, 1, name);
                        if (values[0] < 0) {
                            throw new IllegalArgumentException("sqrt needs positive input");
                        }
                        return Math.sqrt(values[0]);
                    case "ln":
                        requireCount(values, 1, name);
                        return Math.log(values[0]);
                    case "log":
                        requireCount(values, 1, name);
                        return Math.log10(values[0]);
                    case "abs":
                        requireCount(values, 1, name);
                        return Math.abs(values[0]);
                    case "sum":
                        return sum(values);
                    case "mean":
                        return sum(values) / values.length;
                    case "std":
                        return standardDeviation(values);
                    case "npr":
                        requireCount(values, 2, name);
                        return permutation(values[0], values[1]);
                    case "ncr":
                        requireCount(values, 2, name);
                        return combination(values[0], values[1]);
                    case "det2":
                        requireCount(values, 4, name);
                        return determinant(values, 2);
                    case "det3":
                        requireCount(values, 9, name);
                        return determinant(values, 3);
                    case "det4":
                        requireCount(values, 16, name);
                        return determinant(values, 4);
                    default:
                        throw new IllegalArgumentException("unknown function " + name);
                }
            }

            private static boolean isDigitOrDot(int c) {
                return (c >= '0' && c <= '9') || c == '.';
            }

            private static void requireCount(double[] values, int count, String name) {
                if (values.length != count) {
                    throw new IllegalArgumentException(name + " needs " + count + " value(s)");
                }
            }

            private static double sum(double[] values) {
                if (values.length == 0) {
                    throw new IllegalArgumentException("at least one value is required");
                }
                double total = 0;
                for (double value : values) {
                    total += value;
                }
                return total;
            }

            private static double standardDeviation(double[] values) {
                if (values.length == 0) {
                    throw new IllegalArgumentException("at least one value is required");
                }
                double mean = sum(values) / values.length;
                double total = 0;
                for (double value : values) {
                    double diff = value - mean;
                    total += diff * diff;
                }
                return Math.sqrt(total / values.length);
            }

            private static double permutation(double nValue, double rValue) {
                int n = checkedWholeNumber(nValue);
                int r = checkedWholeNumber(rValue);
                if (r > n) {
                    throw new IllegalArgumentException("r cannot be greater than n");
                }
                return factorialInt(n) / factorialInt(n - r);
            }

            private static double combination(double nValue, double rValue) {
                int n = checkedWholeNumber(nValue);
                int r = checkedWholeNumber(rValue);
                if (r > n) {
                    throw new IllegalArgumentException("r cannot be greater than n");
                }
                return factorialInt(n) / (factorialInt(r) * factorialInt(n - r));
            }

            private static double factorial(double value) {
                return factorialInt(checkedWholeNumber(value));
            }

            private static int checkedWholeNumber(double value) {
                if (value < 0 || Math.rint(value) != value || value > 170) {
                    throw new IllegalArgumentException("factorial values must be whole numbers from 0 to 170");
                }
                return (int) value;
            }

            private static double factorialInt(int value) {
                double total = 1;
                for (int i = 2; i <= value; i++) {
                    total *= i;
                }
                return total;
            }
        }
    }
}


