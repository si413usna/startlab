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
