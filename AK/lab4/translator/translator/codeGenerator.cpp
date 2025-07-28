#include "codeGenerator.h"

CodeGenerator::CodeGenerator() { }
CodeGenerator::~CodeGenerator() { }

std::string CodeGenerator::generateCode(ASTNode* root) {
    if (!root || root->nodeType != ASTNodeType::Block)
        throw std::runtime_error("Root node must be block");

    dataSection.clear();
    codeSection.clear();
    funcSection.clear();
    variables.clear();
    functionLabels.clear();
    functions.clear();

    labelCounter = 0;
    strCounter = 0;
    arrCounter = 0;

    // processRoot(root);
    //TODO: idk

    emitCodeLabel("_start");
    root->accept(*this);
    emitCode("halt");

    // BlockNode* rootBlock = static_cast<BlockNode*>(root);

    // for (const auto& child : rootBlock->children) {
    //     std::cout << "accepting " + nodeStr(child) + "\n";
    //     child->accept(*this);
    // }

    return assembleCode();
}

void CodeGenerator::visit(VarDeclNode& node) {
    std::string varLabel = getVarLabel(node.name);

    if (node.type == "int[]") {
        emitData(varLabel + ": 0");
        // emitData(varLabel + "_size: 0");
        variables[varLabel] = node.type;

        node.value->accept(*this);

        emitCode("st " + varLabel);
        // emitCode("pop");
        // emitCode("st " + varLabel + "_size");
    } else {
        emitData(varLabel + ": 0");
        variables[varLabel] = node.type;
        node.value->accept(*this);
        emitCode("st " + varLabel);
    }
}

void CodeGenerator::visit(NumberLiteralNode& node) {
    emitCode("ldi " + std::to_string(node.number));
}

void CodeGenerator::visit(CharLiteralNode& node) {
    int char_code = static_cast<int>(node.value);

    emitCode("ldi " + std::to_string(char_code));
}

void CodeGenerator::visit(StringLiteralNode& node) {
    std::string strLabel = "str_" + std::to_string(strCounter++);

    emitData(strLabel + ": \"" + node.value + "\\0\"");
    emitCode("ldi " + strLabel);
}

void CodeGenerator::visit(BooleanLiteralNode& node) {
    emitCode("ldi " + std::to_string(node.value ? 1 : 0));
}

void CodeGenerator::visit(VoidLiteralNode& node) {

}

void CodeGenerator::visit(IntArrayLiteralNode& node) {
    std::string arrLabel = "arr_" + std::to_string(arrCounter++);
    // emitData(arrLabel + "_size: " + std::to_string(node.values.size()));
    
    std::string dataLine = arrLabel + ": ";
    for (size_t i = 0; i < node.values.size(); i++) {
        auto numberNode = static_cast<NumberLiteralNode*>(node.values[i]);
        dataLine += std::to_string(numberNode->number) + (i == node.values.size() - 1 ? "" : ", ");
    }
    emitData(dataLine);
    
    // emitCode("ld " + arrLabel + "_size");
    // emitCode("push");
    emitCode("ldi " + arrLabel);
}

void CodeGenerator::visit(ArrayGetNode& node) {
    //TODO: semanalyze check indexing overflow underflow
    node.object->accept(*this);
    emitCode("push");
    node.index->accept(*this);
    
    emitCode("st temp_right");
    emitCode("pop");
    emitCode("add temp_right");
    emitCode("st temp_right");
    emitCode("lda temp_right");
}

void CodeGenerator::visit(MethodCallNode& node) {
    if (node.object->nodeType != ASTNodeType::Identifier)
        throw std::runtime_error("method call on complex expressions not supported");
    
    IdentifierNode* objIdentifier = static_cast<IdentifierNode*>(node.object);
    
    std::string varLabel = getVarLabel(objIdentifier->name);

    if (node.methodName == "size") {
        // emitCode("ld " + varLabel + "_size");
        node.object->accept(*this);
        
        emitCode("call arr_size");
    }
}

void CodeGenerator::visit(IdentifierNode& node) {
    std::string varLabel = getVarLabel(node.name);
    std::string varType = variables.at(varLabel);

    if (varType == "int[]") {
        emitCode("ld " + varLabel);
    } else {
        emitCode("ld " + varLabel);
    }
}

void CodeGenerator::visit(AssignNode& node) {
    node.var2->accept(*this);

    ASTNode* lhs = node.var1;

    if (lhs->nodeType == ASTNodeType::Identifier) {
        auto identifier = static_cast<IdentifierNode*>(lhs);
        std::string varLabel = getVarLabel(identifier->name);
        emitCode("st " + varLabel);
    } else if (lhs->nodeType == ASTNodeType::ArrayGet) {
        auto arrayGet = static_cast<ArrayGetNode*>(lhs);
        emitCode("push");

        arrayGet->index->accept(*this);
        emitCode("st temp_right");
        
        arrayGet->object->accept(*this);
        emitCode("add temp_right");
        emitCode("st temp_right");

        emitCode("pop");
        emitCode("sta temp_right");
    }
}

// void CodeGenerator::visit(BinaryOpNode& node) {
//     const std::string& op = node.op;
//     bool isLogicalOp = (op == ">" || op == "<" || op == ">=" || op == "<=" || op == "==" || op == "!=" || op == "&&" || op == "||");

//     if (isLogicalOp && !currentTrueLabel.empty() && !currentFalseLabel.empty() &&
//         (op == ">" || op == "<" || op == ">=" || op == "<=" || op == "==" || op == "!=" || op == "&&" || op == "||")) {
//         if (op == "&&") {
//             std::string rightSideLabel = getNewLabel();

//             std::string oldFalse = currentFalseLabel;
//             visitWithLabels(node.left, rightSideLabel, oldFalse);

//             emitCodeLabel(rightSideLabel);
//             visitWithLabels(node.right, currentTrueLabel, currentFalseLabel);
//         } else if (op == "||") {
//             std::string rightSideLabel = getNewLabel();

//             std::string oldTrue = currentTrueLabel;
//             visitWithLabels(node.left, oldTrue, rightSideLabel);

//             emitCodeLabel(rightSideLabel);
//             visitWithLabels(node.right, currentTrueLabel, currentFalseLabel);
//         } else {
//             node.right->accept(*this);
//             emitCode("st temp_right");
//             node.left->accept(*this);
//             emitCode("sub temp_right");

//             if (op == "==") emitCode("jz " + currentTrueLabel);
//             else if (op == "!=") emitCode("jnz " + currentTrueLabel);
//             else if (op == ">") emitCode("jg " + currentTrueLabel);
//             else if (op == ">=") emitCode("jge " + currentTrueLabel);
//             else if (op == "<") emitCode("jl " + currentTrueLabel);
//             else if (op == "<=") emitCode("jle " + currentTrueLabel);

//             emitCode("jmp " + currentFalseLabel);
//         }
//     } else {
//         node.right->accept(*this);
//         emitCode("st temp_right");
//         node.left->accept(*this);
        
//         if (op == "+") emitCode("add temp_right");
//         else if (op == "-") emitCode("sub temp_right");
//         else if (op == "*") emitCode("mul temp_right");
//         else if (op == "/") emitCode("div temp_right");
//         else if (op == "%") emitCode("rem temp_right");
//         else {
//             if (op == "&&") {
//                 emitCode("mul temp_right");
//             } else if (op == "||") {
//                 emitCode("add temp_right");
                
//                 std::string falseLabel = getNewLabel();
//                 std::string endLabel = getNewLabel();
                
//                 emitCode("jz " + falseLabel);
//                 emitCode("ldi 1");
//                 emitCode("jmp " + endLabel);
                
//                 emitCodeLabel(falseLabel);
//                 emitCode("ldi 0");

//                 emitCodeLabel(endLabel);
//             } else {
//                 emitCode("sub temp_right");

//                 std::string trueLabel = getNewLabel();
//                 std::string endLabel = getNewLabel();

//                 if (op == "==") emitCode("jz " + trueLabel);
//                 else if (op == "!=") emitCode("jnz " + trueLabel);
//                 else if (op == ">") emitCode("jg " + trueLabel);
//                 else if (op == ">=") emitCode("jge " + trueLabel);
//                 else if (op == "<") emitCode("jl " + trueLabel);
//                 else if (op == "<=") emitCode("jle " + trueLabel);

//                 emitCode("ldi 0");
//                 emitCode("jmp " + endLabel);

//                 emitCodeLabel(trueLabel);
//                 emitCode("ldi 1");

//                 emitCodeLabel(endLabel);
//             }
//         }


//         //generate 0/1 for ex bool a = 4 > 5;

//     }
// }

void CodeGenerator::visit(BinaryOpNode& node) {
    const std::string& op = node.op;
    bool isLogicalOp = (op == ">" || op == "<" || op == ">=" || op == "<=" || op == "==" || op == "!=" || op == "&&" || op == "||");

    if (isLogicalOp && !currentTrueLabel.empty() && !currentFalseLabel.empty()) {
        if (op == "&&") {
            std::string rightSideLabel = getNewLabel();
            visitWithLabels(node.left, rightSideLabel, currentFalseLabel);

            emitCodeLabel(rightSideLabel);
            visitWithLabels(node.right, currentTrueLabel, currentFalseLabel);

            return;
        }

        if (op == "||") {
            std::string rightSideLabel = getNewLabel();
            visitWithLabels(node.left, currentTrueLabel, rightSideLabel);

            emitCodeLabel(rightSideLabel);
            visitWithLabels(node.right, currentTrueLabel, currentFalseLabel);

            return;
        }

        node.left->accept(*this);
        emitCode("push");
        
        node.right->accept(*this);
        emitCode("st temp_right");
        emitCode("pop");
        emitCode("sub temp_right");

        if (op == "==")
            emitCode("jz " + currentTrueLabel);
        else if (op == "!=")
            emitCode("jnz " + currentTrueLabel);
        else if (op == ">")
            emitCode("jg " + currentTrueLabel);
        else if (op == ">=")
            emitCode("jge " + currentTrueLabel);
        else if (op == "<")
            emitCode("jl " + currentTrueLabel);
        else if (op == "<=")
            emitCode("jle " + currentTrueLabel);

        emitCode("jmp " + currentFalseLabel);
    } else {
        node.left->accept(*this);
        emitCode("push");

        node.right->accept(*this);
        emitCode("st temp_right");
        emitCode("pop");
        
        if (op == "+")
            emitCode("add temp_right");
        else if (op == "-")
            emitCode("sub temp_right");
        else if (op == "*")
            emitCode("mul temp_right");
        else if (op == "/")
            emitCode("div temp_right");
        else if (op == "%")
            emitCode("rem temp_right");
        
        else if (op == "&&")
            emitCode("mul temp_right");
        else if (op == "||") {
            emitCode("add temp_right");

            std::string falseLabel = getNewLabel();
            std::string endLabel = getNewLabel();

            emitCode("jz " + falseLabel);
            emitCode("ldi 1");
            emitCode("jmp " + endLabel);
            emitCodeLabel(falseLabel);
            emitCode("ldi 0");
            emitCodeLabel(endLabel);
        }
        else if (op == ">" || op == "<" || op == ">=" || op == "<=" || op == "==" || op == "!=") {
            emitCode("sub temp_right");

            std::string trueLabel = getNewLabel();
            std::string endLabel = getNewLabel();

            if (op == "==")
                emitCode("jz " + trueLabel);
            else if (op == "!=")
                emitCode("jnz " + trueLabel);
            else if (op == ">")
                emitCode("jg " + trueLabel);
            else if (op == ">=")
                emitCode("jge " + trueLabel);
            else if (op == "<")
                emitCode("jl " + trueLabel);
            else if (op == "<=")
                emitCode("jle " + trueLabel);

            emitCode("ldi 0");
            emitCode("jmp " + endLabel);

            emitCodeLabel(trueLabel);
            emitCode("ldi 1");

            emitCodeLabel(endLabel);
        }
    }
}

void CodeGenerator::visit(UnaryOpNode& node) {
    const std::string& op = node.op;

    if (op == "!" && !currentTrueLabel.empty() && !currentFalseLabel.empty()) {
        visitWithLabels(node.operand, currentFalseLabel, currentTrueLabel);
        return;
    }

    node.operand->accept(*this);

    if (op == "-") {
        emitCode("st temp_right");
        emitCode("ldi 0");
        emitCode("sub temp_right");
    } else if (op == "!") {
        std::string trueLabel = getNewLabel();
        std::string endLabel = getNewLabel();

        emitCode("jz " + trueLabel);

        emitCode("ldi 0");
        emitCode("jmp " + endLabel);

        emitCodeLabel(trueLabel);
        emitCode("ldi 1");

        emitCodeLabel(endLabel);
    }

    // node.operand->accept(*this);

    // if (node.op == "-") {
    //     emitCode("not");
    //     emitCode("inc");
    // } else if (node.op == "!") {
    //     std::string trueLabel = getNewLabel();
    //     std::string endLabel = getNewLabel();
        
    //     emitCode("jz " + trueLabel);
    //     emitCode("ldi 0");
    //     emitCode("jmp " + endLabel);
    //     emitCodeLabel(trueLabel);
    //     emitCode("ldi 1");
    //     emitCodeLabel(endLabel);
    // }
}

void CodeGenerator::visit(IfNode& node) {
    std::string thenLabel = getNewLabel();
    std::string elseLabel = node.elseBranch ? getNewLabel() : "";
    std::string endLabel = getNewLabel();

    this->currentTrueLabel = thenLabel;
    this->currentFalseLabel = node.elseBranch ? elseLabel : endLabel;

    node.condition->accept(*this);

    auto* condNode = node.condition;
    if (condNode->nodeType != ASTNodeType::BinaryOp && condNode->nodeType != ASTNodeType::UnaryOp) {
        emitCode("jnz " + this->currentTrueLabel);
        emitCode("jmp " + this->currentFalseLabel);
    }

    this->currentTrueLabel = "";
    this->currentFalseLabel = "";

    emitCodeLabel(thenLabel);
    node.thenBranch->accept(*this);
    if (node.elseBranch) {
        emitCode("jmp " + endLabel);
        emitCodeLabel(elseLabel);
        node.elseBranch->accept(*this);
    }

    emitCodeLabel(endLabel);

    // this->currentTrueLabel = "";
    // this->currentFalseLabel = "";
    

    // std::string elseLabel = getNewLabel();
    // std::string endLabel = getNewLabel();

    // node.condition->accept(*this);
    // // emitCode("jz " + (node.elseBranch ? elseLabel : endLabel));
    

    // node.thenBranch->accept(*this);
    // emitCode("jmp " + endLabel);

    // if (node.elseBranch) {
    //     emitCodeLabel(elseLabel);
    //     node.elseBranch->accept(*this);
    // }

    // emitCodeLabel(endLabel);
}

void CodeGenerator::visit(WhileNode& node) {
    std::string startLabel = getNewLabel();
    std::string bodyLabel = getNewLabel();
    std::string endLabel = getNewLabel();

    breakLabels.push_back(endLabel);

    emitCodeLabel(startLabel);

    this->currentTrueLabel = bodyLabel;
    this->currentFalseLabel = endLabel;

    node.condition->accept(*this);

    this->currentTrueLabel = "";
    this->currentFalseLabel = "";

    emitCodeLabel(bodyLabel);
    node.body->accept(*this);

    emitCode("jmp " + startLabel);
    emitCodeLabel(endLabel);

    breakLabels.pop_back();
}

void CodeGenerator::visit(BreakNode& node) {
    emitCode("jmp " + breakLabels.back());
}

void CodeGenerator::visit(BlockNode& node) {
    std::cout << "visited block node\n";
    for (const auto& child : node.children)
        child->accept(*this);
}

void CodeGenerator::visit(ParameterNode& node) {
    throw std::logic_error("visit(ParameterNode&) should not be called in codeGen");
}

void CodeGenerator::visit(FunctionNode& node) {
    std::vector<std::string> paramTypes;
    for (const auto& param : node.parameters)
        paramTypes.push_back(static_cast<ParameterNode*>(param)->type);

    std::string mangledLabel = mangleFunctionName(node.name, paramTypes);

    FunctionData funcData;
    funcData.name = node.name;
    funcData.label = mangledLabel;
    // funcData.params //TODO: ???
    funcData.returnType = node.returnType;

    for (const auto& paramRaw : node.parameters) {
        auto paramNode = static_cast<ParameterNode*>(paramRaw);
        funcData.params.push_back({paramNode->type, paramNode->name});

        std::string argLabel = "arg_" + mangledLabel + "_" + paramNode->name;
        emitData(argLabel + ": 0");
        variables[argLabel] = paramNode->type;
    }

    functions[node.name].push_back(funcData);

    auto previousFunction = currentFunction;
    currentFunction = std::make_shared<FunctionData>(funcData);

    emitCodeLabel(mangledLabel);

    emitCode("pop");
    emitCode("st temp_ret_addr");

    for (int i = currentFunction->params.size() - 1; i >= 0; i--) {
        const auto& param = currentFunction->params[i];
        std::string argLabel = "arg_" + currentFunction->label + "_" + param.second;
        emitCode("pop");
        emitCode("st " + argLabel);
    }

    node.body->accept(*this);

    emitCode("");

    // emitCode("ret");
    //TODO: return is a must
    // emitCode("ld temp_ret_addr");
    // emitCode("push");
    // emitCode("ret");
    currentFunction = previousFunction;
    
    // currentFunction = std::make_unique<FunctionData>();
    // currentFunction->name = node.name;


    // std::string funcLabel = "func_" + node.name;
    // emitCodeLabel(funcLabel);

    // node.body->accept(*this);

    // if (node.returnType == "void")
    //     emitCode("ret");

    // currentFunction.reset();
}

void CodeGenerator::visit(FunctionCallNode& node) {
    if (reservedFunctions.count(node.name))
        processReservedFunctionCall(node);
    else
        processRegularFunctionCall(node);
}

void CodeGenerator::visit(ReturnNode& node) {
    // emitCode("ld temp_ret_addr");
    // emitCode("push");
    
    // if (node.returnValue)
    //     node.returnValue->accept(*this);

    // emitCode("ret");

    if (node.returnValue)
        node.returnValue->accept(*this);

    emitCode("st temp_right");
    emitCode("ld temp_ret_addr");
    emitCode("push");
    emitCode("ld temp_right");
    emitCode("ret");
}

void CodeGenerator::visitWithLabels(ASTNode* node, const std::string& trueL, const std::string& falseL) {
    std::string oldTrue = currentTrueLabel;
    std::string oldFalse = currentFalseLabel;

    currentTrueLabel = trueL;
    currentFalseLabel = falseL;

    node->accept(*this);

    currentTrueLabel = oldTrue;
    currentFalseLabel = oldFalse;
}

void CodeGenerator::processReservedFunctionCall(FunctionCallNode& node) {
    std::vector<std::string> argTypes;
    for (const auto& argExpr : node.parameters)
        argTypes.push_back(static_cast<ExpressionNode*>(argExpr)->resolvedType);
    std::string expectedReturnType = evalType(&node);

    const FunctionSignature* signature = findReservedFunction(node.name, argTypes, expectedReturnType);
    if (!signature)
        throw std::logic_error("reserved function signature mismatch");

    if (node.name == "in") {
        if (signature->paramTypes.empty())
            emitCode("ldi 0");
        else
            node.parameters[0]->accept(*this);
        emitCode("st input_count");

        const std::string returnType = node.resolvedType;

        if (returnType == "int") {
            emitCode("call read_int");
        } else if (returnType == "char") {
            emitCode("call read_char");
        } else if (returnType == "string") {
            emitCode("call read_string");
        } else if (returnType == "int[]") {
            emitCode("call read_arr");
        }
        
    } else if (node.name == "out") {
        auto arg = node.parameters[0];
        const std::string& typeToPrint = static_cast<ExpressionNode*>(arg)->resolvedType;
        arg->accept(*this);

        if (typeToPrint == "int") {
            emitCode("call write_int");
        } else if (typeToPrint == "char") {
            emitCode("call write_char");
        } else if (typeToPrint == "string") {
            emitCode("call write_string");
        } else if (typeToPrint == "int[]") {
            // emitCode("st output_arr");
            // emitCode("pop");
            // emitCode("st output_arr_size");
            emitCode("call write_arr");
        }
    }
    
}

void CodeGenerator::processRegularFunctionCall(FunctionCallNode& node) {
    std::vector<std::string> argTypes;
    for (const auto& argExpr : node.parameters) {
        argTypes.push_back(static_cast<ExpressionNode*>(argExpr)->resolvedType);
    }
    std::string mangledLabelToCall = mangleFunctionName(node.name, argTypes);

    if (currentFunction) {
        emitCode("ld temp_ret_addr");
        emitCode("push");

        for (const auto& param : currentFunction->params) {
            std::string argLabel = "arg_" + currentFunction->label + "_" + param.second;
            emitCode("ld " + argLabel);
            emitCode("push");
        }

        //same for local vars
    }

    for (const auto& argExpr : node.parameters) {
        argExpr->accept(*this);
        emitCode("push");
    }

    emitCode("call " + mangledLabelToCall);

    if (currentFunction) {
        emitCode("st temp_right");

        for (int i = currentFunction->params.size() - 1; i >= 0; i--) {
            const auto& param = currentFunction->params[i];
            std::string argLabel = "arg_" + currentFunction->label + "_" + param.second;
            emitCode("pop");
            emitCode("st " + argLabel);
        }

        emitCode("pop");
        emitCode("st temp_ret_addr");

        emitCode("ld temp_right");
    }
}

std::string CodeGenerator::evalType(ASTNode* node) {
    switch (node->nodeType) {
        case ASTNodeType::NumberLiteral:
            return "int";
        case ASTNodeType::CharLiteral:
            return "char";
        case ASTNodeType::StringLiteral:
            return "string";
        case ASTNodeType::BooleanLiteral:
            return "bool";
        case ASTNodeType::VoidLiteral:
            return "void";
        case ASTNodeType::IntArrayLiteral:
            return "int[]";
        case ASTNodeType::ArrayGet:
            return "int";
        case ASTNodeType::Identifier: {
            IdentifierNode* identifierNode = static_cast<IdentifierNode*>(node);
            std::cout << "identifier\n";
            std::cout << "variables\n";
            for (auto& v: variables) {
                std::cout << v.first << " " << v.second << "\n";
            }
            return variables[getVarLabel(identifierNode->name)];
        }
        case ASTNodeType::BinaryOp: {
            BinaryOpNode* binaryOpNode = static_cast<BinaryOpNode*>(node);
            std::string op = binaryOpNode->op;
            if (op == "+" || op == "-" || op == "*" || op == "/" || op == "%")
                return "int";
            else if (op == "==" || op == "!=" || op == ">" || op == ">=" ||
                     op == "<" || op == "<=" || op == "&&" || op == "||")
                return "bool";
            else
                throw std::runtime_error("Unknown op " + op);
        }
        case ASTNodeType::UnaryOp: {
            UnaryOpNode* unaryOpNode = static_cast<UnaryOpNode*>(node);
            std::string op = unaryOpNode->op;
            if (op == "!")
                return "bool";
            else if (op == "-")
                return "int";
            else
                throw std::runtime_error("Unknown op " + op);
        }
        case ASTNodeType::FunctionCall: {
            FunctionCallNode* functionCallNode = static_cast<FunctionCallNode*>(node);

            std::vector<std::string> paramTypes;
            for (auto* param : functionCallNode->parameters)
                paramTypes.push_back(evalType(param));

            if (reservedFunctions.count(functionCallNode->name)) {
                const FunctionSignature* funcSig = findReservedFunction(functionCallNode->name, paramTypes, "");
                return funcSig->returnType;
            } else {
                FunctionData* funcData = findFunction(functionCallNode->name, paramTypes);
                return funcData->returnType;
            }
        }
        case ASTNodeType::MethodCall: {
            MethodCallNode* methodCallNode = static_cast<MethodCallNode*>(node);

            return methodCallNode->resolvedType;
        }
        default:
            throw std::runtime_error("Node is not expression " + nodeStr(node));
    }
}

CodeGenerator::FunctionData* CodeGenerator::findFunction(const std::string& name, std::vector<std::string> paramTypes) {
    for (auto& funcData : functions[name]) {        
        bool match = true;
        
        if (funcData.params.size() != paramTypes.size()) continue;
        for (size_t i = 0; i < paramTypes.size(); i++) {
            if (funcData.params[i].first != paramTypes[i]) {
                match = false;
                break;
            }
        }

        if (match) return &funcData;
    }

    return nullptr;
}

const CodeGenerator::FunctionSignature* CodeGenerator::findReservedFunction(const std::string& name, const std::vector<std::string>& paramTypes, const std::string& expectedReturnType) {
    if (reservedFunctions.find(name) == reservedFunctions.end())
        return nullptr;
    
    for (const auto& sig : reservedFunctions.at(name)) {
        if (sig.paramTypes != paramTypes)
            continue;

        if (expectedReturnType.empty() || sig.returnType == expectedReturnType)
            return &sig;
    }

    return nullptr;
}

std::string CodeGenerator::getNewLabel() {
    return "L" + std::to_string(labelCounter++);
}

std::string CodeGenerator::mangleFunctionName(const std::string& name, const std::vector<std::string>& paramTypes) {
    std::string mangledName = "func_" + name;
    for (const auto& type : paramTypes) {
        mangledName += "_";
        if (type == "int") mangledName += "i";
        else if (type == "string") mangledName += "s";
        else if (type == "bool") mangledName += "b";
        else if (type == "int[]") mangledName += "ai";
    }
    return mangledName;
}

std::string CodeGenerator::nodeStr(ASTNode* node) {
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
        case ASTNodeType::MethodCall:
            return "MethodCall";
        default:
            return "Unknown";
    }
}

void CodeGenerator::emitCode(const std::string& line) {
    if (currentFunction)
        funcSection.push_back("  " + line);
    else
        codeSection.push_back("  " + line);
}

void CodeGenerator::emitCodeLabel(const std::string& label) {
    if (currentFunction)
        funcSection.push_back(label + ":");
    else
        codeSection.push_back(label + ":");
}

void CodeGenerator::emitData(const std::string& line) {
    dataSection.push_back("  " + line);
}

// void CodeGenerator::processRoot(ASTNode* node) {
//     BlockNode* root = static_cast<BlockNode*>(node);

//     emitCodeLabel("_start");

//     for (auto* child : root->children) {
//         processNode(child);
//     }

//     emitCode("halt");
// }

// void CodeGenerator::processFunction(ASTNode* node) {
//     FunctionNode* functionNode = static_cast<FunctionNode*>(node);
    
//     std::string funcName = functionNode->name;

//     currentFunction = std::make_unique<FunctionData>();
//     currentFunction->name = funcName;
//     currentFunction->label = funcName + "_" + std::to_string(functionLabels[funcName]++);
//     currentFunction->returnType = functionNode->returnType;

//     std::string funcLabel = "func_" + currentFunction->label;
//     emitCodeLabel(funcLabel);

//     for (auto param : functionNode->parameters) {
//         ParameterNode* paramNode = static_cast<ParameterNode*>(param);
//         currentFunction->params.push_back({paramNode->type, paramNode->name});
//         emitData("arg_" + currentFunction->label + "_" + paramNode->name + ": 0");
//         variables["arg_" + currentFunction->label + "_" + paramNode->name] = paramNode->type;
//     }
//     functions[funcName].push_back(*currentFunction);

//     processNode(functionNode->body);
//     emitCode("");

//     currentFunction.reset();
// }

// void CodeGenerator::processVarDecl(ASTNode* node) {
//     VarDeclNode* varDeclNode = static_cast<VarDeclNode*>(node);
    
//     std::string varName = varDeclNode->name;
//     std::string varLabel = getVarLabel(varName);

//     emitData(varLabel + ": 0");
//     variables[varLabel] = varDeclNode->type;

//     processNode(varDeclNode->value);

//     if (varDeclNode->type == "int[]") {
//         emitData(varLabel + "_size: 0");
//         if (varDeclNode->value->nodeType == ASTNodeType::FunctionCall) {
//             emitCode("st " + varLabel);
//             emitCode("ldi token_buffer");
//             emitCode("add token_buffer_i");
//             emitCode("sub token_i");
//             emitCode("dec");
//             emitCode("st " + varLabel + "_size");
//         } else {
//             emitCode("st " + varLabel);
//             emitCode("pop");
//             emitCode("st " + varLabel + "_size");
//         }
//         emitCode("");
//     } else if (varDeclNode->type == "string") {
//         emitCode("st " + varLabel);
//     } else if (varDeclNode->type == "int") {
//         if (varDeclNode->value->nodeType == ASTNodeType::FunctionCall) {
//             emitCode("st temp_right");
//             emitCode("lda temp_right");
//             emitCode("st " + varLabel);
//         } else {
//             emitCode("st " + varLabel);
//         }
//     } else {
//         emitCode("st " + varLabel);
//     }
// }

// void CodeGenerator::processAssignment(ASTNode* node) {
//     AssignNode* assignNode = static_cast<AssignNode*>(node);

//     IdentifierNode* var1Node = static_cast<IdentifierNode*>(assignNode->var1);
//     std::string var1Label = getVarLabel(var1Node->name);

//     processNode(assignNode->var2);

//     if (evalType(assignNode->var1) == "string") {
//         if (assignNode->var2->nodeType == ASTNodeType::StringLiteral) {
//             emitCode("ld str_" + std::to_string(strCounter - 1));
//             emitCode("st " + var1Label);
//         } else {
//             emitCode("st " + var1Label);
//         }
//     } else if (assignNode->var1->nodeType == ASTNodeType::ArrayGet) {
//         ArrayGetNode* leftVar = static_cast<ArrayGetNode*>(assignNode->var1);
//         emitCode("push");

//         processNode(leftVar->index);

//         emitCode("st temp_right");
//         // emitCode("ld " + getVarLabel(leftVar->name));
//         emitCode("add temp_right");
//         emitCode("st temp_right");
//         emitCode("pop");
//         emitCode("sta temp_right");
//     } else {
//         if (variables.find(var1Label) != variables.end())
//             emitCode("st " + var1Label);
//         else
//             throw std::runtime_error("Could not find variable " + var1Label);
//     }
// }

// void CodeGenerator::processBinaryOp(ASTNode* node) {
//     BinaryOpNode* binaryOpNode = static_cast<BinaryOpNode*>(node);

//     std::string op = binaryOpNode->op;
//     ASTNode* left = binaryOpNode->left;
//     ASTNode* right = binaryOpNode->right;

//     processNode(left);

//     pushToStack();

//     processNode(right);

//     emitCode("st temp_right");

//     popFromStack();

//     if (op == "+")
//         emitCode("add temp_right");
//     else if (op == "*")
//         emitCode("mul temp_right");
//     else if (op == "/")
//         emitCode("div temp_right");
//     else if (op == "%")
//         emitCode("rem temp_right");
//     else if (op == "-" || op == ">" || op == ">=" || op == "<" ||
//                 op == "<=" || op == "==" || op == "!=")
//         emitCode("sub temp_right");
// }

void CodeGenerator::pushToStack() {
    emitCode("push");
}

void CodeGenerator::popFromStack() {
    emitCode("pop");
}

// void CodeGenerator::processUnaryOp(ASTNode* node) {
//     UnaryOpNode* unaryOpNode = static_cast<UnaryOpNode*>(node);

//     std::string op = unaryOpNode->op;
    
//     processNode(unaryOpNode->operand);

//     if (op == "!") {
//         emitCode("st temp_right");
//         emitCode("ldi 1");
//         emitCode("sub temp_right");
//     } else if (op == "-") {
//         emitCode("not");
//         emitCode("inc");
//     } else {
//         throw std::runtime_error("Unknown unary op " + op);
//     }
// }

// void CodeGenerator::processIf(ASTNode* node) {
//     IfNode* ifNode = static_cast<IfNode*>(node);

//     ASTNode* condition = ifNode->condition;
//     ASTNode* thenBlock = ifNode->thenBranch;
//     ASTNode* elseBlock = ifNode->elseBranch;

//     std::string elseLabel;
//     if (elseBlock) elseLabel = getNewLabel();
//     std::string endLabel = getNewLabel();

//     processNode(condition);
//     std::string notCondJmp = getNotConditionJump(condition);

//     if (elseBlock) emitCode(notCondJmp + " " + elseLabel);
//     else emitCode(notCondJmp + " " + endLabel);

//     processNode(thenBlock);
//     emitCode("jmp " + endLabel);

//     if (elseBlock) {
//         emitCodeLabel(elseLabel);
//         processNode(elseBlock);
//     }

//     emitCodeLabel(endLabel);
// }

std::string CodeGenerator::getNotConditionJump(ASTNode* node) {
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
            return "jz";
        } else {
            throw std::runtime_error("Unary condition must be logical op");
        }
    } else if (node->nodeType == ASTNodeType::Identifier) {
        return "jnz";
    } else
        throw std::runtime_error("Condition must be binary op");
}

// void CodeGenerator::processWhile(ASTNode* node) {
//     WhileNode* whileNode = static_cast<WhileNode*>(node);

//     ASTNode* condition = whileNode->condition;
//     ASTNode* body = whileNode->body;

//     std::string startLabel = getNewLabel();
//     std::string endLabel = getNewLabel();

//     emitCodeLabel(startLabel);

//     processNode(condition);
//     std::string notCondJmp = getNotConditionJump(condition);

//     emitCode(notCondJmp + " " + endLabel);

//     processNode(body);

//     emitCode("jmp " + startLabel);
//     emitCodeLabel(endLabel);
// }

// void CodeGenerator::processBlock(ASTNode* node) {
//     BlockNode* blockNode = static_cast<BlockNode*>(node);

//     for (auto child : blockNode->children) {
//         processNode(child);
//     }
// }

// void CodeGenerator::processReservedFunction(ASTNode* node) {
//     FunctionCallNode* functionCallNode = static_cast<FunctionCallNode*>(node);

//     std::string funcName = functionCallNode->name;
    
//     std::vector<std::string> paramTypes;
//     for (auto& param: functionCallNode->parameters) {
//         paramTypes.push_back(evalType(param));
//         std::cout << "paramtype: " << evalType(param) << "\n";
//     }

//     if(!std::any_of(reservedFunctions.at(funcName).begin(),
//                     reservedFunctions.at(funcName).end(),
//                     [&](const FunctionSignature& sig) {
//                         return sig.paramTypes == paramTypes;
//                     })) {
//         throw std::runtime_error("Argument mismatch in reserved function " + funcName);
//     }

//     if (funcName == "in") {
//         std::string bufferLabel = "buffer" + std::to_string(bufferCounter);
//         std::string bufferCountLabel = "buffer" + std::to_string(bufferCounter++);

//         if (paramTypes.size() == 0)
//             emitCode("ldi 0");
//         else if (paramTypes.size() == 1)
//             processNode(functionCallNode->parameters[0]);

//         emitCode("st token_count");
//         emitCode("call read_string"); 
//     } else if (funcName == "out") {
//         processNode(functionCallNode->parameters[0]);
        
//         if (paramTypes[0] == "int") {
//             emitCode("st output_number");
//             emitCode("call write_number");
//         } else if (paramTypes[0] == "int[]") {
//             emitCode("st output_arr");
//             emitCode("pop");
//             emitCode("st output_arr_size");
//             emitCode("call write_arr");
//         } else if (paramTypes[0] == "string") {
//             emitCode("st output_string");
//             emitCode("call write_string");
//         }
//     } else {
//         throw std::runtime_error("Unimplemented reserved function " + funcName + " behavior");
//     }
// }

// void CodeGenerator::processFunctionCall(ASTNode* node) {
//     FunctionCallNode* functionCallNode = static_cast<FunctionCallNode*>(node);

//     std::string funcName = functionCallNode->name;
    
//     if (reservedFunctions.find(funcName) != reservedFunctions.end()) {
//         processReservedFunction(node);
//         return;
//     }
    
//     std::vector<std::string> paramTypes;
//     for (auto* param : functionCallNode->parameters)
//         paramTypes.push_back(evalType(param));
//     FunctionData* funcData = findFunction(functionCallNode->name, paramTypes);

//     for (size_t i = 0; i < functionCallNode->parameters.size(); i++) {
//         processNode(functionCallNode->parameters[i]);
//         emitCode("st arg_" + funcData->label + "_" + funcData->params[i].second);
//     }

//     emitCode("call func_" + funcData->label);
// }

// void CodeGenerator::processReturn(ASTNode* node) {
//     ReturnNode* returnNode = static_cast<ReturnNode*>(node);

//     ASTNode* returnValue = returnNode->returnValue;
//     if (returnValue)
//         processNode(returnValue);

//     emitCode("ret");
// }

std::string CodeGenerator::getVarLabel(const std::string& varName) {
    if (currentFunction) {
        auto& funcData = functions[currentFunction->name];
        bool isArg = std::any_of(
            currentFunction->params.begin(),
            currentFunction->params.end(),
            [&varName](const auto& param) { return param.second == varName; });

        if (isArg) {
            return "arg_" + currentFunction->label + "_" + varName;
        } else {
            if (variables.find("var_" + varName) != variables.end()) {
                return "var_" + varName;
            } else {
                return "var_" + currentFunction->label + "_" + varName;
            }
        }
    } else {
        return "var_" + varName;
    }
}

// void CodeGenerator::processIdentifier(ASTNode* node) {
//     IdentifierNode* identifierNode = static_cast<IdentifierNode*>(node);

//     std::string varLabel = getVarLabel(identifierNode->name);

//     if (variables[varLabel] == "int[]") {
//         emitCode("ld " + varLabel + "_size");
//         emitCode("push");
//         emitCode("ld " + varLabel);
//     } else {
//         emitCode("ld " + varLabel);
//     }
// }

// void CodeGenerator::processNumberLiteral(ASTNode* node) {
    
// }

// void CodeGenerator::processBooleanLiteral(ASTNode* node) {
//     BooleanLiteralNode* booleanLiteralNode = static_cast<BooleanLiteralNode*>(node);

//     bool value = booleanLiteralNode->value;
//     emitCode("ldi " + std::to_string(value ? 1 : 0));
// }

// void CodeGenerator::processStringLiteral(ASTNode* node) {
//     StringLiteralNode* stringLiteralNode = static_cast<StringLiteralNode*>(node);

//     std::string value = stringLiteralNode->value;
//     std::string strLabel = "str_" + std::to_string(strCounter++);

//     emitData(strLabel + ": \"" + value + "\\0\"");
//     emitData(strLabel + "_size: " + std::to_string(value.size() + 1));
//     emitCode("ldi " + strLabel);
// }

// void CodeGenerator::processVoidLiteral(ASTNode* node) {
//     return;
// }

// void CodeGenerator::processIntArrayLiteral(ASTNode* node) {
//     IntArrayLiteralNode* intArrayLiteralNode = static_cast<IntArrayLiteralNode*>(node);

//     std::string dataLabel = "arr_" + std::to_string(arrCounter++);
//     std::string data = dataLabel + ": ";

//     for (size_t i = 0; i < intArrayLiteralNode->values.size(); i++) {
//         auto* number = static_cast<NumberLiteralNode*>(intArrayLiteralNode->values[i]);
//         if (!number) throw std::runtime_error("Only int allowed in int[]");
//         data += std::to_string(number->number);
//         if (i != intArrayLiteralNode->values.size() - 1) data += ", ";
//     }

//     emitData(data);
//     emitData(dataLabel + "_size: " + std::to_string(intArrayLiteralNode->values.size()));
//     emitCode("ld " + dataLabel + "_size");
//     emitCode("push");
//     emitCode("ldi " + dataLabel);
// }

// void CodeGenerator::processArrayGet(ASTNode* node) {
//     ArrayGetNode* arrayGetNode = static_cast<ArrayGetNode*>(node);

//     // std::string arrayName = arrayGetNode->name;
//     processNode(arrayGetNode->index);

//     emitCode("st temp_right");
//     // emitCode("ld " + getVarLabel(arrayName));
//     emitCode("add temp_right");
//     emitCode("st temp_right");
//     emitCode("lda temp_right");
// }

std::string CodeGenerator::assembleCode() {
    std::stringstream result;

    result << data << "\n";

    for (const auto& line : dataSection) {
        result << line << "\n";
    }

    result << "\n.text\n";
    result << ".org 0x20\n";
    result << interrupts;
    result << read_char;
    result << read_int;
    result << write_to_buf;
    result << read_string;
    result << read_arr;
    result << write_char;
    result << write_int;
    result << write_string;
    result << write_arr;
    result << arr_size;
    
    for (const auto& line : funcSection) {
        result << line << "\n";
    }

    for (const auto& line : codeSection) {
        result << line << "\n";
    }

    return result.str();
}