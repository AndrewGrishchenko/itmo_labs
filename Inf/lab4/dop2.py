import re

sourceFile = open('Inf/lab4/lessons.xml', 'r')

depth = 0
reData = []

for line in sourceFile.readlines():
    line = line.strip()
    
    diff = line.count('<') - 2 * line.count('</')

    if diff == -1:
        depth -= 1
    elif diff == 0:
        reData.append(re.sub('<', '  ' * depth, re.sub('>', ': ', re.sub('<\/\w*>', '', line))))
    else:
        reData.append(re.sub('<', '  ' * depth, re.sub('>', ':', line)))
        depth += 1

outputFile = open('Inf/lab4/lessons.yaml', 'w')
outputFile.write('\n'.join(reData))