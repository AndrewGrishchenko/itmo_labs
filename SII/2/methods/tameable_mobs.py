from .common import run_prolog, parse_pair_list

def handle():
    goal = "findall(M-Item, tameable(M, Item), L), writeln(L)"
    out = run_prolog(goal)
    pairs = parse_pair_list(out)
    if not pairs:
        print("Приручаемых мобов нет")
    else:
        print("Приручаемые мобы:")
        for mob, item in pairs:
            print(f"- {mob} (предмет: {item})")