#ifndef _SEMANTIC_ANALYZER_H
#define _SEMANTIC_ANALYZER_H

#include <iostream>
#include <unordered_map>
#include <string>
#include <algorithm>

#include "ASTNode.hpp"
#include "ASTVisitor.hpp"

class SemanticAnalyzer : ASTVisitor {
    public:
        SemanticAnalyzer();
        ~SemanticAnalyzer();

        // struct VariableData {
        //     std::string type;
        //     size_t size;

        //     VariableData(std::string type, size_t size)
        //         : type(type), size(size) { }
        //     VariableData(std::string type)
        //         : type(type), size(0) { }
        //     VariableData()
        //         : type(""), size(0) { }
        // };

        void analyze(ASTNode* node);

        void visit(VarDeclNode& node) override;
        void visit(NumberLiteralNode& node) override;
        void visit(CharLiteralNode& node) override;
        void visit(StringLiteralNode& node) override;
        void visit(BooleanLiteralNode& node) override;
        void visit(VoidLiteralNode& node) override;
        void visit(IntArrayLiteralNode& node) override;
        void visit(ArrayGetNode& node) override;
        void visit(MethodCallNode& node) override;
        void visit(IdentifierNode& node) override;
        void visit(AssignNode& node) override;
        void visit(BinaryOpNode& node) override;
        void visit(UnaryOpNode& node) override;
        void visit(IfNode& node) override;
        void visit(WhileNode& node) override;
        void visit(BreakNode& node) override;
        void visit(BlockNode& node) override;
        void visit(ParameterNode& node) override;
        void visit(FunctionNode& node) override;
        void visit(FunctionCallNode& node) override;
        void visit(ReturnNode& node) override;
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
        std::vector<std::unordered_map<std::string, std::string>> scopes;
        
        const std::unordered_map<std::string, std::vector<FunctionSignature>> reservedFunctions = {
            {"in", {
                {"int", {"int"}},
                {"int", {}},
                {"char", {}},
                {"string", {"int"}},
                {"string", {}},
                {"int[]", {"int"}},
                {"int[]", {}}
            }},
            {"out", {
                {"void", {"int"}},
                {"void", {"char"}},
                {"void", {"int[]"}},
                {"void", {"string"}}
            }}
        };

        const std::unordered_map<std::string, std::unordered_map<std::string, FunctionSignature>> typeMethods = {
            {"int[]", {
                {"size", {"int", {}}}
            }},

            {"string", {
                {"size", {"int", {}}}
            }}
        };

        bool isReserved(const std::string& name, FunctionSignature sig);
        std::string findFunction(const std::string& name, std::vector<std::string> paramTypes, std::string& expected);

        void enterScope();
        void exitScope();

        void declareVariable(const std::string& name, std::string data);
        std::string lookupVariable(const std::string& name);

        int loopDepth = 0;
        bool hasReturn;
        std::string currentReturnType;

        std::string nodeStr(ASTNode* node);

        ExpressionNode* lastVisitedExpression = nullptr;
        // VariableData lastExpressionResult;
        std::string expectedType;
};

#endif