#include "semanticAnalyzer.h"

SemanticAnalyzer::SemanticAnalyzer() { }

SemanticAnalyzer::~SemanticAnalyzer() { }

void SemanticAnalyzer::analyze(ASTNode* node) {
    enterScope();
    // if (!node || node->nodeType == ASTNodeType::Block) {
    //     auto root = static_cast<BlockNode*>(node);
    //     for (auto& stmt : root->children)
    //         analyzeStatement(stmt);
    // } else {
    //     throw std::runtime_error("Root node must be block");
    // }
    node->accept(*this);
    exitScope();
}

void SemanticAnalyzer::visit(VarDeclNode& node) {
    this->expectedType = node.type;
    node.value->accept(*this);
    this->expectedType = "";

    std::string varType = this->lastVisitedExpression->resolvedType;
    if (varType != node.type)
        throw std::runtime_error("Type mismatch in variable declaration: "  + node.name);
    else if (varType == "void") {
        throw std::runtime_error("Can't assign void");
    }
    declareVariable(node.name, varType);
}

void SemanticAnalyzer::visit(NumberLiteralNode& node) {
    node.resolvedType = "int";
    
    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(StringLiteralNode& node) {
    node.resolvedType = "string";

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(BooleanLiteralNode& node) {
    node.resolvedType = "bool";

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(VoidLiteralNode& node) {
    node.resolvedType = "void";

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(IntArrayLiteralNode& node) {
    for (auto& value : node.values) {
        value->accept(*this);
        if (this->lastVisitedExpression->resolvedType != "int")
            throw std::runtime_error("Int array must contain int values");
    }
    
    node.resolvedType = "int[]";

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(ArrayGetNode& node) {
    node.object->accept(*this);
    std::string objectType = this->lastVisitedExpression->resolvedType;

    if (objectType != "int[]")
        throw std::runtime_error("subscripted value is not array");
    
    node.index->accept(*this);
    std::string indexType = this->lastVisitedExpression->resolvedType;

    if (indexType != "int")
        throw std::runtime_error("array subscript is not int");
    
    node.resolvedType = "int";

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(MethodCallNode& node) {
    node.object->accept(*this);
    std::string objectType = this->lastVisitedExpression->resolvedType;

    if (typeMethods.find(objectType) == typeMethods.end())
        throw std::runtime_error("type " + objectType + " has no methods");

    if (typeMethods.at(objectType).find(node.methodName) == typeMethods.at(objectType).end())
        throw std::runtime_error("type " + objectType + " has no method named " + node.methodName);

    const FunctionSignature& expectedSig = typeMethods.at(objectType).at(node.methodName);

    if (node.arguments.size() != expectedSig.paramTypes.size())
        throw std::runtime_error("argument size mismatch for method " + node.methodName);
    
    for (size_t i = 0; i < node.arguments.size(); i++) {
        node.arguments[i]->accept(*this);
        if (this->lastVisitedExpression->resolvedType != expectedSig.paramTypes[i])
            throw std::runtime_error("argument type mismatch for method " + node.methodName);
    }

    node.resolvedType = expectedSig.returnType;
    
    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(IdentifierNode& node) {
    node.resolvedType = lookupVariable(node.name);

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(AssignNode& node) {
    node.var1->accept(*this);
    std::string var1Type = this->lastVisitedExpression->resolvedType;

    node.var2->accept(*this);
    std::string var2Type = this->lastVisitedExpression->resolvedType;
    
    if (var1Type != var2Type)
        throw std::runtime_error("Type mismatch in variable assignment");    
}

void SemanticAnalyzer::visit(BinaryOpNode& node) {
    node.left->accept(*this);
    std::string leftType = this->lastVisitedExpression->resolvedType;

    node.right->accept(*this);
    std::string rightType = this->lastVisitedExpression->resolvedType;

    if (node.op == "+" || node.op == "-" || node.op == "*" || node.op == "/" || node.op == "%") {
        if (leftType != "int" || rightType != "int")
            throw std::runtime_error("Arithmetic operations require int operands");
        node.resolvedType = "int";
    } else if (node.op == "==" || node.op == "!=" || node.op == ">" || node.op == ">=" || node.op == "<" || node.op == "<=") {
        if (leftType != rightType)
            throw std::runtime_error("Comparsion between incompatible types");
        node.resolvedType = "bool";
    } else if (node.op == "&&" || node.op == "||") {
        if (leftType != "bool" || rightType != "bool")
            throw std::runtime_error("Logical operations require bool operands");
        node.resolvedType = "bool";
    } else
        throw std::runtime_error("Unknown binary operator: " + node.op);

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(UnaryOpNode& node) {
    node.operand->accept(*this);
    std::string opType = this->lastVisitedExpression->resolvedType;

    if (node.op == "!") {
        if (opType != "bool")
            throw std::runtime_error("Unary '!' requres bool");
        node.resolvedType = "bool";
    } else if (node.op == "-") {
        if (opType != "int")
            throw std::runtime_error("Unary '-' requires int");
        node.resolvedType = "int";
    } else
        throw std::runtime_error("Unknown unary operator: " + node.op);

    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(IfNode& node) {
    node.condition->accept(*this);
    std::string condType = this->lastVisitedExpression->resolvedType;
    
    if (condType != "bool")
        throw std::runtime_error("if condition must be boolean");

    // enterScope();
    node.thenBranch->accept(*this);
    // exitScope();

    if (node.elseBranch) {
        // enterScope();
        node.elseBranch->accept(*this);
        // exitScope();
    }
}

void SemanticAnalyzer::visit(WhileNode& node) {
    node.condition->accept(*this);
    std::string condType = this->lastVisitedExpression->resolvedType;

    if (condType != "bool")
        throw std::runtime_error("while condition must be boolean");

    // enterScope();
    loopDepth++;
    node.body->accept(*this);
    loopDepth--;
    // exitScope();
}

void SemanticAnalyzer::visit(BreakNode& node) {
    if (loopDepth == 0)
        throw std::runtime_error("'break' statement not in loop");
}

void SemanticAnalyzer::visit(BlockNode& node) {
    enterScope();
    for (const auto& child : node.children)
        child->accept(*this);
    exitScope();
}

void SemanticAnalyzer::visit(ParameterNode& node) {
    
}

void SemanticAnalyzer::visit(FunctionNode& node) {
    FunctionSignature sig;
    sig.returnType = node.returnType;
    for (const auto& paramNode : node.parameters)
        sig.paramTypes.push_back(static_cast<ParameterNode*>(paramNode)->type);

    if (isReserved(node.name, sig))
        throw std::runtime_error("Function name " + node.name + " is reserved");

    this->functions[node.name].push_back(sig);
    // hasReturn = false;
    // currentReturnType = fn->returnType;

    currentReturnType = node.returnType;
    hasReturn = false;

    // enterScope();

    for (const auto& paramNode : node.parameters) {
        auto param = static_cast<ParameterNode*>(paramNode);
        declareVariable(param->name, {param->type});
    }

    node.body->accept(*this);

    // exitScope();

    if (!hasReturn && currentReturnType != "void")
        throw std::runtime_error("non-void function " + node.name + " must have return statement");
    hasReturn = false;
}

void SemanticAnalyzer::visit(FunctionCallNode& node) {
    std::vector<std::string> actualParamTypes;
    for (const auto& argExpression : node.parameters) {
        argExpression->accept(*this);
        actualParamTypes.push_back(this->lastVisitedExpression->resolvedType);
    }

    std::string returnType = findFunction(node.name, actualParamTypes, this->expectedType);
    if (returnType.empty())
        throw std::runtime_error("no matching function found");

    node.resolvedType = returnType;
    
    this->lastVisitedExpression = &node;
}

void SemanticAnalyzer::visit(ReturnNode& node) {
    this->expectedType = this->currentReturnType;
    node.returnValue->accept(*this);
    this->expectedType = "";

    std::string returnType = this->lastVisitedExpression->resolvedType;
    if (returnType != this->currentReturnType)
        throw std::runtime_error("return type mismatch in function signature");

    this->hasReturn = true;
}

void SemanticAnalyzer::enterScope() {
    scopes.push_back({});
}

void SemanticAnalyzer::exitScope() {
    scopes.pop_back();
}

void SemanticAnalyzer::declareVariable(const std::string& name, std::string data) {
    scopes.back()[name] = data;
}

std::string SemanticAnalyzer::lookupVariable(const std::string& name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
        if (scopes[i].count(name))
            return scopes[i][name];
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
    else if (functions.count(name))
        return search(functions[name]);
    
    return "";
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