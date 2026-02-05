/* --------------------------Section de Code Utilisateur---------------------*/
package fr.usmb.m1isc.compilation.examen;
import java_cup.runtime.Symbol;

%%

/* -----------------Section des Declarations et Options----------------------*/
// nom de la class a generer
%class TableLexer
%unicode
%line   // numerotation des lignes
%column // numerotation caracteres par ligne

// utilisation avec CUP
// nom de la classe generee par CUP qui contient les symboles terminaux
%cupsym TableParserSym
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
{entier}        { return new Symbol(TableParserSym.ENTIER, Integer.parseInt(yytext())); }
"\\begin"       { return new Symbol(TableParserSym.DEBUT); }
"\\end"         { return new Symbol(TableParserSym.FIN); }
"tabular"       { return new Symbol(TableParserSym.TABULAR); }
"{"             { return new Symbol(TableParserSym.GAUCHE); }
"}"             { return new Symbol(TableParserSym.DROITE); }
"&"             { return new Symbol(TableParserSym.ET); }
"\\\\"          { return new Symbol(TableParserSym.FINLIGNE); }
{cols}          { return new Symbol(TableParserSym.COLS, yytext()); }
.               { return new Symbol(TableParserSym.ERROR); }
