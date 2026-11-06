parser grammar Grammar;

tokens {
  BOOLLIT, ID, EXCRETE, VOMIT, REFRIGERATE, COMPARE, HARVEST, CHOP, COOK, EAT, NOT, OR, LPAREN, RPAREN, SEMI, STRLIT, NAUSEA, HURL, RECOVER, SCOTTY, BEAMMEUP, RECIPE, PRESENT, LBRACKET, RBRACKET, COLON
}

start 
  : program EOF #begin
  ;

program
  : stmt program  #prgm
  | #emptyprgm
  ;

stmt
  : LPAREN RECIPE ID LPAREN paramList RPAREN COLON program LPAREN PRESENT expression RPAREN RPAREN #funcDef
  | LPAREN expression RPAREN #parenexpression
  | LPAREN EXCRETE expression RPAREN #printstatement
  | LPAREN REFRIGERATE ID SEMI expression RPAREN #varassign 
  | LPAREN ifStatement RPAREN #parenifstatement
  | LPAREN whileStatement RPAREN  #whileloop
  ;

argList
  : expression SEMI argList #multiarglist
  | expression  #arg
  | #noarg
  ;

paramList
  : ID SEMI paramList  #paramlist
  | ID  #param
  | #noparam
  ;

ifStatement
  : NAUSEA LPAREN expression RPAREN HURL program RECOVER program  #ifbody
  ;

whileStatement
  : SCOTTY LPAREN expression RPAREN program BEAMMEUP  #whilebody
  ;

expression
  : notExpression #notexpr
  ;

notExpression
  : thingExpression #thingexpr
  | thingExpression OR notExpression #orexpression
  ;

thingExpression
  : NOT thingExpression #notthingexpr
  | thing #thingexprvalue
  ; 

thing
  : BOOLLIT #boolliteral
  | STRLIT  #strliteral
  | ID  #identifier
  | command #cmd
  | LPAREN expression RPAREN #parenthing
  | LBRACKET ID RBRACKET LPAREN argList RPAREN  #funcCall
  ;

command
  : EAT #input
  | VOMIT expression  #reverse
  | COOK expression SEMI expression #concatenation
  | COMPARE expression SEMI expression #strcomp
  | HARVEST expression #boolconversion
  | CHOP expression SEMI expression #chop
  ;