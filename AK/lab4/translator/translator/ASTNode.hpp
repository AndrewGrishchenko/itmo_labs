#pragma once

#include <vector>
#include <string>
#include <iostream>
#include <memory>

#include "ASTVisitor.hpp"

enum class ASTNodeType {
    VarDecl,
    
    NumberLiteral,
    StringLiteral,
    BooleanLiteral,
    VoidLiteral,
    IntArrayLiteral,
    ArrayGet,
    ArraySize,
    MethodCall,
    
    Identifier,
    Assignment,
    
    BinaryOp,
    UnaryOp,
    
    If,
    While,
    Break,
    
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

    ASTNode(ASTNodeType nodeType)
        : nodeType(nodeType) { }

    virtual void accept(ASTVisitor& visitor) = 0;
};

struct ExpressionNode : ASTNode {
    std::string resolvedType;

    ExpressionNode(ASTNodeType nodeType)
        : ASTNode(nodeType) { }
};

struct VarDeclNode : ASTNode {
    std::string type;
    std::string name;
    ASTNode* value;

    VarDeclNode(const std::string& type, const std::string& name, ASTNode* value)
        : ASTNode(ASTNodeType::VarDecl), type(type), name(name), value(value) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct NumberLiteralNode : ExpressionNode {
    int number;
    
    NumberLiteralNode(int number)
        : ExpressionNode(ASTNodeType::NumberLiteral), number(number) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct StringLiteralNode : ExpressionNode {
    std::string value;

    StringLiteralNode(std::string value)
        : ExpressionNode(ASTNodeType::StringLiteral), value(value) { }
    
    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct BooleanLiteralNode : ExpressionNode {
    bool value;

    BooleanLiteralNode(bool value)
        : ExpressionNode(ASTNodeType::BooleanLiteral), value(value) { }
    
    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct VoidLiteralNode : ExpressionNode {
    VoidLiteralNode()
        : ExpressionNode(ASTNodeType::VoidLiteral) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct IntArrayLiteralNode : ExpressionNode {
    std::vector<ASTNode*> values;
    
    IntArrayLiteralNode(std::vector<ASTNode*> values)
        : ExpressionNode(ASTNodeType::IntArrayLiteral), values(values) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct ArrayGetNode : ExpressionNode {
    ASTNode* object;
    ASTNode* index;

    ArrayGetNode(ASTNode* object, ASTNode* index)
        : ExpressionNode(ASTNodeType::ArrayGet), object(object), index(index) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct MethodCallNode : ExpressionNode {
    ASTNode* object;
    std::string methodName;
    std::vector<ASTNode*> arguments;

    MethodCallNode(ASTNode* object, const std::string& methodName, std::vector<ASTNode*> arguments)
        : ExpressionNode(ASTNodeType::MethodCall), object(object), methodName(methodName), arguments(arguments) { }
    
    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct IdentifierNode : ExpressionNode {
    std::string name;
    
    IdentifierNode(const std::string name)
        : ExpressionNode(ASTNodeType::Identifier), name(name) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct AssignNode : ASTNode {
    ASTNode* var1;
    ASTNode* var2;
    
    AssignNode(ASTNode* var1, ASTNode* var2)
        : ASTNode(ASTNodeType::Assignment), var1(var1), var2(var2) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct BinaryOpNode : ExpressionNode {
    std::string op;
    ASTNode* left;
    ASTNode* right;

    BinaryOpNode(const std::string& op, ASTNode* left, ASTNode* right)
        : ExpressionNode(ASTNodeType::BinaryOp), op(op), left(left), right(right) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct UnaryOpNode : ExpressionNode {
    std::string op;
    ASTNode* operand;

    UnaryOpNode(const std::string& op, ASTNode* operand)
        : ExpressionNode(ASTNodeType::UnaryOp), op(op), operand(operand) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct IfNode : ASTNode {
    ASTNode* condition;
    ASTNode* thenBranch;
    ASTNode* elseBranch = nullptr;

    IfNode(ASTNode* condition, ASTNode* thenBranch, ASTNode* elseBranch = nullptr)
        : ASTNode(ASTNodeType::If), condition(condition), thenBranch(thenBranch), elseBranch(elseBranch) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct WhileNode : ASTNode {
    ASTNode* condition;
    ASTNode* body;

    WhileNode(ASTNode* condition, ASTNode* body)
        : ASTNode(ASTNodeType::While), condition(condition), body(body) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct BreakNode : ASTNode {
    BreakNode()
        : ASTNode(ASTNodeType::Break) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct BlockNode : ASTNode {
    std::vector<ASTNode*> children;

    void addChild(ASTNode* child) {
        if (child == nullptr) return;
        children.push_back(child);
    }

    BlockNode()
        : ASTNode(ASTNodeType::Block) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct ParameterNode : ASTNode {
    std::string name;
    std::string type;

    ParameterNode(std::string name, std::string type)
        : ASTNode(ASTNodeType::Parameter), name(name), type(type) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct FunctionNode : ASTNode {
    std::string returnType;
    std::string name;
    std::vector<ASTNode*> parameters;
    ASTNode* body;

    FunctionNode(std::string returnType, std::string name, std::vector<ASTNode*> parameters, ASTNode* body)
        : ASTNode(ASTNodeType::Function), returnType(returnType), name(name), parameters(parameters), body(body) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct FunctionCallNode : ExpressionNode {
    std::string name;
    std::vector<ASTNode*> parameters;

    FunctionCallNode(std::string name, std::vector<ASTNode*> parameters)
        : ExpressionNode(ASTNodeType::FunctionCall), name(name), parameters(parameters) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};

struct ReturnNode : ASTNode {
    ASTNode* returnValue;

    ReturnNode(ASTNode* returnValue)
        : ASTNode(ASTNodeType::Return), returnValue(returnValue) { }

    void accept(ASTVisitor& visitor) override {
        visitor.visit(*this);
    }
};