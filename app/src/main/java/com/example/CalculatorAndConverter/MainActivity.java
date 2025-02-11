package com.example.CalculatorAndConverter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private String expression = "";
    private boolean lastInputWasOperator = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // UI Elements
        TextView screen = findViewById(R.id.textview_display);

        // Buttons (Declare and initialize all your buttons here as before)
        Button ac = findViewById(R.id.button_ac);
        Button div = findViewById(R.id.button_div);
        Button sev = findViewById(R.id.button_7);
        Button eight = findViewById(R.id.button_8);
        Button nine = findViewById(R.id.button_9);
        Button multiply = findViewById(R.id.button_multiply);
        Button four = findViewById(R.id.button_4);
        Button five = findViewById(R.id.button_5);
        Button six = findViewById(R.id.button_6);
        Button subtract = findViewById(R.id.button_subtract);
        Button one = findViewById(R.id.button_1);
        Button two = findViewById(R.id.button_2);
        Button three = findViewById(R.id.button_3);
        Button add = findViewById(R.id.button_add);
        Button del = findViewById(R.id.button_del);
        Button zero = findViewById(R.id.button_0);
        Button dot = findViewById(R.id.button_dot);
        Button equal = findViewById(R.id.button_equal);
        Button openParen = findViewById(R.id.button_open_paren);
        Button closeParen = findViewById(R.id.button_close_paren);
        Button buttonGo = findViewById(R.id.button_go);


        // Number Button Click Listeners
        setNumberClickListener(sev, screen);
        setNumberClickListener(eight, screen);
        setNumberClickListener(nine, screen);
        setNumberClickListener(four, screen);
        setNumberClickListener(five, screen);
        setNumberClickListener(six, screen);
        setNumberClickListener(one, screen);
        setNumberClickListener(two, screen);
        setNumberClickListener(three, screen);
        setNumberClickListener(zero, screen);
        setNumberClickListener(dot, screen);

        // Operator Button Click Listeners
        setOperatorClickListener(add, screen);
        setOperatorClickListener(subtract, screen);
        setOperatorClickListener(multiply, screen);
        setOperatorClickListener(div, screen);

        // Parentheses
        openParen.setOnClickListener(v -> {
            expression += "(";
            screen.setText(expression);
            lastInputWasOperator = false;
        });

        closeParen.setOnClickListener(v -> {
            expression += ")";
            screen.setText(expression);
            lastInputWasOperator = false;
        });

        // Equals Button
        equal.setOnClickListener(v -> {
            if (!expression.isEmpty()) {
                double result = evaluateExpression(expression);
                if (Double.isNaN(result)) {
                    screen.setText("Error"); // Or a more informative message
                } else {
                    screen.setText(String.valueOf(result));
                    expression = String.valueOf(result); // Store result for further operations
                }
            } else {
                screen.setText("0");
            }
        });

        // Clear (AC)
        ac.setOnClickListener(v -> {
            expression = "";
            screen.setText("0");
            lastInputWasOperator = false;
        });

        // Delete (DEL)
        del.setOnClickListener(v -> {
            if (!expression.isEmpty()) {
                expression = expression.substring(0, expression.length() - 1);
                screen.setText(expression.isEmpty() ? "0" : expression);
                lastInputWasOperator = isOperator(expression.charAt(expression.length() - 1));
            }
        });

        // Go to Number Conversion Activity
        buttonGo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Number_conv.class);
            startActivity(intent);
        });
    }

    private void setNumberClickListener(Button button, TextView screen) {
        button.setOnClickListener(v -> {
            String value = button.getText().toString();
            expression += value;
            screen.setText(expression);
            lastInputWasOperator = false;
        });
    }

    private void setOperatorClickListener(Button button, TextView screen) {
        button.setOnClickListener(v -> {
            if (!expression.isEmpty() && !lastInputWasOperator) {
                expression += button.getText().toString(); // No extra spaces needed
                screen.setText(expression);
                lastInputWasOperator = true;
            }
        });
    }

    private double evaluateExpression(String expression) {
        try {
            return parseExpression(expression);
        } catch (ArithmeticException e) {
            return Double.NaN; // Or display an error message
        } catch (Exception e) { // Catch other potential parsing errors
            return Double.NaN;
        }
    }

    private double parseExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/()", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();

            if (token.isEmpty()) continue;

            if (Character.isDigit(token.charAt(0)) || token.charAt(0) == '.') {
                try {
                    values.push(Double.parseDouble(token));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number: " + token);
                }
            } else if (token.charAt(0) == '(') {
                operators.push(token.charAt(0));
            } else if (token.charAt(0) == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    applyOperator(operators, values);
                }
                operators.pop(); // Remove '('
            } else if (isOperator(token.charAt(0))) {
                char op = token.charAt(0);
                while (!operators.isEmpty() && hasPrecedence(op, operators.peek())) {
                    applyOperator(operators, values);
                }
                operators.push(op);
            }
        }

        while (!operators.isEmpty()) {
            applyOperator(operators, values);
        }

        return values.pop();
    }

    private void applyOperator(Stack<Character> operators, Stack<Double> values) {
        char operator = operators.pop();
        double operand2 = values.pop();
        double operand1 = values.pop();
        double result = 0;

        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = operand1 / operand2;
                break;
        }
        values.push(result);
    }

    private boolean isOperator(char c) {
        return "+-*/".indexOf(c) != -1;
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }
}