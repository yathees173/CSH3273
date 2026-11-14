import random

MAX_FITNESS = 28
POPULATION_SIZE = 4
CHROMOSOME_LENGTH = 8


# Fitness = 28 - attacking pairs
def fitness(board):
    attacking = 0

    for i in range(CHROMOSOME_LENGTH):
        row1 = int(board[i])

        for j in range(i + 1, CHROMOSOME_LENGTH):
            row2 = int(board[j])

            # Same row attack
            if row1 == row2:
                attacking += 1

            # Diagonal attack
            if abs(row1 - row2) == abs(i - j):
                attacking += 1

    return MAX_FITNESS - attacking


def valid_input(s):
    return len(s) == 8 and all(ch in "12345678" for ch in s)


def get_solution(population):
    for board in population:
        if fitness(board) == MAX_FITNESS:
            return board
    return None


# Selection: best fitness first
def selection(population):
    return sorted(population, key=fitness, reverse=True)


# Your crossover format:
# first pair split after 3 digits
# second pair split after 5 digits
def crossover(selected):
    cut1 = 3
    cut2 = 5

    children = [None] * 4

    children[0] = selected[0][:cut1] + selected[1][cut1:]
    children[1] = selected[1][:cut1] + selected[0][cut1:]

    children[2] = selected[2][:cut2] + selected[3][cut2:]
    children[3] = selected[3][:cut2] + selected[2][cut2:]

    split_text = [
        selected[0][:cut1] + "|" + selected[0][cut1:],
        selected[1][:cut1] + "|" + selected[1][cut1:],
        selected[2][:cut2] + "|" + selected[2][cut2:],
        selected[3][:cut2] + "|" + selected[3][cut2:]
    ]

    return children, split_text


# Mutation: randomly change one digit
def mutate(board):
    board_list = list(board)

    position = random.randint(0, 7)
    new_value = str(random.randint(1, 8))

    board_list[position] = new_value

    return "".join(board_list)


def mutation(children):
    return [mutate(child) for child in children]


def print_solution(solution):
    print("\nSOLUTION FOUND")
    print("Solution string:", solution)
    print("Fitness: 28")

    print("\nColumn:  ", " ".join(str(i) for i in range(1, 9)))
    print("Row:     ", " ".join(solution))
    print("STOP")


def print_generation(generation, population, selected, split_text, children, mutated):
    print(f"\nGENERATION {generation}")
    print("-" * 115)

    print(
        f"{'Row':<5}"
        f"{'Population':<15}"
        f"{'Fitness':<10}"
        f"{'Selection':<15}"
        f"{'Crossover Split':<20}"
        f"{'Child':<15}"
        f"{'Mutation':<15}"
    )

    print("-" * 115)

    for i in range(POPULATION_SIZE):
        mark = "  <-- SOLUTION" if fitness(mutated[i]) == MAX_FITNESS else ""

        print(
            f"{i + 1:<5}"
            f"{population[i]:<15}"
            f"{fitness(population[i]):<10}"
            f"{selected[i]:<15}"
            f"{split_text[i]:<20}"
            f"{children[i]:<15}"
            f"{mutated[i]:<15}"
            f"{mark}"
        )


def main():
    population = []

    print("Enter 4 strings, each string has 8 digits from 1 to 8.")
    print("Example:")
    print("32752411")
    print("24748152")
    print("32738417")
    print("24715152\n")

    for i in range(POPULATION_SIZE):
        while True:
            value = input(f"Enter string {i + 1}: ").strip()

            if valid_input(value):
                population.append(value)
                break
            else:
                print("Invalid input. Enter exactly 8 digits from 1 to 8.")

    generation = 1

    while True:
        solution = get_solution(population)

        if solution:
            print_solution(solution)
            break

        selected = selection(population)
        children, split_text = crossover(selected)
        mutated = mutation(children)

        print_generation(
            generation,
            population,
            selected,
            split_text,
            children,
            mutated
        )

        solution = get_solution(mutated)

        if solution:
            print_solution(solution)
            break

        population = mutated
        generation += 1


main()