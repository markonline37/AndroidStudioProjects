package a.lab4_task1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean stuffedCrust = false;
    private boolean thinAndCrispy = false;
    private boolean mushrooms = false;
    private boolean sweetcorn = false;
    private boolean onions = false;
    private boolean peppers = false;
    private boolean extraCheese = false;
    private RadioButton buttonStuffedCrust;
    private RadioButton buttonThinAndCrispy;
    private CheckBox checkboxMushrooms;
    private CheckBox checkBoxSweetcorn;
    private CheckBox checkboxOnions;
    private CheckBox checkboxPeppers;
    private ToggleButton toggleExtraCheese;
    private Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStuffedCrust = findViewById(R.id.stuffedCrust);
        buttonThinAndCrispy = findViewById(R.id.thinAndCrispy);
        buttonStuffedCrust.toggle();
        checkboxMushrooms = findViewById(R.id.mushrooms);
        checkboxOnions = findViewById(R.id.onions);
        checkBoxSweetcorn = findViewById(R.id.sweetcorn);
        checkboxPeppers = findViewById(R.id.peppers);
        toggleExtraCheese = findViewById(R.id.extraCheese);

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(buttonStuffedCrust.isChecked()){
            stuffedCrust = true;
            thinAndCrispy = false;
        }else{
            stuffedCrust = false;
            thinAndCrispy = true;
        }
        mushrooms = checkboxMushrooms.isChecked() ? true : false;
        sweetcorn = checkBoxSweetcorn.isChecked() ? true : false;
        onions = checkboxOnions.isChecked() ? true : false;
        peppers = checkboxPeppers.isChecked() ? true : false;
        extraCheese = toggleExtraCheese.isChecked() ? true : false;

        bakePizza();
    }

    private void bakePizza(){
        StringBuilder output = new StringBuilder();
        output.append("You ordered:\n");
        if(stuffedCrust)output.append("Stuffed Crust\n");
        if(thinAndCrispy)output.append("Thin and Crispy\n");
        if(mushrooms)output.append("Mushrooms\n");
        if(sweetcorn)output.append("Sweetcorn\n");
        if(onions)output.append("Onions\n");
        if(peppers)output.append("Peppers\n");
        if(extraCheese){
            output.append("Yes Extra Cheese\n");
        }else{
            output.append("No Extra Cheese\n");
        }
        Context context = getApplicationContext();
        Toast.makeText(context, output.toString(), Toast.LENGTH_LONG).show();
        resetApp();
    }

    private void resetApp(){
        buttonStuffedCrust.toggle();
        if(checkboxMushrooms.isChecked())checkboxMushrooms.toggle();
        if(checkBoxSweetcorn.isChecked())checkBoxSweetcorn.toggle();
        if(checkboxOnions.isChecked())checkboxOnions.toggle();
        if(checkboxPeppers.isChecked())checkboxPeppers.toggle();
        if(toggleExtraCheese.isChecked())toggleExtraCheese.toggle();
        stuffedCrust = false;
        thinAndCrispy = false;
        mushrooms = false;
        sweetcorn = false;
        onions = false;
        peppers = false;
        extraCheese = false;
    }
}
