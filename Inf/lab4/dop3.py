from parser.parser import *

parser = Parser()
parser.loadXML('Inf/lab4/test1.xml')
parser.writeYAML('Inf/lab4/lessons.yaml')