package com.example.CalculatorAndConverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Number_conv extends AppCompatActivity {
    private EditText inputNumber;
    private TextView resultText;
    private Spinner fromSpinner, toSpinner;
    private String fromBase = "Decimal";
    private String toBase = "Binary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_number_conv);

        Intent intent = getIntent();

        Button buttonBack = findViewById(R.id.button_back);

        // Set an OnClickListener to start the Number_conv activity when clicked
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Number_conv.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize UI Components
        inputNumber = findViewById(R.id.input_number);
        resultText = findViewById(R.id.output_result);
        fromSpinner = findViewById(R.id.spinner_from_base);
        toSpinner = findViewById(R.id.spinner_to_base);
        Button convertButton = findViewById(R.id.convert_button);
        Button clearButton = findViewById(R.id.clear_button);
        Button swapButton = findViewById(R.id.reload_button);

        // Disable system keyboard
        inputNumber.setFocusable(false);
        inputNumber.setClickable(true);

// Initialize number buttons
        int[] buttonIds = {
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
                R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
                R.id.button_8, R.id.button_9, R.id.button_A, R.id.button_B,
                R.id.button_C, R.id.button_D, R.id.button_E, R.id.button_F
        };

        // Attach click listeners to all number buttons
        for (int id : buttonIds) {
            Button button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(v -> {
                    String currentText = inputNumber.getText().toString();
                    inputNumber.setText(currentText + button.getText().toString());
                });
            }
        }


        // Setup Spinner Dropdowns
        String[] bases = {"Decimal", "Binary", "Octal", "Hexadecimal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bases);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        // Handle Spinner Selection
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromBase = bases[position];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toBase = bases[position];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Convert Button Click
        convertButton.setOnClickListener(view -> convertNumber());

        // Clear Button
        clearButton.setOnClickListener(view -> {
            inputNumber.setText("");
            resultText.setText("");
        });

        // Swap Button
        swapButton.setOnClickListener(view -> {
            int fromPos = fromSpinner.getSelectedItemPosition();
            int toPos = toSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toPos);
            toSpinner.setSelection(fromPos);
        });

    }

    private void convertNumber() {
        String input = inputNumber.getText().toString().trim();
        if (input.isEmpty()) {
            resultText.setText("Enter a number");
            return;
        }

        try {
            int decimalValue = convertToDecimal(input, fromBase);
            String convertedResult = convertFromDecimal(decimalValue, toBase);
            resultText.setText(convertedResult);
        } catch (Exception e) {
            resultText.setText("Invalid Input");
        }
    }

    private int convertToDecimal(String input, String base) throws NumberFormatException {
        switch (base) {
            case "Binary": return Integer.parseInt(input, 2);
            case "Octal": return Integer.parseInt(input, 8);
            case "Hexadecimal": return Integer.parseInt(input, 16);
            default: return Integer.parseInt(input); // Decimal
        }
    }

    private String convertFromDecimal(int decimal, String base) {
        switch (base) {
            case "Binary": return Integer.toBinaryString(decimal);
            case "Octal": return Integer.toOctalString(decimal);
            case "Hexadecimal": return Integer.toHexString(decimal).toUpperCase();
            default: return String.valueOf(decimal); // Decimal
        }
    }
}