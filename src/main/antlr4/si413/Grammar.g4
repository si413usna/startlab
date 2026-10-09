// pyt the grammar rules for your language here!
parser grammar Grammar;

tokens {
  HELLO
  }

prog
  : HELLO prog  #HelloProg
  | EOF         #EmptyProg
  ;
