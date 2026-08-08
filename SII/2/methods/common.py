import subprocess
from pathlib import Path

PROLOG_FILE = Path(__file__).resolve().parent.parent / "prolog" / "minecraft.pl"

def run_prolog(goal: str) -> str:
    args = [
        "swipl",
        "-q",
        "-s", str(PROLOG_FILE),
        "-g", goal,
        "-t", "halt"
    ]
    result = subprocess.run(
        args,
        text=True,
        capture_output=True
    )

    if result.returncode != 0:
        raise RuntimeError(f"prolog err: {result.stderr.strip()}")

    return result.stdout.strip()

def parse_prolog_list(s: str) -> list[str]:
    s = s.strip()
    if not s or s == "[]":
        return []
    if s[0] == "[" and s[-1] == "]":
        s = s[1:-1]
    if not s:
        return []
    items = [item.strip() for item in s.split(",") if item.strip()]
    return items

def parse_pair_list(s: str) -> list[tuple[str, str]]:
    raw = parse_prolog_list(s)
    pairs: list[tuple[str, str]] = []
    for item in raw:
        if "-" in item:
            left, right = item.split("-", 1)
            pairs.append((left.strip(), right.strip()))
        else:
            pairs.append((item.strip(), ""))
    return pairs