#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

char* reverse_string(const char* original) {
  int n = strlen(original);
  char* reversed = malloc(n+1);
  for (int i = 0; i < n; ++i) {
    reversed[i] = original[n-i-1];
  }
  reversed[n] = '\0';
  return reversed;
}

char* concat_strings(const char* lhs, const char* rhs) {
  char* result = malloc(strlen(lhs) + strlen(rhs) + 1);
  strcpy(result, lhs);
  strcat(result, rhs);
  return result;
}

char* read_line() {
  char* result = NULL;
  size_t size;
  ssize_t length = getline(&result, &size, stdin);
  if (length <= 0) {
    fprintf(stderr, "Runtime error: Got EOF when attempting to input\n");
    exit(7);
  }
  // cut off the newline character
  result[length-1] = '\0';
  return result;
}

void print_bool(bool b) {
  if (b) puts("True");
  else puts("False");
}

bool string_less(const char* lhs, const char* rhs) {
  return strcmp(lhs, rhs) < 0;
}

bool string_contains(const char* lhs, const char* rhs) {
  return (strstr(lhs, rhs) != NULL);
}
