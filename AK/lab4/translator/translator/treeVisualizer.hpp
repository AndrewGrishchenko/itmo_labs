#pragma once

#include "ASTNode.hpp"

#include <iostream>
#include <unordered_map>
#include <vector>

class TreeVisualizer {
    public:
        TreeVisualizer() { }
        ~TreeVisualizer() { }

        std::string makeUML(ASTNode* node) {
            objects.clear();
            objectValues.clear();
            objectAssociations.clear();
            nodeLabels.clear();

            std::string result = "";
            processNode(node);

            result += "@startuml ast\n\n";
            for (auto o : objects) {
                result += o + "\n";
            }
            result += "\n";

            for (auto o : objectValues) {
                result += o + "\n";
            }
            result += "\n";

            for (auto o : objectAssociations) {
                result += o + "\n";
            }
            result += "\n";
            result += "@enduml";

            return result;
        }

    private:
        std::vector<std::string> objects;
        std::vector<std::string> objectValues;
        std::vector<std::string> objectAssociations;
        std::unordered_map<ASTNodeType, int> nodeLabels;

        int labelCount(ASTNodeType type) {
            if (nodeLabels.find(type) == nodeLabels.end())
                nodeLabels[type] = 0;
            else
                nodeLabels[type] += 1;
            return nodeLabels[type];
        }

        void emitObject(const std::string& objName) {
            objects.push_back("object " + objName);
        }

        void emitObjectData(const std::string& objName, const std::string& dataName, const std::string& dataValue) {
            objectValues.push_back(objName + " : " + dataName + " = " + dataValue);
        }

        void emitObjectAssociation(const std::string& parentName, const std::string childName, const std::string& label = "") {
            if (label.empty())
                objectAssociations.push_back(parentName + " --> " + childName);
            else
                objectAssociations.push_back(parentName + " --> " + childName + " : " + label);
        }

        std::string processNode(ASTNode* node) {
            switch(node->nodeType) {
                case ASTNodeType::VarDecl: {
                    VarDeclNode* varDeclNode = static_cast<VarDeclNode*>(node);
                    std::string objName = "VarDecl" + std::to_string(labelCount(node->nodeType));
                    
                    emitObject(objName);
                    emitObjectData(objName, "name", varDeclNode->name);
                    emitObjectData(objName, "type", varDeclNode->type);

                    std::string childName = processNode(varDeclNode->value);
                    emitObjectAssociation(objName, childName, "value");

                    return objName;
                }
                case ASTNodeType::NumberLiteral: {
                    NumberLiteralNode* numberLiteralNode = static_cast<NumberLiteralNode*>(node);
                    std::string objName = "NumberLiteral" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", numberLiteralNode->resolvedType);
                    emitObjectData(objName, "value", std::to_string(numberLiteralNode->number));

                    return objName;
                }
                case ASTNodeType::StringLiteral: {
                    StringLiteralNode* stringLiteralNode = static_cast<StringLiteralNode*>(node);
                    std::string objName = "StringLiteral" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", stringLiteralNode->resolvedType);
                    emitObjectData(objName, "value", stringLiteralNode->value);

                    return objName;
                }
                case ASTNodeType::BooleanLiteral: {
                    BooleanLiteralNode* booleanLiteralNode = static_cast<BooleanLiteralNode*>(node);
                    std::string objName = "BooleanLiteral" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", booleanLiteralNode->resolvedType);
                    emitObjectData(objName, "value", booleanLiteralNode->value ? "true" : "false");

                    return objName;
                }
                case ASTNodeType::VoidLiteral: {
                    VoidLiteralNode* voidLiteralNode = static_cast<VoidLiteralNode*>(node);
                    std::string objName = "VoidLiteral" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", voidLiteralNode->resolvedType);

                    return objName;
                }
                case ASTNodeType::IntArrayLiteral: {
                    IntArrayLiteralNode* intArrayLiteralNode = static_cast<IntArrayLiteralNode*>(node);
                    std::string objName = "IntArrayLiteral" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", intArrayLiteralNode->resolvedType);

                    for (auto* value : intArrayLiteralNode->values) {
                        std::string valueName = processNode(value);
                        emitObjectAssociation(objName, valueName, "value");
                    }

                    return objName;
                }
                case ASTNodeType::ArrayGet: {
                    ArrayGetNode* arrayGetNode = static_cast<ArrayGetNode*>(node);
                    std::string objName = "ArrayGet" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", arrayGetNode->resolvedType);

                    std::string objectName = processNode(arrayGetNode->object);
                    emitObjectAssociation(objName, objectName, "object");

                    std::string indexName = processNode(arrayGetNode->index);
                    emitObjectAssociation(objName, indexName, "index");
                    
                    return objName;
                }
                case ASTNodeType::MethodCall: {
                    MethodCallNode* methodCallNode = static_cast<MethodCallNode*>(node);
                    std::string objName = "MethodCall" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", methodCallNode->resolvedType);
                    
                    std::string objectName = processNode(methodCallNode->object);
                    emitObjectAssociation(objName, objectName, "object");

                    emitObjectData(objName, "methodName", methodCallNode->methodName);

                    for (auto& arg : methodCallNode->arguments) {
                        std::string argName = processNode(arg);
                        emitObjectAssociation(objName, argName, "arg");
                    }

                    return objName;
                }
                case ASTNodeType::Identifier: {
                    IdentifierNode* identifierNode = static_cast<IdentifierNode*>(node);
                    std::string objName = "Identifier" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", identifierNode->resolvedType);
                    emitObjectData(objName, "name", identifierNode->name);

                    return objName;
                }
                case ASTNodeType::Assignment: {
                    AssignNode* assignNode = static_cast<AssignNode*>(node);
                    std::string objName = "Assign" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    
                    std::string var1Name = processNode(assignNode->var1);
                    std::string var2Name = processNode(assignNode->var2);

                    emitObjectAssociation(objName, var1Name, "var1");
                    emitObjectAssociation(objName, var2Name, "var2");

                    return objName;
                }
                case ASTNodeType::BinaryOp: {
                    BinaryOpNode* binaryOpNode = static_cast<BinaryOpNode*>(node);
                    std::string objName = "BinaryOp" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", binaryOpNode->resolvedType);
                    emitObjectData(objName, "op", binaryOpNode->op);

                    std::string leftName = processNode(binaryOpNode->left);
                    std::string rightName = processNode(binaryOpNode->right);

                    emitObjectAssociation(objName, leftName, "left");
                    emitObjectAssociation(objName, rightName, "right");

                    return objName;
                }
                case ASTNodeType::UnaryOp: {
                    UnaryOpNode* unaryOpNode = static_cast<UnaryOpNode*>(node);
                    std::string objName = "UnaryOp" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", unaryOpNode->resolvedType);
                    emitObjectData(objName, "op", unaryOpNode->op);

                    std::string operandName = processNode(unaryOpNode->operand);

                    emitObjectAssociation(objName, operandName, "operand");

                    return objName;
                }
                case ASTNodeType::If: {
                    IfNode* ifNode = static_cast<IfNode*>(node);
                    std::string objName = "If" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    
                    std::string conditionName = processNode(ifNode->condition);
                    std::string thenName = processNode(ifNode->thenBranch);
                    std::string elseName = ifNode->elseBranch ? processNode(ifNode->elseBranch) : "";
                    
                    emitObjectAssociation(objName, conditionName, "condition");
                    emitObjectAssociation(objName, thenName, "then");
                    if (ifNode->elseBranch) emitObjectAssociation(objName, elseName, "else");

                    return objName;
                }
                case ASTNodeType::While: {
                    WhileNode* whileNode = static_cast<WhileNode*>(node);
                    std::string objName = "While" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);

                    std::string conditionName = processNode(whileNode->condition);
                    std::string bodyName = processNode(whileNode->body);

                    emitObjectAssociation(objName, conditionName, "condition");
                    emitObjectAssociation(objName, bodyName, "body");

                    return objName;
                }
                case ASTNodeType::Block: {
                    BlockNode* blockNode = static_cast<BlockNode*>(node);
                    std::string objName = "Block" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);

                    for (auto* child : blockNode->children) {
                        std::string childName = processNode(child);
                        emitObjectAssociation(objName, childName);
                    }

                    return objName;
                }
                case ASTNodeType::Parameter: {
                    ParameterNode* parameterNode = static_cast<ParameterNode*>(node);
                    std::string objName = "Parameter" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "type", parameterNode->type);
                    emitObjectData(objName, "name", parameterNode->name);

                    return objName;
                }
                case ASTNodeType::Function: {
                    FunctionNode* functionNode = static_cast<FunctionNode*>(node);
                    std::string objName = "Function" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "name", functionNode->name);
                    emitObjectData(objName, "returnType", functionNode->returnType);

                    for (auto* p : functionNode->parameters) {
                        std::string parameterName = processNode(p);
                        emitObjectAssociation(objName, parameterName, "parameter");
                    }

                    std::string bodyName = processNode(functionNode->body);
                    emitObjectAssociation(objName, bodyName, "body");

                    return objName;
                }
                case ASTNodeType::FunctionCall: {
                    FunctionCallNode* functionCall = static_cast<FunctionCallNode*>(node);
                    std::string objName = "FunctionCall" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    emitObjectData(objName, "resolvedType", functionCall->resolvedType);
                    emitObjectData(objName, "name", functionCall->name);

                    for (auto* p : functionCall->parameters) {
                        std::string parameterName = processNode(p);
                        emitObjectAssociation(objName, parameterName, "parameter");
                    }

                    return objName;
                }
                case ASTNodeType::Return: {
                    ReturnNode* returnNode = static_cast<ReturnNode*>(node);
                    std::string objName = "Return" + std::to_string(labelCount(node->nodeType));

                    emitObject(objName);
                    
                    std::string returnValueName = processNode(returnNode->returnValue);
                    emitObjectAssociation(objName, returnValueName, "returnValue");

                    return objName;
                }
                default:
                    throw std::runtime_error("unknown node");
            }
        }
};