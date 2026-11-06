# Lab 4.2 starter code for Hibana

This language was created by Mekenzie Espinola, who says it was inspired by

> anime and flowers and fire and pretty things for my brain

You can see the formal syntax specification in
[tokenSpec.txt](src/main/resources/si413/tokenSpec.txt)
and [Grammar.g4](src/main/antlr4/si413/Grammar.g4).

## Starter code

The starter code contains a *partial* implementation of a
working interpreter for this language, supporting some of the
basic types and operations, but nothing relating to function
definitions or function calls. You will have to spend some time
understanding how the starter code works before you can make
progress; it is based on what we have been building in previous
labs, adapted for this language.

Most significantly, you will need to make changes in:

*   `Value.java` to support Closures in addition to strings and
    booleans.
*   `Frame.java` to support lexical scoping with parent frames
*   `ASTGen.java` to add visit methods for parse tree nodes related to
    function definitions and function calls
*   `Expr.java` and `Stmt.java` to add some new AST nodes relating to
    function calls and definitions
*   `RunState.java` to add any methods or data needed to store the
    state of the running interpreter.

To be clear, you are free to change anything here to get your code to
work. The idea with the starter code is to let you focus on what's
important for *this* lab, not previous labs.

## Example programs

The [dinner\_program](dinner_program) is an implementation in this
language of the example program from last lab:

```python
def table_tracker(name):
    first = True
    total = ""

    def order(food):
        nonlocal first, total, name

        print("Table " + name + " ordering " + food)

        if first:
            first = False
            total = food
        else:
            total = total + ", " + food

        return total

    return order

def string_eq(s1, s2):
    return not (s1 < s2 or s2 < s1)

table1 = table_tracker("CS profs")
table1("Kimchi")

print("Your name?")
table2 = table_tracker(input())

done = False
t2got = "nothing"
while not done:
    print("What do you want?")
    food = input()
    if string_eq(food, "done"):
        done = True
    else:
        t2got = table2(food)

table1("Tteokbokki")
total1 = table1("Samgyeopsal")

print("We got " + total1 + " and you got " + t2got)
```

`dinner_program` *will not work yet* in the interpreter since it contains
function definitions and function calls.

The [unit3\_testprog](unit3_testprog) is an implementation in this
language of the test program from lab 3.1:

```python
print("Enter a word")
word = input()

rev_word = reverse(word)
is_palindrome = not (word < rev_word or rev_word < word)

print("Is '" + word + "' a palindrome?")
print(bool2str(is_palindrome))

print("Enter some colon-separated words")
csv_line = input()
sep = ":"
remains = csv_line

print("Tokens:")
while "" < remains:
    token = chop(remains, sep)
    print(token)

    # if token == remains, then this was the last word
    if token < remains or remains < token:
        # Strip 'token' and 'sep' from the front of 'remains' using reverse and chop
        rev_remains = reverse(remains)
        rev_prefix = reverse(token + sep)
        rev_rest = chop(rev_remains, rev_prefix)
        remains = reverse(rev_rest)
    else:
        # Clear remains to terminate the loop
        remains = ""
```

`unit3_testprog` *should work* with this starter code already, since it
doesn't contain any function definitions or function calls.
