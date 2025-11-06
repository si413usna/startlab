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
    return not s1 < s2 and not s2 < s1

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
