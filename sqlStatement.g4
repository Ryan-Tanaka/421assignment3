grammar sqlStatement;

import sqlLexerRules;

stat : query+ ; 

query : select_clause from_clause SEMI               #selectFrom
      | select_clause from_clause where_clause SEMI  #selectFromWhere
      ; 

select_clause : SELECT colnames #selectCols
              | SELECT STAR     #selectAll
              ;

from_clause : FROM tnames ; #fromTnames

where_clause : WHERE conditions #whereConditions
             ; 
     
colnames: ID+ #colnames 
        ;

tnames : ID+ #tnames  
       ;

conditions: expr+ #conditions  
          ;

expr : ID EQUALS ID   #eqExp
     | ID LESS ID     #lessExp
     | ID GREATER ID  #grtrExp
     | ID LESSEQ ID   #lessEqExp
     | ID GREATEQ ID  #grtrEqExp
     ;



