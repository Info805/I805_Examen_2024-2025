/* --------------------------Section de Code Utilisateur---------------------*/
package fr.usmb.m1isc.compilation.examen;
import java_cup.runtime.Symbol;

%%

/* -----------------Section des Declarations et Options----------------------*/
// nom de la class a generer
%class Lexer
%unicode
%line   // numerotation des lignes
%column // numerotation caracteres par ligne

// utilisation avec CUP
// nom de la classe generee par CUP qui contient les symboles terminaux
%cupsym sym
// generation analyser lexical pour CUP
%cup

// code a ajouter dans la classe produite
%{

%}

/* definitions regulieres */
entier  =   [0-9]+
espace  =   \s 
cols    =   [clr]+

%% 
/* ------------------------Section des Regles Lexicales----------------------*/

/* regles */
{espace}+       { /* pas d'action */ }
{entier}        { return new Symbol(sym.ENTIER, Integer.parseInt(yytext())); }
"\\begin"       { return new Symbol(sym.DEBUT); }
"\\end"         { return new Symbol(sym.FIN); }
"tabular"       { return new Symbol(sym.TABULAR); }
"{"             { return new Symbol(sym.GAUCHE); }
"}"             { return new Symbol(sym.DROITE); }
"&"             { return new Symbol(sym.ET); }
"\\\\"          { return new Symbol(sym.FINLIGNE); }
{cols}          { return new Symbol(sym.COLS, yytext()); }
.               { return new Symbol(sym.ERROR); }
