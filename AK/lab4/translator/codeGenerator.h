#ifndef _CODE_GENERATOR_H
#define _CODE_GENERATOR_H

#include "ASTNode.h"

#include <unordered_map>
#include <vector>
#include <sstream>
#include <algorithm>

class CodeGenerator {
    public:
        CodeGenerator() {}
        ~CodeGenerator() {}

        std::string generateCode(ASTNode* root) {
            if (!root || root->nodeType != ASTNodeType::Block)
                throw std::runtime_error("Root node must be block");

            dataSection.clear();
            codeSection.clear();
            funcSection.clear();
            variables.clear();
            functionLabels.clear();
            functionParams.clear();

            labelCounter = 0;
            tempVarCounter = 0;
            stackOffset = 0;

            processRoot(root);

            return assembleCode();
        }

    private:
        std::vector<std::string> dataSection;
        std::vector<std::string> codeSection;
        std::vector<std::string> funcSection;
        std::unordered_map<std::string, std::string> variables;
        std::unordered_map<std::string, int> functionLabels;
        std::unordered_map<std::string, std::vector<std::pair<std::string, std::string>>> functionParams;

        int labelCounter = 0;
        int tempVarCounter = 0;
        int stackOffset = 0;

        std::string currentFunction = "";

        std::string getNewLabel() {
            return "L" + std::to_string(labelCounter++);
        }

        std::string getNewTempVar() {
            return "temp" + std::to_string(tempVarCounter++);
        }

        std::string nodeStr(ASTNode* node) {
            switch (node->nodeType) {
                case ASTNodeType::VarDecl:
                    return "VarDecl";
                case ASTNodeType::NumberLiteral:
                    return "NumberLiteral";
                case ASTNodeType::StringLiteral:
                    return "StringLiteral";
                case ASTNodeType::BooleanLiteral:
                    return "BooelanLiteral";
                case ASTNodeType::Identifier:
                    return "Identifier";
                case ASTNodeType::Assignment:
                    return "Assignment";
                case ASTNodeType::BinaryOp:
                    return "BinaryOp";
                case ASTNodeType::UnaryOp:
                    return "UnaryOp";
                case ASTNodeType::If:
                    return "If";
                case ASTNodeType::While:
                    return "While";
                case ASTNodeType::Block:
                    return "Block";
                case ASTNodeType::Parameter:
                    return "Parameter";
                case ASTNodeType::Function:
                    return "Function";
                case ASTNodeType::CallParameter:
                    return "CallParameter";
                case ASTNodeType::FunctionCall:
                    return "FunctionCall";
                case ASTNodeType::Expression:
                    return "Expression";
                case ASTNodeType::Return:
                    return "Return";
                default:
                    return "Unknown";
            }
        }

        void processNode(ASTNode* node) {
            if (!node) return;

            switch(node->nodeType) {
                case ASTNodeType::Function:
                    processFunction(node);
                    break;
                case ASTNodeType::VarDecl:
                    processVarDecl(node);
                    break;
                case ASTNodeType::Assignment:
                    processAssignment(node);
                    break;
                case ASTNodeType::BinaryOp:
                    processBinaryOp(node);
                    break;
                case ASTNodeType::UnaryOp:
                    processUnaryOp(node);
                    break;
                case ASTNodeType::If:
                    processIf(node);
                    break;
                case ASTNodeType::While:
                    processWhile(node);
                    break;
                case ASTNodeType::Block:
                    processBlock(node);
                    break;
                case ASTNodeType::FunctionCall:
                    processFunctionCall(node);
                    break;
                case ASTNodeType::Return:
                    processReturn(node);
                    break;
                case ASTNodeType::Identifier:
                    processIdentifier(node);
                    break;
                case ASTNodeType::NumberLiteral:
                    processNumberLiteral(node);
                    break;
                case ASTNodeType::BooleanLiteral:
                    processBooleanLiteral(node);
                    break;
                case ASTNodeType::StringLiteral:
                    processStringLiteral(node);
                    break;
                case ASTNodeType::VoidLiteral:
                    processVoidLiteral(node);
                    break;
                case ASTNodeType::Expression:
                    processExpression(node);
                    break;
                default:
                    throw std::runtime_error("Unknown node");
            }
        }

        void emitCode(const std::string& line, int indent = 0) {
            if (currentFunction.empty())
                codeSection.push_back("  " + line);
            else
                funcSection.push_back("  " + line);
        }

        void emitCodeLabel(const std::string& label) {
            if (currentFunction.empty())
                codeSection.push_back(label + ":");
            else
                funcSection.push_back(label + ":");
        }

        void emitData(const std::string& line) {
            dataSection.push_back("  " + line);
        }

        void processRoot(ASTNode* node) {
            BlockNode* root = static_cast<BlockNode*>(node);

            emitCodeLabel("_start");

            emitCode("ldi 1000");
            emitCode("st sp");
            emitCode("");

            for (auto* child : root->children) {
                processNode(child);
            }

            emitCode("halt");
        }

        void processFunction(ASTNode* node) {
            FunctionNode* functionNode = static_cast<FunctionNode*>(node);
            currentFunction = functionNode->name;

            std::string funcLabel = "func_" + currentFunction;
            functionLabels[currentFunction] = labelCounter++;
            emitCodeLabel(funcLabel);

            for (auto param : functionNode->parameters) {
                ParameterNode* paramNode = static_cast<ParameterNode*>(param);
                functionParams[currentFunction].push_back({paramNode->type, paramNode->name});
                emitData("arg_" + currentFunction + "_" + paramNode->name + ": 0");
            }
            
            // emitCode("ld sp");
            // emitCode("st temp_sp");
            //TODO: have i to save sp?
            //TODO: redo parameters as field ParametersNode, containing vector of nodes
            //TODO: think making function arguments as stack (+using sp)

            processNode(functionNode->body);

            currentFunction = "";
        }

        void processVarDecl(ASTNode* node) {
            VarDeclNode* varDeclNode = static_cast<VarDeclNode*>(node);
            
            std::string varName = varDeclNode->name;
            std::string varLabel = "var_";

            varLabel = getVarLabel(varName);

            emitData(varLabel + ": 0");
            variables[varLabel] = varDeclNode->type;

            processNode(varDeclNode->value);
            emitCode("st " + varLabel);
        }

        void processAssignment(ASTNode* node) {
            AssignNode* assignNode = static_cast<AssignNode*>(node);
            IdentifierNode* var1Node = static_cast<IdentifierNode*>(assignNode->var1);

            std::string varName = var1Node->name;
            std::string varLabel = getVarLabel(varName);

            processNode(assignNode->var2);

            if (variables.find(varLabel) != variables.end())
                emitCode("st " + varLabel);
            else
                throw std::runtime_error("Could not find variable " + varName);
        }

        void processBinaryOp(ASTNode* node) {
            BinaryOpNode* binaryOpNode = static_cast<BinaryOpNode*>(node);

            std::string op = binaryOpNode->op;
            ASTNode* left = binaryOpNode->left;
            ASTNode* right = binaryOpNode->right;

            processNode(left);

            pushToStack();

            processNode(right);

            emitCode("st temp_right");

            popFromStack();

            if (op == "+")
                emitCode("add temp_right");
            else if (op == "*")
                emitCode("mul temp_right");
            else if (op == "/")
                emitCode("div temp_right");
            else if (op == "%")
                emitCode("rem temp_right");
            else if (op == "-" || op == ">" || op == ">=" || op == "<" ||
                       op == "<=" || op == "==" || op == "!=")
                emitCode("sub temp_right");
        }

        void pushToStack() {
            emitCode("push");
        }

        void popFromStack() {
            emitCode("pop");
        }

        void processUnaryOp(ASTNode* node) {
            UnaryOpNode* unaryOpNode = static_cast<UnaryOpNode*>(node);

            std::string op = unaryOpNode->op;
            
            processNode(unaryOpNode->operand);

            if (op == "!") {
                emitCode("st temp_right");
                emitCode("ldi 1");
                emitCode("sub temp_right");
            } else if (op == "-") {
                emitCode("not");
            }
        }

        void processIf(ASTNode* node) {
            IfNode* ifNode = static_cast<IfNode*>(node);

            ASTNode* condition = ifNode->condition;
            ASTNode* thenBlock = ifNode->thenBranch;
            ASTNode* elseBlock = ifNode->elseBranch;

            std::string elseLabel;
            if (elseBlock) elseLabel = getNewLabel();
            std::string endLabel = getNewLabel();

            processNode(condition);
            std::string notCondJmp = getNotConditionJump(condition);

            if (elseBlock) emitCode(notCondJmp + " " + elseLabel);
            else emitCode(notCondJmp + " " + endLabel);

            processNode(thenBlock);
            emitCode("jmp " + endLabel);

            if (elseBlock) {
                emitCodeLabel(elseLabel);
                processNode(elseBlock);
            }

            emitCodeLabel(endLabel);
        }

        std::string getNotConditionJump(ASTNode* node) {
            if (node->nodeType == ASTNodeType::Expression) {
                ExpressionNode* expressionNode = static_cast<ExpressionNode*>(node);
                node = expressionNode->expression;
            }
            
            if (node->nodeType == ASTNodeType::BinaryOp) {
                BinaryOpNode* binaryOpNode = static_cast<BinaryOpNode*>(node);
                std::string op = binaryOpNode->op;

                if (op == ">") {
                    return "jle";
                } else if (op == ">=") {
                    return "jl";
                } else if (op == "<") {
                    return "jge";
                } else if (op == "<=") {
                    return "jg";
                } else if (op == "==") {
                    return "jnz";
                } else if (op == "!=") {
                    return "jz";
                } else {
                    throw std::runtime_error("Condition must be logical binary op");
                }
            } else if (node->nodeType == ASTNodeType::UnaryOp) {
                UnaryOpNode* unaryOpNode = static_cast<UnaryOpNode*>(node);
                std::string op = unaryOpNode->op;

                if (op == "!") {
                    return "jz"; //jz MEANING HERE: i.e. z = 0
                } else {
                    throw std::runtime_error("Unary condition must be logical op");
                }
            } else if (node->nodeType == ASTNodeType::Identifier) {
                return "jnz";
                //TODO: codeGen: make commands as enum
            } else
                throw std::runtime_error("Condition must be binary op");
        }

        void processWhile(ASTNode* node) {
            WhileNode* whileNode = static_cast<WhileNode*>(node);

            ASTNode* condition = whileNode->condition;
            ASTNode* body = whileNode->body;

            std::string startLabel = getNewLabel();
            std::string endLabel = getNewLabel();

            emitCodeLabel(startLabel);

            processNode(condition);
            std::string notCondJmp = getNotConditionJump(condition);

            emitCode(notCondJmp + " " + endLabel);

            processNode(body);

            emitCode("jmp " + startLabel);
            emitCodeLabel(endLabel);
        }

        void processBlock(ASTNode* node) {
            BlockNode* blockNode = static_cast<BlockNode*>(node);

            for (auto child : blockNode->children) {
                processNode(child);
            }
        }

        void processFunctionCall(ASTNode* node) {
            FunctionCallNode* functionCallNode = static_cast<FunctionCallNode*>(node);

            std::string funcName = functionCallNode->name;
            
            for (size_t i = 0; i < functionCallNode->parameters.size(); i++) {
                processNode(functionCallNode->parameters[i]);
                emitCode("st arg_" + funcName + "_" + functionParams[funcName][i].second);
            }

            if (functionLabels.find(funcName) != functionLabels.end())
                emitCode("call func_" + funcName);
            else
                throw std::runtime_error("Could not find function " + funcName);
        }

        void processReturn(ASTNode* node) {
            ReturnNode* returnNode = static_cast<ReturnNode*>(node);

            ASTNode* returnValue = returnNode->returnValue;
            if (returnValue)
                processNode(returnValue);

            emitCode("ret");
        }

        std::string getVarLabel(const std::string& varName) {
            if (!currentFunction.empty()) {
                auto& vecPair = functionParams[currentFunction];
                auto it = std::find_if(vecPair.begin(), vecPair.end(),
                [&varName](const auto& elem){ return elem.second == varName; });
                
                if (it != vecPair.end()) {
                    return "arg_" + currentFunction + "_" + varName;
                } else {
                    if (variables.find("var_" + varName) != variables.end()) {
                        return "var_" + varName;
                    } else {
                        return "var_" + currentFunction + "_" + varName;
                    }
                }
            } else {
                return "var_" + varName;
            }
        }

        void processIdentifier(ASTNode* node) {
            IdentifierNode* identifierNode = static_cast<IdentifierNode*>(node);

            std::string varName = identifierNode->name;
            emitCode("ld " + getVarLabel(varName));
            //TODO: double check arg_ var_ labels everywhere im so cooked rn
        }

        void processNumberLiteral(ASTNode* node) {
            NumberLiteralNode* numberLiteralNode = static_cast<NumberLiteralNode*>(node);

            int value = numberLiteralNode->number;
            emitCode("ldi " + std::to_string(value));
        }

        void processBooleanLiteral(ASTNode* node) {
            BooleanLiteralNode* booleanLiteralNode = static_cast<BooleanLiteralNode*>(node);

            bool value = booleanLiteralNode->value;
            emitCode("ldi " + std::to_string(value ? 1 : 0));
        }

        void processStringLiteral(ASTNode* node) {
            StringLiteralNode* stringLiteralNode = static_cast<StringLiteralNode*>(node);

            std::string value = stringLiteralNode->value;
            std::string strLabel = "str_" + std::to_string(labelCounter++);

            emitData(strLabel + ": \"" + value + "\\0\"");
            emitCode("ldi " + strLabel);
        }

        void processVoidLiteral(ASTNode* node) {
            return;
        }

        std::string assembleCode() {
            std::stringstream result;

            result << ".data\n";
            result << "  sp: 0\n";
            result << "  temp_sp: 0\n";
            result << "  temp_addr: 0\n";
            result << "  temp_right: 0\n";

            for (const auto& line : dataSection) {
                result << line << "\n";
            }

            result << "\n.text\n";

            for (const auto& line : funcSection) {
                result << line << "\n";
            }

            for (const auto& line : codeSection) {
                result << line << "\n";
            }

            return result.str();
        }

        void processExpression(ASTNode* node) {
            ExpressionNode* expressionNode = static_cast<ExpressionNode*>(node);
            processNode(expressionNode->expression);
        }
};

#endif