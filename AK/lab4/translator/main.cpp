#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <fstream>
#include <string>

#include "translator.h"
#include "codeGenerator.h"

#include "treeVisualizer.h"

int main() {
    std::ifstream file("input.txt");
    std::stringstream buffer;
    buffer << file.rdbuf();
    std::string data = buffer.str();

    Translator translator;
    ASTNode* tree = translator.makeTree(data);

    TreeVisualizer tv;
    std::string puml_str = tv.makeUML(tree); 
    std::ofstream file_o("output.puml");
    file_o << puml_str;

    CodeGenerator cg;
    std::string code = cg.generateCode(tree);

    std::cout << "CODE:\n";
    std::cout << code;
}