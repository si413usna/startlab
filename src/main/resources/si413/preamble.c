#include <stdlib.h>
#include <string.h>

char* reverse_string(const char* original) {
  int n = strlen(original);
  char* reversed = malloc(n+1);
  for (int i = 0; i < n; ++i) {
    reversed[i] = original[n-i-1];
  }
  reversed[n] = '\0';
  return reversed;
}
