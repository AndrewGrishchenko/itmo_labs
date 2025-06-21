#ifndef _AST_NODE_H
#define _AST_NODE_H

#include <vector>
#include <string>
#include <iostream>

enum class ASTNodeType {
    VarDecl,
    
    NumberLiteral,
    StringLiteral,
    BooleanLiteral,
    VoidLiteral,
    
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

inline void printIndent(int indent) {
    for (int i = 0; i < indent; ++i) std::cout << "  ";
}

struct ASTNode {
    ASTNodeType nodeType;
    ASTNode* parent = nullptr;

    ASTNode(ASTNodeType nodeType)
        : nodeType(nodeType) { }

    void printNode(int indent = 0) const {
        std::cout << "abstract astnode\n";
    }

    virtual void print(int indent = 0) const = 0;
};

struct VarDeclNode : ASTNode {
    std::string type;
    std::string name;
    ASTNode* value;

    VarDeclNode(const std::string& type, const std::string& name, ASTNode* value)
        : ASTNode(ASTNodeType::VarDecl), type(type), name(name), value(value) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "VarDecl: " << name << "\n";
        if (value) value->print(indent + 1);
    }
};

struct NumberLiteralNode : ASTNode {
    int number;
    
    NumberLiteralNode(int number)
        : ASTNode(ASTNodeType::NumberLiteral), number(number) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "Number: " << number << "\n";
    }
};

struct StringLiteralNode : ASTNode {
    std::string value;

    StringLiteralNode(std::string value)
        : ASTNode(ASTNodeType::StringLiteral), value(value) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "String: " << value << "\n";
    }
};

struct BooleanLiteralNode : ASTNode {
    bool value;

    BooleanLiteralNode(bool value)
        : ASTNode(ASTNodeType::BooleanLiteral), value(value) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "Boolean: " << (value ? "true" : "false") << " \n";
    }
};

struct VoidLiteralNode : ASTNode {
    VoidLiteralNode()
        : ASTNode(ASTNodeType::VoidLiteral) { }
    
    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "VoidLiteral\n";
    }
};

struct IdentifierNode : ASTNode {
    std::string name;
    
    IdentifierNode(const std::string name)
        : ASTNode(ASTNodeType::Identifier), name(name) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "Identifier: " << name << "\n";
    }
};

struct AssignNode : ASTNode {
    ASTNode* var1;
    ASTNode* var2;
    
    AssignNode(ASTNode* var1, ASTNode* var2)
        : ASTNode(ASTNodeType::Assignment), var1(var1), var2(var2) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "Assignment:\n";
        if (var1) var1->print(indent + 1);
        if (var2) var2->print(indent + 1);
    }
};

struct BinaryOpNode : ASTNode {
    std::string op;
    ASTNode* left;
    ASTNode* right;

    BinaryOpNode(const std::string& op, ASTNode* left, ASTNode* right)
        : ASTNode(ASTNodeType::BinaryOp), op(op), left(left), right(right) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "BinaryOp: " << op << "\n";
        if (left) left->print(indent + 1);
        if (right) right->print(indent + 1);
    }
};

struct UnaryOpNode : ASTNode {
    std::string op;
    ASTNode* operand;

    UnaryOpNode(const std::string& op, ASTNode* operand)
        : ASTNode(ASTNodeType::UnaryOp), op(op), operand(operand) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "UnaryOp: " << op << "\n";
        if (operand) operand->print(indent + 1);
    }
};

struct IfNode : ASTNode {
    ASTNode* condition;
    ASTNode* thenBranch;
    ASTNode* elseBranch = nullptr;

    IfNode(ASTNode* condition, ASTNode* thenBranch, ASTNode* elseBranch = nullptr)
        : ASTNode(ASTNodeType::If), condition(condition), thenBranch(thenBranch), elseBranch(elseBranch) { }
    
    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "If\n";
        if (condition) condition->print(indent + 1);
        if (thenBranch) thenBranch->print(indent + 1);
        if (elseBranch) elseBranch->print(indent + 1);
    }
};

struct WhileNode : ASTNode {
    ASTNode* condition;
    ASTNode* body;

    WhileNode(ASTNode* condition, ASTNode* body)
        : ASTNode(ASTNodeType::While), condition(condition), body(body) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "While\n";
        if (condition) condition->print(indent + 1);
        if (body) body->print(indent + 1);
    }
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

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "Block\n";
        for (auto child : children) {
            if (child != nullptr) {
                child->print(indent + 1);
            }
        }
    }
};

struct ParameterNode : ASTNode {
    std::string name;
    std::string type;

    ParameterNode(std::string name, std::string type)
        : ASTNode(ASTNodeType::Parameter), name(name), type(type) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "Parameter: \n";
        
        printIndent(indent + 1);
        std::cout << "Name: " << name << "\n";
        
        printIndent(indent + 1);
        std::cout << "Type: " << type << "\n"; 
    }
};

struct FunctionNode : ASTNode {
    std::string returnType;
    std::string name;
    std::vector<ASTNode*> parameters;
    ASTNode* body;

    FunctionNode(std::string returnType, std::string name, std::vector<ASTNode*> parameters, ASTNode* body)
        : ASTNode(ASTNodeType::Function), returnType(returnType), name(name), parameters(parameters), body(body) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "FunctionNode\n";

        printIndent(indent + 1);
        std::cout << "returnType: " << returnType << "\n";

        printIndent(indent + 1);
        std::cout << "name: " << name << "\n";

        printIndent(indent + 1);
        std::cout << "parameters:\n";
        for (auto parameter : parameters) parameter->print(indent + 2);

        printIndent(indent + 1);
        std::cout << "body:\n";
        body->print(indent + 2);
    }
};

struct CallParameterNode : ASTNode {
    ASTNode* parameter;

    CallParameterNode(ASTNode* parameter)
        : ASTNode(ASTNodeType::CallParameter), parameter(parameter) { }

    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "CallPararameter\n";

        printIndent(indent + 1);
        std::cout << "parameter:\n";

        parameter->print(indent + 2);
    }
};

struct FunctionCallNode : ASTNode {
    std::string name;
    std::vector<ASTNode*> parameters;

    FunctionCallNode(std::string name, std::vector<ASTNode*> parameters)
        : ASTNode(ASTNodeType::FunctionCall), name(name), parameters(parameters) { }
    
    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "FunctionCall\n";

        printIndent(indent + 1);
        std::cout << "name: " << name << "\n";

        printIndent(indent + 1);
        std::cout << "parameters:\n";

        for (auto parameter : parameters) parameter->print(indent + 2);
    }
};

struct ExpressionNode : ASTNode {
    ASTNode* expression;

    ExpressionNode(ASTNode* expression)
        : ASTNode(ASTNodeType::Expression), expression(expression) { }

    void print(int indent = 0) const override {
        // std::cout << "{Expression}\n";
        expression->print(indent);
    }
};

struct ReturnNode : ASTNode {
    ASTNode* returnValue;

    ReturnNode(ASTNode* returnValue)
        : ASTNode(ASTNodeType::Return), returnValue(returnValue) { }
    
    void print(int indent = 0) const override {
        printIndent(indent);
        std::cout << "ReturnNode\n";

        printIndent(indent + 1);
        std::cout << "returnValue:\n";

        returnValue->print(indent + 2);
    }
};

#endif