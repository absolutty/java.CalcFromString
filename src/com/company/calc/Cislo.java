package com.company.calc;

public class Cislo {
    private boolean jeKladne;
    private final StringBuilder obsahCisla;

    public Cislo() {
        this.jeKladne = true; //defaultne je číslo kladné
        this.obsahCisla = new StringBuilder();
    }

    /**
     * Prida k obsahCisla novy znak na jeho koniec.
     * V prípade nesprávneho formátovania čísla (Musí byť Character.isDigit() alebo desatinná čiarka)
     * @param pridavanyZnak znak, kt. bude pridaný do čísla napr. 1 pridám 2, výsledok 12
     */
    public void pridajZnak(char pridavanyZnak) throws IllegalArgumentException{
        if (Character.isDigit(pridavanyZnak) || (pridavanyZnak == CalcFromString.DESATINNA_CIARKA) ) {
            this.obsahCisla.append(pridavanyZnak);
        } else {
            throw new IllegalArgumentException(String.format("[Cislo nemoze obsahovat znak kt. nie je digit], pridavany znak: %c]", pridavanyZnak));
        }
    }

    /**
     * Pouziva sa v pripade ze urcujeme prve cislo, lebo to je pridavane od konca.
     */
    public void obratCislo() {
        this.obsahCisla.reverse();
    }

    /**
     * Pri vytváraní čísla, zistím že obsahuje záporné znamienko.
     */
    public void jeZaporne() {
        this.jeKladne = false;
    }

    /**
     * @return reprezentaciu cisla vytvoreneho pomocou StringBuildera + jeho znamienko (kladne alebo zaporne).
     */
    public String getCislo() {
        if (!jeKladne) {
            return CalcFromString.MINUS + this.obsahCisla.toString();
        }
        return obsahCisla.toString();
    }

    public boolean jeKladne() {
        return jeKladne;
    }
}
