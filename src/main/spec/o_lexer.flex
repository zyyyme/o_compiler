package o_project_compiler;

import java_cup.runtime.Symbol;
import  o_project_compiler.loggers.*;

%%

%class Lexer
%cup
%line
%column

%{
	LexicalErrorLogger lexicalErrorLogger = new LexicalErrorLogger();

	private Symbol newSymbol(int type) {
		return new Symbol(type, yyline + 1, yycolumn + 1);
	}

	private Symbol newSymbol(int type, Object value) {
		return new Symbol(type, yyline + 1, yycolumn + 1, value);
	}
%}

%eofval{
	return newSymbol(sym.EOF);
%eofval}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Whitespace     = {LineTerminator}|[\t\f\b ]

SingleLineComment = "//"{InputCharacter}*{LineTerminator}?
Comment = {SingleLineComment}

DecIntegerLiteral = [0-9]+
PrintableCharLiteral = "'"[ -~]"'"
BooleanLiteral = "true"|"false"
Identifier = [a-zA-Z][a-zA-Z0-9_]*

%%

<YYINITIAL> {
	{Whitespace}           { /* ignore */ }
	
	"program"              { return newSymbol(sym.PROGRAM); }
	"break"                { return newSymbol(sym.BREAK); }
	"class"                { return newSymbol(sym.CLASS); }
	"else"                 { return newSymbol(sym.ELSE); }
	"if"                   { return newSymbol(sym.IF); }
	"new"                  { return newSymbol(sym.NEW); }
	"print"                { return newSymbol(sym.PRINT); }
	"read"                 { return newSymbol(sym.READ); }
	"return"               { return newSymbol(sym.RETURN); }
	"void"                 { return newSymbol(sym.VOID); }
	"do"                   { return newSymbol(sym.DO); }
	"while"                { return newSymbol(sym.WHILE); }
	"extends"              { return newSymbol(sym.EXTENDS);}


    "this"               {return newSymbol(sym.THIS); }





	"="                    { return newSymbol(sym.ASSIGN); }
	
	";"                    { return newSymbol(sym.SEMI); }
	","                    { return newSymbol(sym.COMMA); }
	"."                    { return newSymbol(sym.DOT); }
	
	"("                    { return newSymbol(sym.LPAREN); } // Parentheses (Oble (male) zagrade).
	")"                    { return newSymbol(sym.RPAREN); }
	"["                    { return newSymbol(sym.LBRACKET); } // (Square) Brackets (Uglaste (srednje) zagrade).
	"]"                    { return newSymbol(sym.RBRACKET); }
	"{"                    { return newSymbol(sym.LBRACE); } // (Curly) Braces (Vitičaste (velike) zagrade).
	"}"                    { return newSymbol(sym.RBRACE); }
	
	{Comment}              { /* ignore */ }
	
	{DecIntegerLiteral}    { return newSymbol(sym.INT, new Integer(yytext())); }
	{BooleanLiteral}       { return newSymbol(sym.BOOL, Boolean.valueOf(yytext())); }  
	{PrintableCharLiteral} { return newSymbol(sym.CHAR, new Character(yytext().charAt(1))); }
	
	{Identifier}           { return newSymbol(sym.IDENT, yytext()); }
	
	[^]                    { lexicalErrorLogger.log(yytext(), yyline + 1, yycolumn + 1); return newSymbol(sym.ERROR); }
}