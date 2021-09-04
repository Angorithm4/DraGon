package lexer;
import java.io.*;
import java.util.*;
import symbols.*;

public class Lexer {
    public static int line = 1;
    char peek = ' ';
    Hashtable words = new Hashtable(); // Single representation
    // words is a letter : tag hashtable
    void reserve(Word w) { words.put(w.lexeme, w); } // reserved letters
    public Lexer() {
        reserve(new Word("if",Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("break", Tag.BREAK));
        reserve( Word.True ); reserve( Word.False );
        reserve( Type.Int );  reserve( Type.Char );
        reserve( Type.Bool ); reserve( Type.Float );
    }
    void readch() throws IOException { 
	// heloer function
	// read next char from stdin and update peek
	peek = (char)System.in.read(); 
    }
    boolean readch(char c) throws IOException {
	// read next char and check whether it matches c
	// if match, update peek as ' ' then return true
        readch();
        if( peek != c ) return false;
        peek = ' ';
        return true;
    }

    public Token scan() throws IOException {
	// main function of object lexer
	// jump all ' ' and '\t' and '\n' and return a token(base class)
	// (Num, Real, Word)
        for( ; ; readch() ) {
            if( peek == ' ' || peek == '\t' ) continue;
            else if( peek == '\n' ) line = line + 1;
            else break;
        }
        
        // if didn't match any, jump
        switch( peek ) {
            case '&':
                if( readch('&') ) return Word.and; else return new Token('&');
            case '|':
                if( readch('|') ) return Word.or;  else return new Token('|');
            case '=':
                if( readch('=') ) return Word.eq;  else return new Token('=');
            case '!':
                if( readch('=') ) return Word.ne;  else return new Token('!');
            case '<':
                if( readch('=') ) return Word.le;  else return new Token('<');
            case '>':
                if( readch('=') ) return Word.ge;  else return new Token('>');
        }

        if ( Character.isDigit(peek) ) {
            // integer
            int v = 0;
            do {
                v = 10*v + Character.digit(peek, 10); readch();
            } while ( Character.isDigit(peek) );
            if ( peek != '.') return new Num(v); // not a float

            // float
            float x = v; float d = 10;
            for (;;) {
                readch();
                if( !Character.isDigit(peek) ) break;
                x = x + Character.digit(peek, 10) / d; d = d*10;
            }
            return new Real(x);
        }

        if ( Character.isLetter(peek) ) {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek); readch();
            } while( Character.isLetterOrDigit(peek) );
            String s = b.toString();
            Word w = (Word)words.get(s); // Hashtable
            if( w != null ) return w;
            w = new Word(s, Tag.ID);   // is an ID
            words.put(s, w);
            return w;
        }

        Token tok = new Token(peek); peek = ' '; // just a char ('[' ')')
        return tok;
    }
}
