#ifndef _SEMANTIC_ANALYZER_H
#define _SEMANTIC_ANALYZER_H

#include <iostream>
#include <unordered_map>
#include <string>
#include <algorithm>

#include "ASTNode.h"

class SemanticAnalyzer {
    public:
        SemanticAnalyzer();
        ~SemanticAnalyzer();

        struct VariableData {
            std::string type;
            size_t size;

            VariableData(std::string type, size_t size)
                : type(type), size(size) { }
            VariableData(std::string type)
                : type(type), size(0) { }
            VariableData()
                : type(""), size(0) { }
        };

        void analyze(ASTNode* node);
        VariableData analyzeExpression(ASTNode* node);
    private:
        struct FunctionSignature {
            std::string returnType;
            std::vector<std::string> paramTypes;

            bool operator==(const FunctionSignature& other) const {
                return returnType == other.returnType && 
                    paramTypes == other.paramTypes;
            }
        };

        std::unordered_map<std::string, std::vector<FunctionSignature>> functions;
        std::vector<std::unordered_map<std::string, VariableData>> scopes;
        
        const std::unordered_map<std::string, std::vector<FunctionSignature>> reservedFunctions = {
            {"in", {
                {"string", {}}
            }},
            {"out", {
                {"void", {"int"}},
                {"void", {"string"}}
            }}
        };

        bool isReserved(const std::string& name, FunctionSignature sig);
        std::string findFunction(const std::string& name, std::vector<std::string> paramTypes);

        void enterScope();
        void exitScope();

        void declareVariable(const std::string& name, VariableData data);
        std::string lookupVariable(const std::string& name);

        void analyzeStatement(ASTNode* node);
        void analyzeFunction(ASTNode* node);

        bool hasReturn;
        std::string currentReturnType;

        std::string nodeStr(ASTNode* node);
};

#endif