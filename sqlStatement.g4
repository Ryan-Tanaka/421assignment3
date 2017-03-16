grammar sqlStatement;

import sqlLexerRules;

stat : query+ ;

query : select_clause from_clause SEMI 
      | select_clause from_clause where_clause SEMI 
      ; 

select_clause : SELECT colnames 
              | SELECT STAR
              ;

from_clause : FROM tnames ;

where_clause : WHERE conditions
             ; 
     
colnames: ID+  
        ;

tnames : ID+   
       ;

conditions: expr+  
          ;

expr : ID EQUALS ID
     | ID LESS ID
     | ID GREATER ID
     | ID LESSEQ ID
     | ID GREATEQ ID
     ;



