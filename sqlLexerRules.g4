lexer grammar sqlLexerRules;

STAR    : '*' ;                //wildcard
SELECT  : [sS][eE][lL][eE][cC][tT] ; 
FROM    : [fF][rR][oO][mM] ; 
WHERE   : [wW][hH][eE][rR][eE] ;
AND     : [aA][nN][dD] -> skip ;
EQUALS  : '=' ;
LESS    : '<' ;
GREATER : '>' ;
LESSEQ  : '<=' ;
GREATEQ : '>=' ;
ID      : [-a-zA-Z0-9'_.]+ ;     //identifier
INT     : [0-9]+ ;               //ints
COMMA   : ',' -> skip ;
SEMI    : ';' ;                //semi colon
WS      : [ \t\r\n]+ -> skip ;       //skip white space