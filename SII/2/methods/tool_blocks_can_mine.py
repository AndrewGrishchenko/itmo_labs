from .common import run_prolog, parse_prolog_list

def handle(tool: str):
    goal = f"findall(B, can_mine_with(B, {tool}), L), writeln(L)"
    out = run_prolog(goal)
    blocks = parse_prolog_list(out)
    if not blocks:
        print(f"Инструмент {tool} не может добыть ни один блок")
    else:
        print(f"Инструмент {tool} может добыть следующие блоки:")
        for b in blocks:
            print(f"- {b}")