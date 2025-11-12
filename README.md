# Lab 4.2 starter code for EasyAs language

This language was created by Kaj Dongre.

## Example programs and test files

Find them in this top-level directory under
[dinner\_program](dinner_program), [example\_program](example_program),
and related input/output test files.

## Starter code

The starter code contains a *partial* implementation of a
working interpreter for this language, supporting some of the
basic types and operations, but definitely nothing relating
to function definitions or function calls. You will have to
spend some time understanding how the starter code works
before you can make progress; it is based on what we have been
building in previous labs, adapted for this language.

Most significantly, you will need to make changes in:

*   `Value.java` to support Closures in addition to strings and
    booleans.
*   `Frame.java` to support lexical scoping with parent frames
*   `ASTGen.java` to add visit methods for parse tree nodes related to
    function definitions and function calls
*   `Expr.java` and `Stmt.java` to add some new AST nodes relating to
    function calls and definitions, and perhaps modify the existing AST
    nodes.
*   `Interpreter.java` to add any methods or data needed to store the
    state of the running interpreter.

To be clear, you are free to change anything here to get your code to
work. The idea with the starter code is to let you focus on what's
important for *this* lab, not previous labs.
