preamble.ll: preamble.c
	clang-18 -ferror-limit=5 -S -emit-llvm -O1 -Wall -Wextra -fno-discard-value-names $< -o $@