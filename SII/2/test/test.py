import subprocess

MAIN = "../main.py"
QUERIES = "queries"

with open(QUERIES, "r", encoding="utf-8") as f:
    queries = [q.strip() for q in f.readlines() if q.strip()]

for query in queries:
    print(f"query: {query}\n")

    result = subprocess.run(
        ["python3", MAIN],
        input=query + "\n",
        text=True,
        capture_output=True
    )

    if result.stdout:
        print(result.stdout.strip())
    if result.returncode == 1:
        if result.stderr:
            print("\n[stderr]")
            print(result.stderr.strip())
        
        print("rc 1. stop")
        exit(1)

    print()