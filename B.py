OPERATION = ["!", "|", ">", "&", "(", ")"]


def parse_expression(expression):
    arr = []
    for i in range(0, len(expression)):
        a = expression[i]
        for j in a:
            arr.append(j)

    pars_arr = []
    current_variable = ''
    array_variable = []

    for i in range(0, len(arr)):
        if arr[i] == "-":
            continue

        elif arr[i] not in OPERATION:
            current_variable += arr[i]

        else:
            pars_arr.append(current_variable)
            array_variable.append(current_variable)
            current_variable = ''

        if arr[i] in OPERATION:
            pars_arr.append(arr[i])

    pars_arr.append(current_variable)
    array_variable.append(current_variable)

    while pars_arr.count(''):
        pars_arr.remove('')

    while array_variable.count(''):
        array_variable.remove('')

    array_variable = list(set(array_variable))
    array_variable.sort()

    for i in range(0, len(array_variable)):
        for j in range(0, len(pars_arr)):
            if array_variable[i] == pars_arr[j]:
                pars_arr[j] = str(i)

    truth_table = create_bin_table(len(array_variable))

    return pars_arr, truth_table


def rpn(expression):
    arr = parse_expression(expression)[0]
    operations = []
    result = []

    for symb in arr:
        if symb == "(":
            operations.append(symb)

        elif symb in OPERATION:
            if not operations:
                operations.append(symb)

            elif symb == ")":
                while True:
                    current = operations[len(operations) - 1]
                    operations.pop()

                    if current == "(":
                        break
                    result.append(current)

            elif priority(operations[len(operations) - 1]) <= priority(symb):
                operations.append(symb)

            else:
                while True:
                    if not operations:
                        break

                    current = operations[len(operations) - 1]
                    if priority(current) <= priority(symb):
                        break

                    result.append(current)
                    operations.pop()
                operations.append(symb)

        else:
            result.append(symb)

    while operations:
        current = operations[len(operations) - 1]
        result.append(current)
        operations.pop()

    print(result)
    return result


def priority(operator):
    if operator == "(":
        return 0
    elif operator == ">":
        return 1
    elif operator == "|":
        return 2
    elif operator == "&":
        return 3
    elif operator == "!":
        return 4


def negation(A):
    result = []
    for i in range(0, len(A)):
        if A[i] == 1:
            result.append(0)

        else:
            result.append(1)

    return result


def simple_calculate(A, B, operator):
    result = []
    if operator == '>':
        for i in range(0, len(A)):
            if A[i] <= B[i]:
                result.append(1)

            else:
                result.append(0)
    elif operator == '&':
        for i in range(0, len(A)):
            if A[i] == B[i] and A[i] != 0:
                result.append(1)

            else:
                result.append(0)
    elif operator == '|':
        for i in range(0, len(A)):
            if A[i] == 1 or B[i] == 1:
                result.append(1)

            else:
                result.append(0)

    return result


def calculate_result_table(expression):
    arr, table = rpn(expression), parse_expression(expression)[1]
    stack = []

    for i in range(0, len(arr)):
        if arr[i].isdigit():
            stack.append(table[int(arr[i])])

        elif arr[i] == '!':
            a1 = stack.pop()
            stack.append(negation(a1))

        else:
            a2 = stack.pop()
            a1 = stack.pop()
            stack.append(simple_calculate(a1, a2, arr[i]))

    try:
        return stack.pop()
    except:
        return [0]


def create_bin_table(size):
    arr = []
    for i in range(0, size):
        arr.append(([0] * ((2 ** size) // (2 ** (i + 1))) + [1] * ((2 ** size) // (2 ** (i + 1)))) * 2 ** i)

    return arr


def print_answer(expression):
    result = calculate_result_table(expression)
    zero_count = result.count(0)
    unit_count = result.count(1)

    if zero_count == 0:
        print("Valid")
    elif unit_count == 0:
        print("Unsatisfiable")
    else:
        print(f"Satisfiable and invalid, {unit_count} true and {zero_count} false cases")


s = str(input()).replace(" ", "").replace("!!", "").replace("\t", "").replace("\r", "").split()

import time
start_time = time.time()

print_answer(s)
print("--- %s seconds ---" % (time.time() - start_time))
