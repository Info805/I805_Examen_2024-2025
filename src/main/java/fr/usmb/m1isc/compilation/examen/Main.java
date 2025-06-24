package fr.usmb.m1isc.compilation.examen;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) throws Exception {
        Lexer lexer;
        if (args.length > 0)
            lexer = new Lexer(new FileReader(args[0]));
        else {
            Reader in0 = new StringReader("\\begin{tabular}{clr}\r\n" + 
                    "12 & 256 & 3 \\\\\r\n" + 
                    "32 & & 61 \\\\\r\n" + 
                    "123 & 12 \\\\\r\n" + 
                    "\\end{tabular}\r\n" + 
                    "");
            Reader in = new StringReader(
                    "\\begin{tabular}{rr}\r\n" + 
                    "1 & 2  \\\\\r\n" + 
                    "3 & 4  \\\\\r\n" + 
                    "\\end{tabular}\r\n" );
            lexer = new Lexer(in0);
            //lexer = new Lexer(new InputStreamReader(System.in));
        }
        @SuppressWarnings("deprecation")
        parser p = new parser(lexer);
        Symbol res = p.parse();
        Arbre a = (Arbre)res.value;
        System.out.println(a);
        a.verifCols("", 0, 0);
        StringBuffer html = new StringBuffer();
        a.genHtml(html);
        System.out.println(html);
    }
}
