from .common import run_prolog, parse_prolog_list

def handle(dimension: str, item: str):
    goal = (
        f"findall(M, (spawns_in(M, {dimension}), drops(M, {item})), L), "
        "writeln(L)"
    )
    out = run_prolog(goal)
    mobs = parse_prolog_list(out)
    if not mobs:
        print(f"В измерении {dimension} нет мобов, которые дропают {item}")
    else:
        print(f"Мобы в измерении {dimension}, которые дропают {item}:")
        for m in mobs:
            print(f"- {m}")