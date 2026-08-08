import csv
import os
import matplotlib.pyplot as plt

directory = "./plots"

for filename in os.listdir(directory):
    if not filename.endswith(".csv"):
        continue

    path = os.path.join(directory, filename)

    x_values = []
    y_values = []

    with open(path, newline="") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            x_values.append(float(row["x"]))
            y_values.append(float(row["y"]))

    fig, ax = plt.subplots()

    ax.plot(x_values, y_values)

    ax.set_title(os.path.splitext(filename)[0])
    ax.set_xlabel("x")
    ax.set_ylabel("y")
    ax.grid(True)

    ax.set_xlim(min(x_values), max(x_values))

plt.show()
