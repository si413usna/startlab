print("enter two dance moves")
x = input() + "m" + input()
print("Let's do the " + x)
y = ("mb" in x)
if x < "mambo":
    print(x[::-1])
    y = False
print(y)
print("a" in x and not y)
timer = ""
while not ("..." in timer):
    print("cha")
    timer = timer + "."
# some sample inputs to try:
# bo ba
# sa ba
# ta ure
# mi uet
