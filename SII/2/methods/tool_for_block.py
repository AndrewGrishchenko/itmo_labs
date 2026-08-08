from .common import run_prolog

def handle(block: str):
    goal = f"can_mine({block}, T), writeln(T)"
    out = run_prolog(goal)
    if not out:
        print(f"Не удалось определить инструмент для блока {block}")
    else:
        print(f"Для добычи блока {block} нужен инструмент: {out}")