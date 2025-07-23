#include <fstream>
#include <optional>

#include "ASTNode.hpp"
#include "treeGen.h"
#include "semanticAnalyzer.h"
#include "codeGenerator.h"
#include "binarizer.h"
#include "treeVisualizer.hpp"

struct Args {
    bool isHighLevel = true;
    std::optional<std::string> vizFile;
    std::string inputFile;
    std::string outputFile;
};

Args parseArgs(int argc, char* argv[]) {
    if (argc < 3)
        throw std::runtime_error("Usage: ./translator [--asm|--hl] [--viz file] <input> <output>");

    Args args;
    int i = 1;

    while (i < argc - 2) {
        std::string flag = argv[i];

        if (flag == "--asm") {
            args.isHighLevel = false;
            ++i;
        }
        else if (flag == "--hl") {
            args.isHighLevel = true;
            ++i;
        }
        else if (flag == "--viz") {
            if (i + 1 >= argc - 2)
                throw std::runtime_error("--viz requires a filename");
            args.vizFile = argv[i + 1];
            i += 2;
        }
        else {
            throw std::runtime_error("Unknown flag: " + flag);
        }
    }

    args.inputFile = argv[argc - 2];
    args.outputFile = argv[argc - 1];

    return args;
}

int main(int argc, char* argv[]) {
    try {
        Args args = parseArgs(argc, argv);
        std::string code;

        if (args.isHighLevel) {
            std::ifstream file(args.inputFile);
            std::stringstream buffer;
            buffer << file.rdbuf();
            std::string data = buffer.str();

            TreeGenerator treeGenerator;
            ASTNode* tree = treeGenerator.makeTree(data);

            SemanticAnalyzer semanticAnalyzer;
            semanticAnalyzer.analyze(tree);
            std::cout << "Semantic analyze success\n";

            if (args.vizFile) {
                TreeVisualizer tv;
                std::string uml = tv.makeUML(tree);
                std::ofstream uml_file(*args.vizFile);
                uml_file << uml;
                std::cout << "PlantUML visualize saved to " << *args.vizFile << std::endl;
            }

            CodeGenerator codeGenerator;
            code = codeGenerator.generateCode(tree);
            std::cout << "CODE:\n";
            std::cout << code << std::endl;
        } else {
            std::ifstream asm_file(args.inputFile);
            std::stringstream buffer;
            buffer << asm_file.rdbuf();
            code = buffer.str();
        }

        Binarizer binarizer;
        binarizer.parse(code);
        binarizer.writeToFile(args.outputFile);

        std::cout << "Binary program saved to " << args.outputFile << std::endl;

        std::cout << "DUMP:\n";
        binarizer.dump();
    } catch (const std::exception& ex) {
        std::cerr << ex.what() << std::endl;
    }
}