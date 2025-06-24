#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <fstream>
#include <string>

#include "translator.h"
#include "codeGenerator.h"
#include "semanticAnalyzer.h"
#include "binarizer.h"
#include "processorModel.h"

#include "treeVisualizer.h"

int main() {
    // std::ifstream file("input.txt");
    // std::stringstream buffer;
    // buffer << file.rdbuf();
    // std::string data = buffer.str();

    // Translator translator;
    // ASTNode* tree = translator.makeTree(data);

    // SemanticAnalyzer analyzer;
    // analyzer.analyze(tree);
    // std::cout << "semantic analyze success\n\n";

    // TreeVisualizer tv;
    // std::string puml_str = tv.makeUML(tree); 
    // std::ofstream file_o("output.puml");
    // file_o << puml_str;

    // CodeGenerator cg;
    // std::string code = cg.generateCode(tree);

    std::ifstream file("program.asm");
    std::stringstream buffer;
    buffer << file.rdbuf();
    std::string code = buffer.str();

    std::cout << "CODE:\n";
    std::cout << code;

    Binarizer binarizer;
    binarizer.parse(code);
    binarizer.writeToFile("program.bin");

    std::cout << "RECONSTRUCTED CODE:\n";
    std::cout << binarizer.toAsm();
    binarizer.dump();

    ProcessorModel processorModel;
    processorModel.loadBinary("program.bin");
    processorModel.process();
}