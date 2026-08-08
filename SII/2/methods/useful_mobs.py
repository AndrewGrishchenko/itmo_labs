from .common import run_prolog, parse_prolog_list

def handle():
    goal = "findall(M, useful(M), L), writeln(L)"
    out = run_prolog(goal)
    mobs = parse_prolog_list(out)
    if not mobs:
        print("Полезных мобов не найдено")
    else:
        print("Полезные мобы:")
        for m in mobs:
            print(f"- {m}")