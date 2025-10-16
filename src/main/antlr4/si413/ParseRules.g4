// grammar for small subset of Python
parser grammar ParseRules;

tokens {
  CONCAT, LP, RP, PRINT, BOOLLIT, NOT, STRLIT
  }

prog
  : stmt prog  #RegularProg
  | EOF        #EmptyProg
  ;

stmt
  : PRINT LP strExpr RP   #StringPrint
  | PRINT LP boolExpr RP  #BoolPrint
  ;

strExpr
  : STRLIT                  #StrLit
  | strExpr CONCAT strExpr  #Concat
  ;

boolExpr
  : BOOLLIT       #BoolLit
  | NOT boolExpr  #BoolNot
  ;
