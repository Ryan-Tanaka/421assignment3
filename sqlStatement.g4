grammar sqlStatement;

import sqlLexerRules;

stat : query+ ;

query : select_clause from_clause SEMI
      | select_clause from_clause where_clause SEMI
      ; 

select_clause : SELECT cols 
              | SELECT STAR
              ;

from_clause : FROM tnames;

where_clause : WHERE condition ; 

cols : ID ',' cols 
     | ID
     ;   
             
tnames : ID ID',' tnames //ID ID represents something like 'Sailors S'
       | ID ',' tnames
       | ID ID
       | ID
       ;

condition : expr AND condition 
          | expr 
          ; 

expr : ID EQUALS ID
     | ID LESS ID
     | ID GREATER ID
     | ID LESSEQ ID
     | ID GREATEQ ID
     ; 

