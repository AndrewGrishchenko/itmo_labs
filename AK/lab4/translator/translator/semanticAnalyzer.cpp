#include "semanticAnalyzer.h"

SemanticAnalyzer::SemanticAnalyzer() { }

SemanticAnalyzer::~SemanticAnalyzer() { }

void SemanticAnalyzer::analyze(ASTNode* node) {
    enterScope();
    if (!node || node->nodeType == ASTNodeType::Block) {
        auto root = static_cast<BlockNode*>(node);
        for (auto& stmt : root->children)
            analyzeStatement(stmt);
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

void SemanticAnalyzer::declareVariable(const std::string& name, VariableData data) {
    scopes.back()[name] = data;
}

std::string SemanticAnalyzer::lookupVariable(const std::string& name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
        if (scopes[i].count(name))
            return scopes[i][name].type;
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

std::string SemanticAnalyzer::findFunction(const std::string& name, std::vector<std::string> paramTypes, std::string& expected) {
    std::cout << "finding function " << name << "\n";
    std::cout << "expecting " << expected << "\n";

    auto search = [&](const std::vector<FunctionSignature>& candidates) -> std::string {
        for (const auto& fn : candidates) {
            if (fn.paramTypes == paramTypes) {
                if (expected.empty() || fn.returnType == expected)
                    return fn.returnType;
            }
        }
        return "";
    };

    if (reservedFunctions.count(name))
        return search(reservedFunctions.at(name));
    else
        return search(functions[name]);
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

            VariableData varData = analyzeExpression(var->value, var->type).type;
            if (varData.type != var->type)
                throw std::runtime_error("Type mismatch in variable declaration: "  + var->name);
            else if (varData.type == "void") {
                throw std::runtime_error("Can't assign void");
            }
            declareVariable(var->name, varData);
            break;
        }

        case ASTNodeType::Assignment: {
            auto assign = static_cast<AssignNode*>(node);
            std::string var1type = analyzeExpression(assign->var1).type;
            if (var1type != analyzeExpression(assign->var2, var1type).type)
                throw std::runtime_error("Type mismatch in variable assignment");
            break;
        }
        
        case ASTNodeType::If: {
            auto ifNode = static_cast<IfNode*>(node);
            std::string condType = analyzeExpression(ifNode->condition).type;
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
            std::string condType = analyzeExpression(wh->condition).type;
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
            std::string returnType = analyzeExpression(ret->returnValue, currentReturnType).type;
            if (returnType != currentReturnType)
                throw std::runtime_error("Return type mismatch function signature " + returnType);
            hasReturn = true;
            break;
        }

        case ASTNodeType::Function:
            analyzeFunction(node);
            break;

        default:
            analyzeExpression(node);
    }
}

SemanticAnalyzer::VariableData SemanticAnalyzer::analyzeExpression(ASTNode* node, std::string expected) {
    switch (node->nodeType) {
        case ASTNodeType::NumberLiteral:
            return {"int"};
        case ASTNodeType::StringLiteral:
            return {"string"};
        case ASTNodeType::BooleanLiteral:
            return {"bool"};
        case ASTNodeType::VoidLiteral:
            return {"void"};
        case ASTNodeType::IntArrayLiteral: {
            auto intArray = static_cast<IntArrayLiteralNode*>(node);
            for (auto value : intArray->values) {
                if (analyzeExpression(value).type != "int")
                    throw std::runtime_error("Int array must contain int values");
            }
            
            return {"int[]", intArray->values.size()};
        }
        
        case ASTNodeType::Identifier: {
            auto id = static_cast<IdentifierNode*>(node);
            return lookupVariable(id->name);
        }

        case ASTNodeType::ArrayGet: {
            auto arrayGet = static_cast<ArrayGetNode*>(node);
            return {"int"};
        }

        case ASTNodeType::BinaryOp: {
            auto bin = static_cast<BinaryOpNode*>(node);
            std::string left = analyzeExpression(bin->left).type;
            std::string right = analyzeExpression(bin->right).type;

            if (bin->op == "+" || bin->op == "-" || bin->op == "*" || bin->op == "/" || bin->op == "%") {
                if (left != "int" || right != "int")
                    throw std::runtime_error("Arithmetic operations require int operands");
                return {"int"};
            }
            if (bin->op == "==" || bin->op == "!=" || bin->op == ">" || bin->op == ">=" || bin->op == "<" || bin->op == "<=") {
                if (left != right)
                    throw std::runtime_error("Comparsion between incompatible types");
                return {"bool"};
            }
            if (bin->op == "&&" || bin->op == "||") {
                if (left != "bool" || right != "bool")
                    throw std::runtime_error("Logical operations require bool operands");
                return {"bool"};
            }
            throw std::runtime_error("Unknown binary operator: " + bin->op);
        }

        case ASTNodeType::UnaryOp: {
            auto un = static_cast<UnaryOpNode*>(node);
            std::string exprType = analyzeExpression(un->operand).type;
            if (un->op == "!") {
                if (exprType != "bool")
                    throw std::runtime_error("Unary '!' requres bool");
                return {"bool"};
            }
            if (un->op == "-") {
                if (exprType != "int")
                    throw std::runtime_error("Unary '-' requires int");
                return {"int"};
            }
            throw std::runtime_error("Unknown unary operator: " + un->op);
        }

        case ASTNodeType::FunctionCall: {
            auto call = static_cast<FunctionCallNode*>(node);

            FunctionSignature sig;
            for (auto& param : call->parameters)
                sig.paramTypes.push_back(analyzeExpression(param).type);
            sig.returnType = findFunction(call->name, sig.paramTypes, expected);

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