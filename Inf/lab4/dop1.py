import xmltodict
import yaml

sourceFile = open('Inf/lab4/lessons.xml', 'r')
lines = sourceFile.read()

xmlDict = xmltodict.parse(lines)

outputFile = open('Inf/lab4/lessons.yaml', 'w')
yaml.dump(xmlDict, outputFile, default_flow_style=False, allow_unicode=True)