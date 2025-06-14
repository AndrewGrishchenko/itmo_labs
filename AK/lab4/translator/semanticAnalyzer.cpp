#include "semanticAnalyzer.h"

SemanticAnalyzer::SemanticAnalyzer() { }

SemanticAnalyzer::~SemanticAnalyzer() { }

void SemanticAnalyzer::analyze(ASTNode* node) {
    enterScope();
    if (node->nodeType == ASTNodeType::Root) {
        auto root = static_cast<RootNode*>(node);
        for (auto stmt : root->children) {
            // auto statement = static_cast<StatementNode*>(stmt);
            if (stmt->nodeType == ASTNodeType::Function)
                analyzeFunction(stmt);
            else
                analyzeStatement(stmt);
        }
    } else {
        analyzeStatement(node);
    }
    exitScope();
}

void SemanticAnalyzer::enterScope() {
    scopes.push_back({});
}

void SemanticAnalyzer::exitScope() {
    scopes.pop_back();
}

void SemanticAnalyzer::declareVariable(const std::string& name, const std::string& type) {
    scopes.back()[name] = type;
    
    // scopes.back()[type] = name;
}

std::string SemanticAnalyzer::lookupVariable(const std::string& name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
        if (scopes[i].count(name))
            return scopes[i][name];
    }

    // for (size_t i = 0; i < scopes.size(); i++) {
    //     std::cout << "Map#" << i << std::endl;
    //     for (const auto& pair : scopes[i]) {
    //         std::cout << pair.first << ": " << pair.second << "\n";
    //     }
    //     std::cout << scopes[i].count("int") << std::endl;
    // }

    throw std::runtime_error("Undeclared variable: " + name);
}

void SemanticAnalyzer::analyzeFunction(ASTNode* node) {
    auto fn = static_cast<FunctionNode*>(node);
    FunctionSignature sig;
    sig.returnType = fn->returnType;
    for (const auto& param : fn->parameters)
        sig.paramTypes.push_back(static_cast<ParameterNode*>(param)->type);
    functions[fn->name] = sig;
    currentReturnType = fn->returnType;

    enterScope();
    for (const auto& param : fn->parameters)
        declareVariable(static_cast<FunctionNode*>(param)->name, static_cast<FunctionNode*>(param)->returnType);
    analyzeStatement(fn->body);
    exitScope();
}

void SemanticAnalyzer::analyzeStatement(ASTNode* node) {
    switch (node->nodeType) {
        case ASTNodeType::VarDecl: {
            auto var = static_cast<VarDeclNode*>(node);
            std::string exprType = analyzeExpression(var->value);
            if (exprType != var->type && var->type != "var")
                throw std::runtime_error("Type mismatch in variable declaration: " + var->name);
            else if (exprType == "void") {
                throw std::runtime_error("Can't assign void");
            }
            declareVariable(var->name, exprType);
            break;
        }

        case ASTNodeType::Assignment: {
            auto assign = static_cast<AssignNode*>(node);
            if (analyzeExpression(assign->var1) != analyzeExpression(assign->var2))
                throw std::runtime_error("Type mismatch in variable assignment"); // TODO
            break;
        }

        case ASTNodeType::Expression:
            analyzeExpression(static_cast<ExpressionNode*>(node)->expression);
            break;
        
        case ASTNodeType::If: {
            auto ifNode = static_cast<IfNode*>(node);
            std::string condType = analyzeExpression(ifNode->condition);
            if (condType != "bool")
                throw std::runtime_error("if condition must be boolean"); // TODO BOOLEAN EXPR;
            enterScope();
            analyzeStatement(ifNode->thenBranch);
            exitScope();
            if (ifNode->elseBranch) {
                enterScope();
                analyzeStatement(ifNode->elseBranch);
                exitScope();
            }
            break;
        }
        
        case ASTNodeType::While: {
            auto wh = static_cast<WhileNode*>(node);
            std::string condType = analyzeExpression(wh->condition);
            if (condType != "bool")
                throw std::runtime_error("while condition must be boolean");
            enterScope();
            analyzeStatement(wh->body);
            exitScope();
        }

        case ASTNodeType::Block: {
            auto block = static_cast<BlockNode*>(node);
            enterScope();
            for (auto stmt : block->children)
                analyzeStatement(stmt);
            exitScope();
            break;
        }

        case ASTNodeType::Return: {
            auto ret = static_cast<ReturnNode*>(node);
            std::string returnType = analyzeExpression(ret->returnValue);
            if (returnType != currentReturnType && returnType != "var")
                throw std::runtime_error("Return type mismatch function signature " + returnType);
            break;
        }

        default:
            throw std::runtime_error("Unknown statement type: " + nodeStr(node));
    }
}

std::string SemanticAnalyzer::analyzeExpression(ASTNode* node) {
    switch (node->nodeType) {
        case ASTNodeType::NumberLiteral:
            return "int";
        case ASTNodeType::StringLiteral:
            return "string";
        case ASTNodeType::BooleanLiteral:
            return "bool";
        case ASTNodeType::VoidLiteral:
            return "void";
        
        case ASTNodeType::Identifier: {
            auto id = static_cast<IdentifierNode*>(node);
            return lookupVariable(id->name);
        }

        case ASTNodeType::Expression: {
            auto expr = static_cast<ExpressionNode*>(node);
            return analyzeExpression(expr->expression);
        }

        case ASTNodeType::BinaryOp: {
            auto bin = static_cast<BinaryOpNode*>(node);
            std::string left = analyzeExpression(bin->left);
            std::string right = analyzeExpression(bin->right);

            if (bin->op == "+" || bin->op == "-" || bin->op == "*" || bin->op == "/") {
                if (left != "int" || right != "int")
                    throw std::runtime_error("Arithmetic operations require int operands");
                return "int";
            }
            if (bin->op == "==" || bin->op == "!=" || bin->op == ">" || bin->op == ">=" || bin->op == "<" || bin->op == "<=") {
                if (left != right)
                    throw std::runtime_error("Comparsion between incompatible types");
                return "bool";
            }
            if (bin->op == "&&" || bin->op == "||") {
                if (left != "bool" || right != "bool")
                    throw std::runtime_error("Logical operations require bool operands");
                return "bool";
            }
            throw std::runtime_error("Unknown binary operator: " + bin->op);
        }

        case ASTNodeType::UnaryOp: {
            auto un = static_cast<UnaryOpNode*>(node);
            std::string exprType = analyzeExpression(un->operand);
            if (un->op == "!") {
                if (exprType != "bool")
                    throw std::runtime_error("Unary '!' requres bool");
                return "bool";
            }
            if (un->op == "-") {
                if (exprType != "int")
                    throw std::runtime_error("Unary '-' requires int");
                return "int";
            }
            throw std::runtime_error("Unknown unary operator: " + un->op);
        }

        case ASTNodeType::CallParameter: {
            auto cp = static_cast<CallParameter*>(node);
            return analyzeExpression(cp->parameter);
        }

        case ASTNodeType::FunctionCall: {
            auto call = static_cast<FunctionCallNode*>(node);
            if (!functions.count(call->name))
                throw std::runtime_error("Call to undefined function: " + call->name);
            FunctionSignature sig = functions[call->name];
            if (sig.paramTypes.size() != call->parameters.size())
                throw std::runtime_error("Incorrect number of arguments to function: " + call->name);
            for (size_t i = 0; i < sig.paramTypes.size(); i++) {
                std::string actual = analyzeExpression(call->parameters[i]);
                if (actual != sig.paramTypes[i] && sig.paramTypes[i] != "var")
                    throw std::runtime_error("Argument type mismatch in function: " + call->name);
            }
            return sig.returnType;
        }

        default:
            throw std::runtime_error("Unsupported expression type: " + nodeStr(node));
    }
}



std::string SemanticAnalyzer::nodeStr(ASTNode* node) {
    switch (node->nodeType) {
        case ASTNodeType::Root:
            return "Root";
        case ASTNodeType::VarDecl:
            return "VarDecl";
        case ASTNodeType::NumberLiteral:
            return "NumberLiteral";
        case ASTNodeType::StringLiteral:
            return "StringLiteral";
        case ASTNodeType::BooleanLiteral:
            return "BooelanLiteral";
        case ASTNodeType::Identifier:
            return "Indetifier";
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