import os

prefix = "."
line_count = 0

f = []
for (dirpath, dirnames, filenames) in os.walk(prefix):
    for file in filenames:
        if ".java" in file:
            with open(dirpath + "/" + file) as f:
                for count, line in enumerate(f):
                    pass
            line_count += count + 1
print(line_count)
