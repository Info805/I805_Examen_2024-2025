package fr.usmb.m1isc.compilation.examen;

public class Arbre {

    public enum NodeType {
        COLS, ENTIER, TABULAR, LIGNE, ET
    }

    private NodeType type;
    private Object value;
    private Arbre fg, fd;
    private char format = 0;
    public Arbre(NodeType type, Object value, Arbre fg, Arbre fd) {
        this.type = type;
        this.value = value;
        this.fg = fg;
        this.fd = fd;
    }
    public Arbre(NodeType type, Object value) {
        this(type, value, null, null);
    }
    public Arbre(NodeType type) {
        this(type, null, null, null);
    }
    public NodeType getType() {
        return type;
    }
    public void setType(NodeType type) {
        this.type = type;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public Arbre getFg() {
        return fg;
    }
    public void setFg(Arbre fg) {
        this.fg = fg;
    }
    public Arbre getFd() {
        return fd;
    }
    public void setFd(Arbre fd) {
        this.fd = fd;
    }
    public char getFormat() {
        return format;
    }
    public void setFormat(char format) {
        this.format = format;
    }

    public String toString() {
        StringBuilder res = new StringBuilder("");
        switch (type) {
            case ENTIER :
                res.append(this.value.toString());
                break;
            case ET :
                res.append("&");
                break;
            case LIGNE :
                res.append("\\\\");
                break;
            case TABULAR :
                res.append("tabular");
                break;
            case COLS :
                res.append("cols:").append(value);
                break;
            default : res.append("????");
        }
        if (fg != null || fd != null) {
            res.append('(');
            if (fg != null) res.append(fg); else res.append("null");
            res.append(", ");
            if (fd != null) res.append(fd); else res.append("null");
            res.append(')');
        }
        return res.toString();
    }

    public void verifCols(String format, int line, int col) throws TabularFormatException {
        if (type == NodeType.TABULAR) {
            format = (String)(fg.getValue());
            if (fd != null) fd.verifCols(format, 0, 0);
        } else if (type == NodeType.LIGNE) {
            if (fg != null) fg.verifCols(format, line, col);
            if (fd != null) fd.verifCols(format, line+1, col);
        } else if (type == NodeType.ET) {
            if ( col + 2 > format.length()) {
                throw new TabularFormatException("Too many columns", line+1, col+2);
            } else {
                this.format = format.charAt(col);
                if (fg != null) fg.verifCols(format, line, col);
                if (fd != null) fd.verifCols(format, line, col+1);
            }
        } else if (type == NodeType.ENTIER) {
            this.format = format.charAt(col);
        }
    } 

    public StringBuffer genHtml(StringBuffer html) {
        if (type == NodeType.TABULAR) {
            html.append("<table>\n");
            if (fd != null) fd.genHtml(html);
            html.append("</table>\n");
        } else if (type == NodeType.LIGNE) {
            html.append("<tr>");
            if (fg != null) fg.genHtml(html);
            html.append("</tr>\n");
            if (fd != null) fd.genHtml(html);
        } else if (type == NodeType.ET) {
            if (fg != null) {
                fg.genHtml(html);
            } else {
                html.append("<td></td>");
            }
            if (fd != null) {
                fd.genHtml(html);
            } else {
                html.append("<td></td>");
            }
        } else if (type == NodeType.ENTIER) {
            html.append("<td");
            switch (format) {
            case 'r' :
                html.append(" align='right'"); 
                break;
            case 'l' :
                html.append(" align='left'"); 
                break;
            case 'c' :
                html.append(" align='center'"); 
                break;
            }
            html.append('>').append(this.getValue()).append("</td>");
        }
        return html;
    }
}
