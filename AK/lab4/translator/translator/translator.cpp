#include <fstream>

#include "ASTNode.h"
#include "treeGen.h"
#include "semanticAnalyzer.h"
#include "codeGenerator.h"
#include "binarizer.h"

int main(int argc, char* argv[]) {
    if (argc != 2)
        throw std::runtime_error("Usage: ./translator <filename>");
    
    std::ifstream file(argv[1]);
    std::stringstream buffer;
    buffer << file.rdbuf();
    std::string data = buffer.str();

    TreeGenerator treeGenerator;
    ASTNode* tree = treeGenerator.makeTree(data);

    SemanticAnalyzer semanticAnalyzer;
    semanticAnalyzer.analyze(tree);
    std::cout << "Semantic analyze success\n";

    CodeGenerator codeGenerator;
    std::string code = codeGenerator.generateCode(tree);
    std::cout << "Code generation success\n";

    Binarizer binarizer;
    binarizer.parse(code);
    binarizer.writeToFile("program.bin");
    
    std::cout << "Binarized code:\n";
    std::cout << binarizer.toAsm() << "\n";

    std::cout << "bindump\n";
    binarizer.dump();
    std::cout << "\n";

    std::cout << "Binary program saved to program.bin\n";
}