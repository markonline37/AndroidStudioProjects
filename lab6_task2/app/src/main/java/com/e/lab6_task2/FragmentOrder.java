package com.e.lab6_task2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

public class FragmentOrder extends Fragment{
    View view;
    static private boolean stuffedCrust = false;
    static private boolean thinAndCrispy = false;
    static private boolean mushrooms = false;
    static private boolean sweetcorn = false;
    static private boolean onions = false;
    static private boolean peppers = false;
    static private boolean extraCheese = false;
    static private RadioButton buttonStuffedCrust;
    static private RadioButton buttonThinAndCrispy;
    static private CheckBox checkboxMushrooms;
    static private CheckBox checkBoxSweetcorn;
    static private CheckBox checkboxOnions;
    static private CheckBox checkboxPeppers;
    static private ToggleButton toggleExtraCheese;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.frag_order, container, false);

        buttonStuffedCrust = view.findViewById(R.id.stuffedCrust);
        buttonThinAndCrispy = view.findViewById(R.id.thinAndCrispy);
        buttonStuffedCrust.toggle();
        checkboxMushrooms = view.findViewById(R.id.mushrooms);
        checkboxOnions = view.findViewById(R.id.onions);
        checkBoxSweetcorn = view.findViewById(R.id.sweetcorn);
        checkboxPeppers = view.findViewById(R.id.peppers);
        toggleExtraCheese = view.findViewById(R.id.extraCheese);

        return view;
    }

    static public void bakePizza(){
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
        MainActivity.orderDetails = output.toString();
    }
}
