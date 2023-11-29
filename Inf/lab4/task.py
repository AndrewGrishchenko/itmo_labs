sourceFile = open('Inf/lab4/lessons.xml', 'r')
lines = sourceFile.readlines()

YAMLdata = ''
depth = 0

for line in lines:
    line = line.strip().replace('<', '>').split('>')[1:-1]
    
    if len(line) > 2:
        line = line[:-1]
    
    if len(line) == 1:
        if line[0][0] == '/':
            depth -= 1
        else:
            YAMLdata += f"{'  ' * depth}{line[0]}:\n"
            depth += 1
    else:
        if len(line) == 0:
            line = ""
        else:
            YAMLdata += f"{'  ' * depth}{line[0]}: {line[1]}\n"

fileOutput = open('Inf/lab4/lessons.yaml', 'w')
fileOutput.write(YAMLdata)