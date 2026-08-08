from .common import run_prolog, parse_prolog_list

def handle():
    goal = "findall(M, boss(M), L), writeln(L)"
    out = run_prolog(goal)
    mobs = parse_prolog_list(out)
    if not mobs:
        print("Боссов не найдено")
    else:
        print("Боссы:")
        for m in mobs:
            print(f"- {m}")