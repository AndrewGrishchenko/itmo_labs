#ifndef _SEMANTIC_ANALYZER_H
#define _SEMANTIC_ANALYZER_H

#include <iostream>
#include <unordered_map>
#include <string>

#include "ASTNode.h"

struct FunctionSignature {
    std::string returnType;
    std::vector<std::string> paramTypes;
};

class SemanticAnalyzer {
    public:
        SemanticAnalyzer();
        ~SemanticAnalyzer();

        void analyze(ASTNode* node);

    private:
        std::unordered_map<std::string, FunctionSignature> functions;
        std::vector<std::unordered_map<std::string, std::string>> scopes;

        void enterScope();
        void exitScope();

        void declareVariable(const std::string& type, const std::string& name);
        std::string lookupVariable(const std::string& name);

        std::string analyzeExpression(ASTNode* node);
        void analyzeStatement(ASTNode* node);
        void analyzeFunction(ASTNode* node);
        std::string currentReturnType;

        std::string nodeStr(ASTNode* node);
};

#endif