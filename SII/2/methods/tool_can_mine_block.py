from .common import run_prolog

def handle(tool: str, block: str):
    goal = (
        f"(can_mine_with({block}, {tool}) -> writeln(true) ; writeln(false))"
    )
    out = run_prolog(goal).strip().lower()

    if out == "true":
        print(f"Да, инструмент {tool} может добыть блок {block}")
    elif out == "false":
        print(f"Нет, инструмент {tool} не может добыть блок {block}")
    else:
        print(f"Не удалось однозначно определить, может ли {tool} добыть {block}. Ответ Prolog: {out}")