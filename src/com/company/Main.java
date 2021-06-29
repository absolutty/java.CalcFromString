package com.company;

import com.company.calc.CalcFromString;

public class Main {

    public static void main(String[] args) {
        Main.testVypocitaj("23+6"); //29
        Main.testVypocitaj("5+3.14"); //8.14
        Main.testVypocitaj("6*-2.3"); //-13.8
        Main.testVypocitaj("-3+5"); //2

        Main.testVypocitaj("-3*(-5+3.3)"); //5.1
        Main.testVypocitaj("1/2*(7.1+2-35)*26"); //-336.7
    }

    private static void testVypocitaj(String priklad) {
        System.out.printf("%s= %s\n", priklad, CalcFromString.spusti(priklad));
    }
}
