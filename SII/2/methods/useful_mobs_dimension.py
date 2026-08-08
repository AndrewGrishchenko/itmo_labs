from .common import run_prolog, parse_prolog_list

def handle(dimension: str):
    goal = (
        f"findall(M, (useful(M), spawns_in(M, {dimension})), L), "
        "writeln(L)"
    )
    out = run_prolog(goal)
    mobs = parse_prolog_list(out)
    if not mobs:
        print(f"В измерении {dimension} полезных мобов нет")
    else:
        print(f"Полезные мобы в измерении {dimension}:")
        for m in mobs:
            print(f"- {m}")