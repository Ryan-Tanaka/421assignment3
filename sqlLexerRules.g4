lexer grammar sqlLexerRules;

SEMI    : ';' ;                //semi colon
STAR    : '*' ;                //wildcard
SELECT  : [sS][eE][lL][eE][cC][tT] ; 
FROM    : [fF][rR][oO][mM] ; 
WHERE   : [wW][hH][eE][rR][eE] ;
AND     : [aA][nN][dD] ;
EQUALS  : '=' ;
LESS    : '<' ;
GREATER : '>' ;
LESSEQ  : '<=' ;
GREATEQ : '>=' ;
ID      : [a-zA-Z0-9.'"-]+ ;         //identifier
INT     : [0-9]+ ;               //ints
NEWLINE : '\r' ? '\n' -> skip;   //skip new lines
WS      : [ \t]+ -> skip ;       //skip white space

