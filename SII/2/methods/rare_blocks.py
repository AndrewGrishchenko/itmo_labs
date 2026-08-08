from .common import run_prolog, parse_prolog_list

def handle():
    goal = "findall(B, rare(B), L), writeln(L)"
    out = run_prolog(goal)
    blocks = parse_prolog_list(out)
    if not blocks:
        print("Редких блоков не найдено")
    else:
        print("Редкие блоки:")
        for b in blocks:
            print(f"- {b}")