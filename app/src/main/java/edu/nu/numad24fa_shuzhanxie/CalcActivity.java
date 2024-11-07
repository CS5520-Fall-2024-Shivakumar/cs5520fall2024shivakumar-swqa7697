package edu.nu.numad24fa_shuzhanxie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalcActivity extends AppCompatActivity {
    private TextView calcDisplay;
    private StringBuilder calcInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calc), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calcDisplay = findViewById(R.id.calc_display);
        calcInput = new StringBuilder();

        setNumberBtnListeners();
        setOperatorBtnListeners();
    }

    private void setNumberBtnListeners() {
        View.OnClickListener listener = v -> {
            Button button = (Button) v;
            calcInput.append(button.getText().toString());
            calcDisplay.setText(calcInput.toString());
        };

        int[] buttons = {R.id.calc_btn_1, R.id.calc_btn_2, R.id.calc_btn_3, R.id.calc_btn_4, R.id.calc_btn_5,
                         R.id.calc_btn_6, R.id.calc_btn_7, R.id.calc_btn_8, R.id.calc_btn_9, R.id.calc_btn_0};

        for (int id : buttons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorBtnListeners() {
        findViewById(R.id.calc_btn_addition).setOnClickListener(v -> {
            calcInput.append("+");
            calcDisplay.setText(calcInput.toString());
        });

        findViewById(R.id.calc_btn_subtraction).setOnClickListener(v -> {
            calcInput.append("-");
            calcDisplay.setText(calcInput.toString());
        });

        findViewById(R.id.calc_btn_backspace).setOnClickListener(v -> {
            if (calcInput.length() > 0) {
                calcInput.deleteCharAt(calcInput.length() - 1);
                calcDisplay.setText(calcInput.toString());
            }
        });

        findViewById(R.id.calc_btn_equal).setOnClickListener(v -> {
            String result = getCalcResult();
            if (result.isEmpty()) {
                Toast.makeText(
                        getApplicationContext(), "Invalid Expression",
                        Toast.LENGTH_LONG).show();
            } else {
                calcInput.setLength(0);
                calcInput.append(result);
                calcDisplay.setText(calcInput.toString());
            }
        });
    }

    private String getCalcResult() {
        int result = 0;
        int i = 0;
        int sign = 1;
        boolean hasNumber = false;
        boolean hasOperator = false;

        while (i < calcInput.length()) {
            if (!Character.isDigit(calcInput.charAt(i))) {
                if (hasOperator) {
                    return "";
                }
                sign = calcInput.charAt(i) == '+' ? 1 : -1;
                hasOperator = true;
                i++;
            } else {
                hasNumber = true;
                hasOperator = false;
                int currNum = 0;

                while (i < calcInput.length() && Character.isDigit(calcInput.charAt(i))) {
                    currNum = (currNum * 10) + Character.getNumericValue(calcInput.charAt(i));
                    i++;
                }

                result += sign * currNum;
            }
        }

        if (!hasNumber) {
            return "";
        } else {
            return Integer.toString(result);
        }
    }
}