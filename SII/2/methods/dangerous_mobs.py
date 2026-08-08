from .common import run_prolog, parse_prolog_list

def handle():
    goal = "findall(M, dangerous(M), L), writeln(L)"
    out = run_prolog(goal)
    mobs = parse_prolog_list(out)
    if not mobs:
        print("Опасных мобов не найдено")
    else:
        print("Опасные мобы:")
        for m in mobs:
            print(f"- {m}")