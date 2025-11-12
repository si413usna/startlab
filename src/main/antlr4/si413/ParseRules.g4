parser grammar ParseRules;

// grammar for Winner Winner Chicken Dinner Language
tokens {IF,RETURN, CONCAT, WHILE, FUNCTION, COMMA, LARROW, RARROW, LPREN, RPREN, LBRACK, RBRACK, NOT, OP, BOOL, PRINT, INPUT, REV, LIT, ID, IGNORE}

prog
  : stat prog #RegularProg
  | EOF #EmptyProg
  ;

stat
  : PRINT expr PRINT #PrintStat
  | ID LARROW expr RARROW ID #IDStat
  | IF LPREN expr RPREN bracket #IFStat
  | WHILE LPREN expr RPREN bracket #WHILEStat
  | FUNCTION ID LPREN arguments RPREN bracket #FUNCStat
  | FUNCTION ID LPREN RPREN bracket #FUNCVOIDStat
  | ID LPREN RPREN #FUNCcallVoidStat
  | ID LPREN arguments RPREN #FUNCcallStat
  | RETURN expr #ReturnStat
  ;

arguments
  : ID argumentsRepeat #ArgArg
  ;

argumentsRepeat
  : COMMA ID argumentsRepeat #ArgRepeat
  | #Emptyargument
  ;

bracket
  : LBRACK inner RBRACK #LRBracket
  ;

inner
  : stat inner #InnerInner
  | #EmptyInner
  ;

expr
  : LIT #LitExpr
  | INPUT #InputExpr
  | REV LARROW expr RARROW REV #RevExpr
  | ID #IDExpr
  | BOOL #BoolLitExpr
  | expr CONCAT expr #ConcatExpr
  | ID LPREN arguments RPREN  #IDLRExpr
  | ID LPREN RPREN  #IDLRVoidExpr
  | LPREN expr RPREN #ParenExpr
  | NOT expr #NotExpr
  | expr OP expr #OpExpr
  ;
