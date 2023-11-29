from parser.node import Node

class Parser():
    def __init__(self):
        self.rootNode = Node(0, "", "", [])

    def loadXML(self, filename):
        self.rootNode.parseXML(filename)
    
    def writeYAML(self, filename):
        self.rootNode.saveAsYAML(filename)