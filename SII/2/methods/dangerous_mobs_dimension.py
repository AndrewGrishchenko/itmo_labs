from .common import run_prolog, parse_prolog_list

def handle(dimension: str):
    goal = (
        f"findall(M, (dangerous(M), spawns_in(M, {dimension})), L), "
        "writeln(L)"
    )
    out = run_prolog(goal)
    mobs = parse_prolog_list(out)
    if not mobs:
        print(f"В измерении {dimension} опасных мобов не найдено")
    else:
        print(f"Опасные мобы в измерении {dimension}:")
        for m in mobs:
            print(f"- {m}")