parser grammar Grammar;

tokens { STRING, BOOL, EMIT, ABSORB, PROBE, OTHERWISE, ORBIT, VAR, REVERSE, SEVER, INVERT, REVEALBOOL, CONCAT, OR, LESSTHAN, LP, RP, LB, RB, FORGE, EJECT, COMMA }

prog
  : program EOF                               #Start
  ;

program
  : statement program                         #ProgStmt
  |                                           #EmptyProg
  ;

statement
  : expr CONCAT EMIT                          #EmitStmt
  | expr CONCAT ABSORB VAR                    #AssignStmt
  | ABSORB VAR                                #InputStmt
  | expr CONCAT PROBE block                   #IfStmt
  | expr CONCAT PROBE block OTHERWISE block   #IfElseStmt
  | expr CONCAT ORBIT block                   #WhileStmt
  | FORGE VAR LP params RP block              #FuncDef
  | expr CONCAT EJECT                         #ReturnStmt
  | EJECT                                     #EmptyReturn
  | expr LP args RP                           #CallStmt
  ;

block
  : LB program RB                             #Body
  ;

expr
  : expr LP args RP                           #Call
  | expr REVERSE                              #Reverse
  | expr INVERT                               #Invert
  | expr REVEALBOOL                           #RevealBool
  | expr SEVER LP expr RP                     #Sever
  | expr CONCAT expr                          #Concat
  | expr LESSTHAN expr                        #Less
  | expr OR expr                              #Or
  | LP expr RP                                #Paren
  | STRING                                    #Str
  | BOOL                                      #Bool
  | VAR                                       #Var
  ;

params
  : VAR COMMA params                          #ParamList
  | VAR                                       #ParamSingle
  |                                           #NoParams
  ;

args
  : expr COMMA args                           #ArgList
  | expr                                      #ArgSingle
  |                                           #NoArgs
  ;
