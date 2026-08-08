from .common import run_prolog, parse_prolog_list

def handle():
    goal = "findall(M, (dangerous(M), useful(M)), L), writeln(L)"
    out = run_prolog(goal)
    mobs = parse_prolog_list(out)
    if not mobs:
        print("Нет одновременно опасных и полезных мобов")
    else:
        print("Мобы, которые одновременно опасные и полезные:")
        for m in mobs:
            print(f"- {m}")