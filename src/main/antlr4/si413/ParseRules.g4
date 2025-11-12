// Simple Grammer
parser grammar ParseRules;

tokens { ASSIGN, PRINT, TYPESTR, TYPEBOOL, TYPEVOID, TYPEFUNC, DEF, RETURN, WHILE, IF, CONCAT, REV, IN, CON, LT, AND, OR, NOT, OPENEX, CLOSEX, BOOL, STR, ID }

prog
  : stmtList EOF #RegProg
  ;

stmtList
  : stmt stmtList #RegStmtList
  |		  #EmptyStmt
  ;

stmt
  : ASSIGN OPENEX TYPESTR ID strEx CLOSEX #AssignStr
  | ASSIGN OPENEX TYPEBOOL ID boolEx CLOSEX #AssignBool
  | ASSIGN OPENEX TYPEFUNC ID funcEx CLOSEX #AssignFun
  | PRINT OPENEX TYPESTR strEx CLOSEX #PrintStr
  | PRINT OPENEX TYPEBOOL boolEx CLOSEX #PrintBool
  | IF OPENEX boolEx OPENEX stmtList CLOSEX OPENEX stmtList CLOSEX CLOSEX #IfElse
  | WHILE OPENEX boolEx stmtList CLOSEX #WhileLoop
  | DEF TYPESTR ID OPENEX paramList CLOSEX OPENEX stmtList RETURN strEx CLOSEX #DefStrFun
  | DEF TYPEBOOL ID OPENEX paramList CLOSEX OPENEX stmtList RETURN boolEx CLOSEX #DefBoolFun
  | DEF TYPEVOID ID OPENEX paramList CLOSEX OPENEX stmtList CLOSEX #DefVoidFun
  | DEF TYPEFUNC ID OPENEX paramList CLOSEX OPENEX stmtList RETURN funcEx CLOSEX #DefFunFun
  | funcEx OPENEX argList CLOSEX #VoidFunCall
  ;

paramList
  : param paramList #RegParam
  |                 #EmptyParam
  ;

param
  : TYPEBOOL ID #BoolParam
  | TYPESTR ID #StrParam
  | TYPEFUNC ID #FunParam
  ;

argList
  : arg argList #RegArg
  |		#EmptyArg
  ;

arg
  : TYPESTR strEx #StrArg
  | TYPEBOOL boolEx #BoolArg
  | TYPEFUNC funcEx #FunArg
  ;

strEx
  : OPENEX strEx CLOSEX #StrIdentity
  | IN OPENEX CLOSEX #Input
  | REV OPENEX strEx CLOSEX #Reverse
  | CONCAT OPENEX strEx strEx CLOSEX #Concat
  | STR #StrLit
  | ID #StrVar
  | funcEx OPENEX argList CLOSEX #StrFunCall
  ;

boolEx
  : AND OPENEX boolEx boolEx CLOSEX #And
  | OR OPENEX boolEx boolEx CLOSEX #Or
  | NOT OPENEX boolEx CLOSEX #Not
  | CON OPENEX strEx strEx CLOSEX #Contains
  | LT OPENEX strEx strEx CLOSEX #LessThan
  | ID #BoolVar
  | BOOL #BoolLit
  | OPENEX boolEx CLOSEX #BoolIdentity
  | funcEx OPENEX argList CLOSEX #BoolFunCall
  ;

funcEx
  : ID #FuncName
  | funcEx OPENEX argList CLOSEX #FunFunCall
  ;
