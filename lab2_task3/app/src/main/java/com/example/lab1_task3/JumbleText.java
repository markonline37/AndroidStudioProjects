package com.example.lab1_task3;

import java.util.ArrayList;
import java.lang.Math;

public class JumbleText {
    public String jumbleString(String input){
        ArrayList<String> arr = new ArrayList<>();

        for(int i = 0, j = input.length(); i<j;i++){
            char temp = input.charAt(0);
            arr.add(Character.toString(temp));
            input = input.substring(1);
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0, j = arr.size(); i<j;i++){
            double randomDouble = Math.random();
            randomDouble =randomDouble * arr.size()-1 + 1;
            int randomInt = (int) randomDouble;
            sb.append(arr.get(randomInt));
            arr.remove(randomInt);
        }

        return sb.toString();
    }
}
