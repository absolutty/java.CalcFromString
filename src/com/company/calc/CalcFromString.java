package com.company.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Vie počítať všekty základne aritmetické operácie zo zadeného Stringu.
 * Napríklad:
 * - "23+6"   = "29"
 * - "5+3.14" = "8.14"
 * - "6*-2.3" = "-13.8"
 * - "-3+5"   = "2"
 *
 * umožnuje počítanie zo zátvorkami (vnorené zátvorky NIE SU podporované)
 * - "-3*(-5+3.3)"       = "5.1"
 * - "1/2*(7.1+2-35)*26" = "-336.7"
 */
public class CalcFromString {
    /**
     * Znamienka, kt. môžu byť použité v Stringu príkladu na vypočítanie
     */
    public static final char PLUS = '+';
    public static final char MINUS = '-';
    public static final char NASOBENIE = '*';
    public static final char DELENIE = '/';
    public static final char DESATINNA_CIARKA = '.';
    public static final char LAVA_ZATVORKA = '(';
    public static final char PRAVA_ZATVORKA = ')';

    private static StringBuilder upravovanyPriklad;
    /**
     * Statická metóda triedy, kt. počíta príklad zo zadeného Stringu užívateľom
     * (v prípade zlého formátovania Stringu, vyhodí Exception)
     *
     * @param prikladNaVypocitanie String, z ktorého je vypočítaný CELÝ príklad
     * @return vysledok zadaneho CELÉHO prikladu
     */
    public static String spusti(String prikladNaVypocitanie) {
        CalcFromString.upravovanyPriklad = new StringBuilder(prikladNaVypocitanie);

        while (true) { //najskor sa pocitaju priklady, kt. su v zatvorkach --> maju prednost
            try {
                //indexy zaciatku a konca zatvorky
                int zaciatokZatvorky = upravovanyPriklad.indexOf(String.valueOf(LAVA_ZATVORKA)),
                        koniecZatvorky = upravovanyPriklad.indexOf(String.valueOf(PRAVA_ZATVORKA));

                String prikladZoZatvoriek = vyberZoStringu(zaciatokZatvorky, koniecZatvorky);
                //pocitanie prikladov, PREDNOSTNE operacie: NASOBENIE, DELENIE
                String prednostneOperacieVysl = CalcFromString.spustiPocitanie(prikladZoZatvoriek, NASOBENIE, DELENIE);
                //pocitanie prikladov, DALSIE operacie: PLUS, MINUS
                String dalsieOperacieVysl = CalcFromString.spustiPocitanie(prednostneOperacieVysl, PLUS, MINUS);

                upravovanyPriklad.replace(zaciatokZatvorky, koniecZatvorky+1, dalsieOperacieVysl);
            } catch (StringIndexOutOfBoundsException ex) { //uz su vypocitane vsetky priklady v zatvorkach
                break;
            }
        }

        //pocitanie prikladov, PREDNOSTNE operacie: NASOBENIE, DELENIE
        upravovanyPriklad = new StringBuilder(CalcFromString.spustiPocitanie(upravovanyPriklad.toString(), NASOBENIE, DELENIE));
        //pocitanie prikladov, DALSIE operacie: PLUS, MINUS
        upravovanyPriklad = new StringBuilder(CalcFromString.spustiPocitanie(upravovanyPriklad.toString(), PLUS, MINUS));


        return upravovanyPriklad.toString();
    }

    /**
     * počíta čiastočný príklad, napr. jednotlivú zátvorku alebo zvyšok príkladu bez zátvoriek
     *
     * @param ciastocnyPriklad String, z ktorého je vypočítaný ČIASTOČNÝ príklad.
     * @param pocitaneZnaky array znakov o dĺžke 2, kt. obsahuje akurát počítané znaky
     * @return vysledok zadaneho ČIASTOČNÉHO prikladu.
     */
    private static String spustiPocitanie(String ciastocnyPriklad, char... pocitaneZnaky) {
        StringBuilder sb = new StringBuilder(ciastocnyPriklad);

        for (int i = 0; i < sb.length(); i++) {
            char zn = sb.charAt(i);

            //narazilo na nejaký znak (plus, minus, ...) AND nejedna sa o znak desatinnej ciarky
            if ( (zn == pocitaneZnaky[0] || (zn == pocitaneZnaky[1])) && (zn != DESATINNA_CIARKA)) {
                Cislo prveCislo = new Cislo(), druheCislo = new Cislo();

                //našlo znak MINUS na nultom indexe a dalsi znak je nejake cislo
                if ((zn == MINUS) && (i == 0) && (Character.isDigit(sb.charAt(i + 1)))) {
                    continue;
                }

                int j = i - 1;
                char prveCislozn;
                do {
                    prveCislozn = sb.charAt(j);
                    try {
                        prveCislo.pridajZnak(prveCislozn);
                    } catch (IllegalArgumentException ex) {  //pri prechádzaní čísel, narazí na znak (+, -, ...)
                        if (prveCislozn == MINUS) { //prve cislo je zaporne
                            prveCislo.jeZaporne();
                            j--;
                        }
                        break;
                    }
                    j--;
                } while (j >= 0);
                prveCislo.obratCislo(); //prveCislo je potrebne obratit (t.j z 321 bude cislo 123 pretoze bolo citane od konca)

                int k = i + 1;
                char druheCislozn;
                do {
                    druheCislozn = sb.charAt(k);
                    try {
                        druheCislo.pridajZnak(druheCislozn);
                    } catch (IllegalArgumentException ex) { //pri prechádzaní čísel, narazí na znak (+, -, ...)
                        if ((druheCislozn == MINUS) && (!Character.isDigit(sb.charAt(k - 1)))) {
                            druheCislo.jeZaporne();
                            k++;
                            continue;
                        }
                        break;
                    }
                    k++;
                } while (k < sb.length());

                String vysledok = CalcFromString.formatNum(CalcFromString.vypocitajPriklad(zn, prveCislo.getCislo(), druheCislo.getCislo()));
                sb.replace(j + 1, k, vysledok);

                i = j + 1;
            }
        }

        return sb.toString();
    }

    /**
     * vypočítanie príkladu z dvoch hodnôt na základe znamienka
     *
     * @param znamienko  znamienko, na základe ktorého sa určí aká aritmetická operácia sa má vykonať
     * @param prveCislo  prvé číslo príkladu
     * @param druheCislo druhé číslo príkladu
     * @return výsledok vypočítaneho príkladu
     */
    private static double vypocitajPriklad(char znamienko, String prveCislo, String druheCislo) {
        BigDecimal c1 = new BigDecimal(prveCislo);
        BigDecimal c2 = new BigDecimal(druheCislo);

        return switch (znamienko) {
            case PLUS -> c1.add(c2).doubleValue();
            case MINUS -> c1.subtract(c2).doubleValue();
            case NASOBENIE -> c1.multiply(c2).doubleValue();
            case DELENIE -> c1.divide(c2, 32, RoundingMode.HALF_UP).doubleValue();
            default -> throw new IllegalArgumentException("[Na zaklade tohto znamienka nie je mozne vykonat aritmeticku operaciu] " + znamienko);
        };
    }

    /**
     * pomocná metóda, kt. určí formátovanie čísla
     *
     * @param cislo číslo na formátovanie
     * @return upravené double číslo (ako String) týmto spôsobom:
     * - 3.41 return "3.41"
     * - 3.00000 return "3"
     * - 3.0041000 return "3.0041"
     * - 1.000 return "1"
     */
    private static String formatNum(double cislo) {
        if (cislo == (long) cislo)
            return String.format("%d", (long) cislo);
        else
            return String.format("%s", cislo);
    }

    /**
     * ak sú v príklade nejaké zátvorky, vyberie obsah kt. sa nachádza v nich.
     *
     * @param indexZaciatokZatvorky index na kt. ZAČÍNA zátvorka.
     * @param indexKoniecZatvorky index na kt. KONČÍ zátvorka.
     * @return vybraný príklad, napr:
     *      - 1/2*(7.1+2-35)*26 -->
     *      indexZaciatokZatvorky: 4; indexKoniecZatvorky: 13
     *      ret String: "7.1+2-35"
     *
     *      - (2.3+3)*6 -->
     *      indexZaciatokZatvorky: 0; indexKoniecZatvorky: 6
     *      ret String: "2.3+3"
     */
    private static String vyberZoStringu(int indexZaciatokZatvorky, int indexKoniecZatvorky) {
        return upravovanyPriklad.substring(indexZaciatokZatvorky + 1, indexKoniecZatvorky);
    }
}
