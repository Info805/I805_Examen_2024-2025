package fr.usmb.m1isc.compilation.examen;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.usmb.m1isc.compilation.examen.Arbre.NodeType;

public class BasicTest {
    
    @Nested
    @DisplayName("Parser tests")
    class ParserTest {
        
        Reader in;
        PrintStream out;
        PrintStream err;
        
        parser createParser(Reader in, PrintStream out, PrintStream err) throws Exception {
            System.setOut(out);
            System.setErr(err);
            Lexer lexer = new Lexer(in);
            @SuppressWarnings("deprecation")
            parser p = new parser(lexer);
            return p;
        }
        
        parser createParser(Reader in) throws Exception {
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(baout, true);
            ByteArrayOutputStream baerr = new ByteArrayOutputStream();
            PrintStream err = new PrintStream(baerr, true);
            System.setOut(out);
            System.setErr(err);
            Lexer lexer = new Lexer(in);
            @SuppressWarnings("deprecation")
            parser p = new parser(lexer);
            return p;
        }
        
        @Nested
        class OkTest {
            @Test
            void simpleTable () throws Exception {
                String arbreExpected = "tabular(cols:rr, \\\\(&(1, 2), \\\\(&(3, 4), null)))";
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" + 
                        "1 & 2  \\\\\r\n" + 
                        "3 & 4  \\\\\r\n" + 
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                Arbre a = (Arbre)p.parse().value;
                assertEquals("", baout.toString());
                assertEquals("", baerr.toString());
                assertEquals(arbreExpected, a.toString());
            }
            
            
            @Test
            void spacesAreOptionnal () throws Exception {
                String arbreExpected = "tabular(cols:rr, \\\\(&(1, 2), \\\\(&(3, 4), null)))";
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}1&2\\\\3&4\\\\\\end{tabular}");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                Arbre a = (Arbre)p.parse().value;
                assertEquals("", baout.toString());
                assertEquals("", baerr.toString());
                assertEquals(arbreExpected, a.toString());
            }
            
            @Test
            void tableCanHaveEmptyCells () throws Exception {
                String arbreExpected = "tabular(cols:clr, \\\\(&(12, &(256, 3)), \\\\(&(32, &(null, 61)), \\\\(&(123, 12), null))))";
                Reader in = new StringReader("\\begin{tabular}{clr}\r\n" + 
                        "12 & 256 & 3 \\\\\r\n" + 
                        "32 & & 61 \\\\\r\n" + 
                        "123 & 12 \\\\\r\n" + 
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                Arbre a = (Arbre)p.parse().value;
                assertEquals("", baout.toString());
                assertEquals("", baerr.toString());
                assertEquals(arbreExpected, a.toString());
            }
            
            @Test
            void tableCanBeEmpty () throws Exception {
                String arbreExpected = "tabular(cols:rr, null)";
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" +
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                Arbre a = (Arbre)p.parse().value;
                assertEquals("", baout.toString());
                assertEquals("", baerr.toString());
                assertEquals(arbreExpected, a.toString());
            }
            
            @Test
            void tableCanHaveEmptyRow () throws Exception {
                String arbreExpected = "tabular(cols:rr, \\\\)";
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" +
                        "\\\\\r\n" +
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                Arbre a = (Arbre)p.parse().value;
                assertEquals("", baout.toString());
                assertEquals("", baerr.toString());
                assertEquals(arbreExpected, a.toString());
            }
        }
        @Nested
        class NotOKTest {
            @Test
            void tableMustHaveAtLeastACol () throws Exception {
                Reader in = new StringReader(
                        "\\begin{tabular}{}\r\n" +
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                assertThrows(Exception.class, ()->p.parse());
                assertTrue(baerr.toString().contains("Syntax error"));
            }
            
            @Test
            void cellContentMustBeIntegers () throws Exception {
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" +
                        "1 & a \\\\\r\n" +
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                assertThrows(Exception.class, ()->p.parse());
                assertTrue(baout.toString().length() > 0);
                assertTrue(baerr.toString().contains("Syntax error"));
            }
            
            @Test
            void cellMustBeSeparatedWithAmpersand () throws Exception {
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" +
                        "1 2 \\\\\r\n" +
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                assertThrows(Exception.class, ()->p.parse());
                assertTrue(baout.toString().length() > 0);
                assertTrue(baerr.toString().contains("Syntax error"));
            }
        }
        @Nested
        class verifyColsTest {
            @Test
            void simpleTable () throws Exception {
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" + 
                        "1 & 2  \\\\\r\n" + 
                        "3 & 4  \\\\\r\n" + 
                        "\\end{tabular}\r\n");
                parser p = createParser(in);
                assertDoesNotThrow(()-> ((Arbre)p.parse().value).verifCols("", 0, 0));
            }
            
            @Test
            void formatIsSetOnCell () throws Exception {
                String arbreExpected = "tabular(cols:clr, \\\\(&(1, &(2, 3)), \\\\(&(null, 5), null)))";
                Reader in = new StringReader(
                        "\\begin{tabular}{clr}\r\n" + 
                        "1 & 2 & 3 \\\\\r\n" + 
                        " & 5  \\\\\r\n" + 
                        "\\end{tabular}\r\n");
                parser p = createParser(in);
                Arbre a = (Arbre)p.parse().value;
                a.verifCols(null, 0, 0);
                assertEquals(arbreExpected, a.toString());
                Arbre cell11 = a.getFd().getFg().getFg();
                Arbre cell12 = a.getFd().getFg().getFd().getFg();
                Arbre cell13 = a.getFd().getFg().getFd().getFd();
                Arbre cell21 = a.getFd().getFd().getFg().getFg();
                Arbre cell22 = a.getFd().getFd().getFg().getFd();
                assertEquals(NodeType.ENTIER, cell11.getType());
                assertEquals(1, cell11.getValue());
                assertEquals('c', cell11.getFormat());
                assertEquals(NodeType.ENTIER, cell12.getType());
                assertEquals(2, cell12.getValue());
                assertEquals('l', cell12.getFormat());
                assertEquals(NodeType.ENTIER, cell13.getType());
                assertEquals(3, cell13.getValue());
                assertEquals('r', cell13.getFormat());
                assertEquals(null, cell21);
                assertEquals(NodeType.ENTIER, cell22.getType());
                assertEquals(5, cell22.getValue());
                assertEquals('l', cell22.getFormat());
            }
            
            @Test
            void tableCanHaveEmptyCells () throws Exception {
                Reader in = new StringReader("\\begin{tabular}{clr}\r\n" + 
                        "12 & 256 & 3 \\\\\r\n" + 
                        "32 & & 61 \\\\\r\n" + 
                        "123 & 12 \\\\\r\n" + 
                        "\\end{tabular}\r\n");
                parser p = createParser(in);
                assertDoesNotThrow(()-> ((Arbre)p.parse().value).verifCols("", 0, 0));
            }
            
            @Test
            void tableCanBeEmpty () throws Exception {
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" +
                        "\\end{tabular}\r\n");
                parser p = createParser(in);
                assertDoesNotThrow(()-> ((Arbre)p.parse().value).verifCols("", 0, 0));
            }
            
            @Test
            void tableCanHaveEmptyRow () throws Exception {
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" +
                        "\\\\\r\n" +
                        "\\end{tabular}\r\n");
                parser p = createParser(in);
                assertDoesNotThrow(()-> ((Arbre)p.parse().value).verifCols("", 0, 0));
            }

            @Test
            void tableCannotHaveTooManyColumns () throws Exception {
                String expectedException = TabularFormatException.class.getName() + ": Too many columns line 2 col 3";
                Reader in = new StringReader(
                        "\\begin{tabular}{rr}\r\n" + 
                        "1 & 2  \\\\\r\n" + 
                        "3 & 4 & 5 \\\\\r\n" + 
                        "\\end{tabular}\r\n");
                parser p = createParser(in);
                TabularFormatException e = assertThrows(TabularFormatException.class, ()-> ((Arbre)p.parse().value).verifCols("", 0, 0));
                assertEquals(expectedException, e.toString());
            }
        }
        @Nested
        class htmlGenerationTest {
            @Test
            void htmlGeneration () throws Exception {
                String htmlExpected = "<table>\r\n"
                        + "<tr><td align=\"center\">12</td><td align=\"left\">256</td><td align=\"right\">3</td></tr>\r\n"
                        + "<tr><td align=\"center\">32</td><td></td><td align=\"right\">61</td></tr>\r\n"
                        + "<tr><td align=\"center\">123</td><td align=\"left\">12</td></tr>\r\n"
                        + "</table>";
                Reader in = new StringReader("\\begin{tabular}{clr}\r\n" + 
                        "12 & 256 & 3 \\\\\r\n" + 
                        "32 & & 61 \\\\\r\n" + 
                        "123 & 12 \\\\\r\n" + 
                        "\\end{tabular}\r\n");
                ByteArrayOutputStream baout = new ByteArrayOutputStream();
                PrintStream out = new PrintStream(baout, true);
                ByteArrayOutputStream baerr = new ByteArrayOutputStream();
                PrintStream err = new PrintStream(baerr, true);
                parser p = createParser(in, out, err);
                Arbre a = (Arbre)p.parse().value;
                a.verifCols("", 0, 0);
                String actualHtml = a.genHtml(new StringBuffer()).toString();
                assertEquals(htmlExpected.trim().replaceAll(">\\s+<","><").replaceAll("'","\""), actualHtml.trim().replaceAll(">\\s+<","><").replaceAll("'","\""));
            }
        }
    }
}
