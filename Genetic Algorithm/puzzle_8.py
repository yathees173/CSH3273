import random

GOAL = "123456780"
MAX_FITNESS = 8
POPULATION_SIZE = 4


def fitness(board):
    score = 0
    for i in range(9):
        if board[i] != "0" and board[i] == GOAL[i]:
            score += 1
    return score


def valid_input(board):
    return len(board) == 9 and sorted(board) == list("012345678")


def get_solution(population):
    for board in population:
        if fitness(board) == MAX_FITNESS:
            return board
    return None


def selection(population):
    return sorted(population, key=fitness, reverse=True)


def get_blank_neighbors(index):
    neighbors = []

    row = index // 3
    col = index % 3

    if row > 0:
        neighbors.append(index - 3)  # up
    if row < 2:
        neighbors.append(index + 3)  # down
    if col > 0:
        neighbors.append(index - 1)  # left
    if col < 2:
        neighbors.append(index + 1)  # right

    return neighbors


def mutate(board):
    board_list = list(board)

    zero_index = board_list.index("0")
    neighbors = get_blank_neighbors(zero_index)

    swap_index = random.choice(neighbors)

    tile = board_list[swap_index]

    board_list[zero_index], board_list[swap_index] = (
        board_list[swap_index],
        board_list[zero_index],
    )

    new_board = "".join(board_list)

    info = f"swap 0(pos {zero_index + 1}) with {tile}(pos {swap_index + 1})"

    return new_board, info


def mutation(children):
    mutated = []
    mutation_info = []

    for child in children:
        new_child, info = mutate(child)
        mutated.append(new_child)
        mutation_info.append(info)

    return mutated, mutation_info


def print_board(board):
    print(board[0], board[1], board[2])
    print(board[3], board[4], board[5])
    print(board[6], board[7], board[8])


def print_iteration(iteration, population, selected, children, mutated, mutation_info):
    print(f"\nITERATION / GENERATION {iteration}")
    print("-" * 125)

    print(
        f"{'Row':<5}"
        f"{'Population':<15}"
        f"{'Fitness':<10}"
        f"{'Selection':<15}"
        f"{'Crossover':<15}"
        f"{'Child':<15}"
        f"{'Mutation':<35}"
        f"{'New Fitness':<12}"
        f"{'Status':<10}"
    )

    print("-" * 125)

    for i in range(POPULATION_SIZE):
        status = "STOP" if fitness(mutated[i]) == MAX_FITNESS else "Continue"

        print(
            f"{i + 1:<5}"
            f"{population[i]:<15}"
            f"{fitness(population[i]):<10}"
            f"{selected[i]:<15}"
            f"{'No crossover':<15}"
            f"{children[i]:<15}"
            f"{mutated[i] + ' (' + mutation_info[i] + ')':<35}"
            f"{fitness(mutated[i]):<12}"
            f"{status:<10}"
        )


def print_solution(solution):
    print("\nSOLUTION FOUND")
    print("Solution string:", solution)
    print("Fitness:", fitness(solution))

    print("\nFinal puzzle:")
    print_board(solution)

    print("\nSTOP")


def main():
    population = []

    print("8-Puzzle Genetic Algorithm")
    print("Use 0 for blank space.")
    print("Goal:", GOAL)
    print("\nEnter 4 strings:")
    print("Example:")
    print("123405786")
    print("123456708")
    print("123046785")
    print("103425786\n")

    for i in range(POPULATION_SIZE):
        while True:
            board = input(f"Enter string {i + 1}: ").strip()

            if valid_input(board):
                population.append(board)
                break
            else:
                print("Invalid input. Use digits 0 to 8 exactly once.")

    iteration = 1

    while True:
        solution = get_solution(population)

        if solution:
            print_solution(solution)
            break

        selected = selection(population)

        # For 8-puzzle, child is copied from selected parent.
        # Normal crossover is avoided because it creates invalid puzzle states.
        children = selected.copy()

        mutated, mutation_info = mutation(children)

        print_iteration(
            iteration,
            population,
            selected,
            children,
            mutated,
            mutation_info
        )

        solution = get_solution(mutated)

        if solution:
            print_solution(solution)
            break

        population = mutated
        iteration += 1


main()