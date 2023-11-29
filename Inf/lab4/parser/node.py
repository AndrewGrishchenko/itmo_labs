class Node:
    def __init__(self, id, name, cargo, nodes=[]):
        self.id = id
        self.name = name
        self.cargo = cargo
        self.nodes = nodes
    
    def getID(self):
        return self.id

    def getName(self):
        return self.name

    def getCargo(self):
        return self.cargo

    def getNodes(self):
        return self.nodes
    
    def getNodeByID(self, ID):
        for i in self.getNodes():
            if i.getID() == ID:
                return i
        return 0
    
    def getNodeByPath(self, path):
        if len(path) == 0:
            return self
        elif len(path) == 1:
            return self.getNodeByID(path[0])
        else:
            return self.getNodeByID(path[0]).getNodeByPath(path[1:])
    
    def addNode(self, node):
        self.nodes.append(node)

    def hasChildByID(self, ID):
        for i in self.getNodes():
            if ID == i.getID():
                return True
        return False
    
    def isLeaf(self):
        if len(self.nodes) == 0:
            return True
        else:
            return False
        
    def printAllNodes(self):
        if self.isLeaf():
            print(f"Leaf node. NAME: {self.name} VALUE: {self.cargo}")
        else:
            print(f"NAME: {self.name} VALUE: {self.cargo}")
            for i in self.getNodes():
                i.printAllNodes()

    def countChildNodesByName(self, name):
        count = 0
        for i in self.nodes:
            if i.getName() == name:
                count += 1
        return count

    def isGrandParent(self):
        for i in self.nodes:
            if len(i.getNodes()) != 0:
                return True
        return False

    def parseXML(self, filename):
        file = open(filename, 'r')

        path = []
        ind = 1
        for line in file.read().split('\n'):
            line = line.strip()

            tag = ""
            value = ""
            openTag = True
            writeTag = False
            writeValue = False
            for i in line:
                if writeTag and i != '>':
                    tag += i
                if writeValue and i != '<':
                    value += i
                if openTag:
                    if i == '<':
                        writeTag = True
                    elif i == '>':
                        writeTag = False
                        writeValue = True
                        openTag = False
                else:
                    if i == '<' or i == '>':
                        break
            
            if '/' in tag:
                path.pop(-1)
            else:
                if '/'+tag not in line:
                    path.append(ind)
            
            if len(path) != 0:
                if value != "":
                    path.append(ind)

                    if self.getNodeByPath(path) == 0:
                        self.getNodeByPath(path[:-1]).addNode(Node(ind, tag, value, []))
                        ind += 1

                    path.pop(-1)
                else:
                    if self.getNodeByPath(path) == 0:
                        self.getNodeByPath(path[:-1]).addNode(Node(ind, tag, value, []))
                        ind += 1

    def saveAsYAML(self, filename):
        file = open(filename, 'w')
        file.writelines(self.YAMLdata(0, 0))

    def YAMLdata(self, depth, minusState=0):
        s = ""

        if self.name == "":
            if len(self.nodes) == 1:
                return self.nodes[0].YAMLdata(depth, 0)
            else:
                for i in self.nodes:
                    s += i.YAMLdata(depth, 0)
                return s
            
        if len(self.nodes) > 1:
            if minusState == 0:
                s += f"{'  ' * depth}{self.name}:\n"

            moreChildNodes = set()
            for i in range(len(self.nodes)):
                if self.countChildNodesByName(self.nodes[i].getName()) > 1:
                    if self.nodes[i].getName() not in moreChildNodes:
                        if minusState == 1:
                            s += f"{'  ' * depth}- {self.nodes[i].getName()}:\n"
                            minusState = 0
                        else:
                            s += f"{'  ' * (depth+1)}{self.nodes[i].getName()}:\n"
                        moreChildNodes.add(self.nodes[i].getName())
                    s += self.nodes[i].YAMLdata(depth+2, 1)
                else:
                    if minusState == 1:
                        s += self.nodes[i].YAMLdata(depth+1, 1)
                        minusState = 0
                    else:
                        s += self.nodes[i].YAMLdata(depth+1, 0)
        elif len(self.nodes) == 1:
            s += self.nodes[0].YAMLdata(depth+1, minusState)
        else:
            if minusState == 1:
                s += f"{'  ' * (depth - 1)}- {self.name}: {self.cargo}\n"
            else:
                s += f"{'  ' * depth}{self.name}: {self.cargo}\n"

        return s