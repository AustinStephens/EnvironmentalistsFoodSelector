package com.example.environmentalistsfoodselector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String skeleton0(){
        return "Test Skeleton0 Code";
    }

    public String skeleton1() {
       return "Test Skeleton1 Code";

    }
    
    public String skeleton2() {
        return "Test Skeleton2 Code";
    }

    public double doubleTest1() {
        double temp = (double)5/2;
        return temp;
    }

    public int testSquare() {
        double x;
        x = Math.pow(3,2);
        return (int)x;
    }
}