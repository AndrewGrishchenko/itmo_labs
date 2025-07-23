#pragma once

struct VarDeclNode;
struct NumberLiteralNode;
struct CharLiteralNode;
struct StringLiteralNode;
struct BooleanLiteralNode;
struct VoidLiteralNode;
struct IntArrayLiteralNode;
struct ArrayGetNode;
struct MethodCallNode;
struct IdentifierNode;
struct AssignNode;
struct BinaryOpNode;
struct UnaryOpNode;
struct IfNode;
struct WhileNode;
struct BreakNode;
struct BlockNode;
struct ParameterNode;
struct FunctionNode;
struct FunctionCallNode;
struct ReturnNode;

class ASTVisitor {
    public:
        virtual ~ASTVisitor() = default;
        virtual void visit(VarDeclNode& node) = 0;
        virtual void visit(NumberLiteralNode& node) = 0;
        virtual void visit(CharLiteralNode& node) = 0;
        virtual void visit(StringLiteralNode& node) = 0;
        virtual void visit(BooleanLiteralNode& node) = 0;
        virtual void visit(VoidLiteralNode& node) = 0;
        virtual void visit(IntArrayLiteralNode& node) = 0;
        virtual void visit(ArrayGetNode& node) = 0;
        virtual void visit(MethodCallNode& node) = 0;
        virtual void visit(IdentifierNode& node) = 0;
        virtual void visit(AssignNode& node) = 0;
        virtual void visit(BinaryOpNode& node) = 0;
        virtual void visit(UnaryOpNode& node) = 0;
        virtual void visit(IfNode& node) = 0;
        virtual void visit(WhileNode& node) = 0;
        virtual void visit(BreakNode& node) = 0;
        virtual void visit(BlockNode& node) = 0;
        virtual void visit(ParameterNode& node) = 0;
        virtual void visit(FunctionNode& node) = 0;
        virtual void visit(FunctionCallNode& node) = 0;
        virtual void visit(ReturnNode& node) = 0;
};