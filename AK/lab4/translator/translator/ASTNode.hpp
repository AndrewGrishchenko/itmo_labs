#pragma once

#include <vector>
#include <string>
#include <iostream>

enum class ASTNodeType {
    VarDecl,
    
    NumberLiteral,
    StringLiteral,
    BooleanLiteral,
    VoidLiteral,
    IntArrayLiteral,
    ArrayGet,
    ArraySize,
    
    Identifier,
    Assignment,
    
    BinaryOp,
    UnaryOp,
    
    If,
    While,
    
    Block,
    Parameter,
    Function,
    CallParameter,
    FunctionCall,
    Expression,
    Return
};

struct ASTNode {
    ASTNodeType nodeType;
    ASTNode* parent = nullptr;

    ASTNode(ASTNodeType nodeType)
        : nodeType(nodeType) { }
};

struct VarDeclNode : ASTNode {
    std::string type;
    std::string name;
    ASTNode* value;

    VarDeclNode(const std::string& type, const std::string& name, ASTNode* value)
        : ASTNode(ASTNodeType::VarDecl), type(type), name(name), value(value) { }
};

struct NumberLiteralNode : ASTNode {
    int number;
    
    NumberLiteralNode(int number)
        : ASTNode(ASTNodeType::NumberLiteral), number(number) { }
};

struct StringLiteralNode : ASTNode {
    std::string value;

    StringLiteralNode(std::string value)
        : ASTNode(ASTNodeType::StringLiteral), value(value) { }
};

struct BooleanLiteralNode : ASTNode {
    bool value;

    BooleanLiteralNode(bool value)
        : ASTNode(ASTNodeType::BooleanLiteral), value(value) { }
};

struct VoidLiteralNode : ASTNode {
    VoidLiteralNode()
        : ASTNode(ASTNodeType::VoidLiteral) { }
};

struct IntArrayLiteralNode : ASTNode {
    std::vector<ASTNode*> values;
    
    IntArrayLiteralNode(std::vector<ASTNode*> values)
        : ASTNode(ASTNodeType::IntArrayLiteral), values(values) { }
};

struct ArrayGetNode : ASTNode {
    std::string name;
    ASTNode* index;

    ArrayGetNode(std::string name, ASTNode* index)
        : ASTNode(ASTNodeType::ArrayGet), name(name), index(index) { }
};

struct ArraySizeNode : ASTNode {
    std::string name;

    ArraySizeNode(std::string name)
        : ASTNode(ASTNodeType::ArraySize), name(name) { }
};

struct IdentifierNode : ASTNode {
    std::string name;
    
    IdentifierNode(const std::string name)
        : ASTNode(ASTNodeType::Identifier), name(name) { }
};

struct AssignNode : ASTNode {
    ASTNode* var1;
    ASTNode* var2;
    
    AssignNode(ASTNode* var1, ASTNode* var2)
        : ASTNode(ASTNodeType::Assignment), var1(var1), var2(var2) { }
};

struct BinaryOpNode : ASTNode {
    std::string op;
    ASTNode* left;
    ASTNode* right;

    BinaryOpNode(const std::string& op, ASTNode* left, ASTNode* right)
        : ASTNode(ASTNodeType::BinaryOp), op(op), left(left), right(right) { }
};

struct UnaryOpNode : ASTNode {
    std::string op;
    ASTNode* operand;

    UnaryOpNode(const std::string& op, ASTNode* operand)
        : ASTNode(ASTNodeType::UnaryOp), op(op), operand(operand) { }
};

struct IfNode : ASTNode {
    ASTNode* condition;
    ASTNode* thenBranch;
    ASTNode* elseBranch = nullptr;

    IfNode(ASTNode* condition, ASTNode* thenBranch, ASTNode* elseBranch = nullptr)
        : ASTNode(ASTNodeType::If), condition(condition), thenBranch(thenBranch), elseBranch(elseBranch) { }

};

struct WhileNode : ASTNode {
    ASTNode* condition;
    ASTNode* body;

    WhileNode(ASTNode* condition, ASTNode* body)
        : ASTNode(ASTNodeType::While), condition(condition), body(body) { }
};

struct BlockNode : ASTNode {
    std::vector<ASTNode*> children;

    void addChild(ASTNode* child) {
        if (child == nullptr) return;
        child->parent = this;
        children.push_back(child);
    }

    BlockNode()
        : ASTNode(ASTNodeType::Block) { }
};

struct ParameterNode : ASTNode {
    std::string name;
    std::string type;

    ParameterNode(std::string name, std::string type)
        : ASTNode(ASTNodeType::Parameter), name(name), type(type) { }
};

struct FunctionNode : ASTNode {
    std::string returnType;
    std::string name;
    std::vector<ASTNode*> parameters;
    ASTNode* body;

    FunctionNode(std::string returnType, std::string name, std::vector<ASTNode*> parameters, ASTNode* body)
        : ASTNode(ASTNodeType::Function), returnType(returnType), name(name), parameters(parameters), body(body) { }
};

struct FunctionCallNode : ASTNode {
    std::string name;
    std::vector<ASTNode*> parameters;

    FunctionCallNode(std::string name, std::vector<ASTNode*> parameters)
        : ASTNode(ASTNodeType::FunctionCall), name(name), parameters(parameters) { }
};

struct ReturnNode : ASTNode {
    ASTNode* returnValue;

    ReturnNode(ASTNode* returnValue)
        : ASTNode(ASTNodeType::Return), returnValue(returnValue) { }
};