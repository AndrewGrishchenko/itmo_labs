#include "semanticAnalyzer.h"

SemanticAnalyzer::SemanticAnalyzer() { }

SemanticAnalyzer::~SemanticAnalyzer() { }

void SemanticAnalyzer::analyze(ASTNode* node) {
    enterScope();
    if (!node || node->nodeType == ASTNodeType::Block) {
        auto root = static_cast<BlockNode*>(node);
        for (auto stmt : root->children) {
            if (stmt->nodeType == ASTNodeType::Function)
                analyzeFunction(stmt);
        }

        for (auto stmt : root->children) {
            if (stmt->nodeType != ASTNodeType::Function)
                analyzeStatement(stmt);
        }
    } else {
        throw std::runtime_error("Root node must be block");
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
}

std::string SemanticAnalyzer::lookupVariable(const std::string& name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
        if (scopes[i].count(name))
            return scopes[i][name];
    }

    std::cout << "Seman didnt found var " << name << "\n";
    for (size_t i = 0; i < scopes.size(); i++) {
        std::cout << "SCOPE #" << i << std::endl;
        for (auto& p : scopes[i]) {
            std::cout << "  " << p.first << ": " << p.second << std::endl;
        }
    }

    throw std::runtime_error("Undeclared variable: " + name);
}

bool SemanticAnalyzer::isReserved(const std::string& name, FunctionSignature sig) {
    for (auto& p : reservedFunctions) {
        if (p.first == name) {
            for (auto& reserved : p.second) {
                if (reserved == sig)
                    return true;
            }
        }
    }
    return false;
}

std::string SemanticAnalyzer::findFunction(const std::string& name, std::vector<std::string> paramTypes) {
    if (reservedFunctions.find(name) != reservedFunctions.end()) {
        for (const auto& fn : reservedFunctions.at(name)) {
            if (fn.paramTypes == paramTypes) return fn.returnType;
        } 
    } else {
        for (auto& fn : functions[name]) {
            if (fn.paramTypes == paramTypes) return fn.returnType;
        }
    }
    return "";
}

void SemanticAnalyzer::analyzeFunction(ASTNode* node) {
    auto fn = static_cast<FunctionNode*>(node);

    FunctionSignature sig;
    sig.returnType = fn->returnType;
    for (const auto& param : fn->parameters)
        sig.paramTypes.push_back(static_cast<ParameterNode*>(param)->type);

    if (isReserved(fn->name, sig))
        throw std::runtime_error("Function name " + fn->name + " is reserved");

    functions[fn->name].push_back(sig);
    hasReturn = false;
    currentReturnType = fn->returnType;

    enterScope();
    for (const auto& param : fn->parameters)
        declareVariable(static_cast<ParameterNode*>(param)->name, static_cast<ParameterNode*>(param)->type);
    analyzeStatement(fn->body);
    exitScope();

    if (!hasReturn)
        throw std::runtime_error("Function must contain at least 1 return");
    else
        hasReturn = false;
}

void SemanticAnalyzer::analyzeStatement(ASTNode* node) {
    switch (node->nodeType) {
        case ASTNodeType::VarDecl: {
            auto var = static_cast<VarDeclNode*>(node);

            std::string exprType = analyzeExpression(var->value);
            if (exprType != var->type)
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
                throw std::runtime_error("Type mismatch in variable assignment");
            break;
        }
        
        case ASTNodeType::If: {
            auto ifNode = static_cast<IfNode*>(node);
            std::string condType = analyzeExpression(ifNode->condition);
            if (condType != "bool")
                throw std::runtime_error("if condition must be boolean");
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
            break;
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
            if (returnType != currentReturnType)
                throw std::runtime_error("Return type mismatch function signature " + returnType);
            hasReturn = true;
            break;
        }

        default:
            analyzeExpression(node);
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

        case ASTNodeType::BinaryOp: {
            auto bin = static_cast<BinaryOpNode*>(node);
            std::string left = analyzeExpression(bin->left);
            std::string right = analyzeExpression(bin->right);

            if (bin->op == "+" || bin->op == "-" || bin->op == "*" || bin->op == "/" || bin->op == "%") {
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

        case ASTNodeType::FunctionCall: {
            auto call = static_cast<FunctionCallNode*>(node);
            
            FunctionSignature sig;
            for (auto& param : call->parameters)
                sig.paramTypes.push_back(analyzeExpression(param));
            sig.returnType = findFunction(call->name, sig.paramTypes);
            
            if (sig.returnType.empty())
                throw std::runtime_error("Unable to find function");
            
            return sig.returnType;
        }

        default:
            throw std::runtime_error("Unsupported expression type: " + nodeStr(node));
    }
}

std::string SemanticAnalyzer::nodeStr(ASTNode* node) {
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