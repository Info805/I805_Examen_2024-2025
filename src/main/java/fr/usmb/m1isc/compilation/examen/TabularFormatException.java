package fr.usmb.m1isc.compilation.examen;

public class TabularFormatException extends Exception {
    private int line, col;

    public TabularFormatException(int line, int col) {
        this("Tabular format exception", line, col);
    }
    public TabularFormatException(String message, int line, int col) {
        super(message);
        this.line = line;
        this.col = col;
    }
    public String toString() {
         return super.toString() + " line "+ line + " col " + col;
    }
}
