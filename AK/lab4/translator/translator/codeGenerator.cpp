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
    stackOffset = 0;

    processRoot(root);

    return assembleCode();
}

std::string CodeGenerator::evalType(ASTNode* node) {
    switch (node->nodeType) {
        case ASTNodeType::NumberLiteral:
            return "int";
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
                throw std::runtime_error("Unknown op " + op); //TODO: remove throw in codeGen
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

            if (reservedFunctions.find(functionCallNode->name) != reservedFunctions.end()) {
                const FunctionSignature* funcSig = findReservedFunction(functionCallNode->name, paramTypes);
                return funcSig->returnType;
            } else {
                FunctionData* funcData = findFunction(functionCallNode->name, paramTypes);
                return funcData->returnType;
            }
        }
        default:
            throw std::runtime_error("Node is not expression");
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

const CodeGenerator::FunctionSignature* CodeGenerator::findReservedFunction(const std::string& name, std::vector<std::string> paramTypes) {
    if (reservedFunctions.find(name) == reservedFunctions.end())
        return nullptr;
    
    for (const auto& sig : reservedFunctions.at(name)) {
        bool match = true;

        if (sig.paramTypes.size() != paramTypes.size()) continue;
        for (size_t i = 0; i < paramTypes.size(); i++) {
            if (sig.paramTypes[i] != paramTypes[i]) {
                match = false;
                break;
            }
        }

        if (match) return &sig;
    }

    return nullptr;
}

std::string CodeGenerator::getNewLabel() {
    return "L" + std::to_string(labelCounter++);
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
        default:
            return "Unknown";
    }
}

void CodeGenerator::processNode(ASTNode* node) {
    if (!node) return;

    switch(node->nodeType) {
        case ASTNodeType::Function:
            processFunction(node);
            break;
        case ASTNodeType::VarDecl:
            processVarDecl(node);
            break;
        case ASTNodeType::Assignment:
            processAssignment(node);
            break;
        case ASTNodeType::If:
            processIf(node);
            break;
        case ASTNodeType::While:
            processWhile(node);
            break;
        case ASTNodeType::Block:
            processBlock(node);
            break;
        case ASTNodeType::Return:
            processReturn(node);
            break;
        case ASTNodeType::BinaryOp:
            processBinaryOp(node);
            break;
        case ASTNodeType::UnaryOp:
            processUnaryOp(node);
            break;
        case ASTNodeType::FunctionCall:
            processFunctionCall(node);
            break;
        case ASTNodeType::Identifier:
            processIdentifier(node);
            break;
        case ASTNodeType::NumberLiteral:
            processNumberLiteral(node);
            break;
        case ASTNodeType::BooleanLiteral:
            processBooleanLiteral(node);
            break;
        case ASTNodeType::StringLiteral:
            processStringLiteral(node);
            break;
        case ASTNodeType::VoidLiteral:
            processVoidLiteral(node);
            break;
        case ASTNodeType::IntArrayLiteral:
            processIntArrayLiteral(node);
            break;
        case ASTNodeType::ArrayGet:
            processArrayGet(node);
            break;
        default:
            throw std::runtime_error("Unknown node");
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

void CodeGenerator::processRoot(ASTNode* node) {
    BlockNode* root = static_cast<BlockNode*>(node);

    emitCodeLabel("_start");

    for (auto* child : root->children) {
        processNode(child);
    }

    emitCode("halt");
}

void CodeGenerator::processFunction(ASTNode* node) {
    FunctionNode* functionNode = static_cast<FunctionNode*>(node);
    
    std::string funcName = functionNode->name;

    currentFunction = std::make_unique<FunctionData>();
    currentFunction->name = funcName;
    currentFunction->label = funcName + "_" + std::to_string(functionLabels[funcName]++);
    currentFunction->returnType = functionNode->returnType;

    std::string funcLabel = "func_" + currentFunction->label;
    emitCodeLabel(funcLabel);

    for (auto param : functionNode->parameters) {
        ParameterNode* paramNode = static_cast<ParameterNode*>(param);
        currentFunction->params.push_back({paramNode->type, paramNode->name});
        emitData("arg_" + currentFunction->label + "_" + paramNode->name + ": 0");
    }
    functions[funcName].push_back(*currentFunction);

    processNode(functionNode->body);
    emitCode("");

    currentFunction.reset();
}

void CodeGenerator::processVarDecl(ASTNode* node) {
    VarDeclNode* varDeclNode = static_cast<VarDeclNode*>(node);
    
    std::string varName = varDeclNode->name;
    std::string varLabel = getVarLabel(varName);

    emitData(varLabel + ": 0");
    variables[varLabel] = varDeclNode->type;

    processNode(varDeclNode->value);
    emitCode("st " + varLabel);

    // if (evalType(varDeclNode->value) == "int[]") {
    //     // emitData(varLabel + ": 0");
    //     // variables[varLabel] = varDeclNode->type;

    //     // processNode(varDeclNode->value);
    //     // emitCode("st " + varLabel);

    //     // std::string arrLabel = "arr_" + std::to_string(labelCounter++);

    //     // emitData(varLabel + ": arr_" + std::to_string(arrCounter - 1));
        
        
    //     emitCode("st " + varLabel);
    // } else if (evalType(varDeclNode->value) == "string") {
    //     // emitData(varLabel + ": str_" + std::to_string(strCounter - 1));
    //     emitCode("st " + varLabel);
    // } else {
    //     emitCode("st " + varLabel);
    // }


    // } else {
    //     if (evalType(varDeclNode->value)  == "string") {
    //         // emitCode("ld " + varLabel);
    //         // emitCode("st target_str");
    //         // emitCode("call str_copy");
    //         // emitCode("");
    //     } else {
    //         // emitCode("st " + varLabel);
    //         emitData(varLabel + ": ")
    //     } 
    // }
}

void CodeGenerator::processAssignment(ASTNode* node) {
    AssignNode* assignNode = static_cast<AssignNode*>(node);
    IdentifierNode* var1Node = static_cast<IdentifierNode*>(assignNode->var1);
    IdentifierNode* var2Node = static_cast<IdentifierNode*>(assignNode->var2);

    std::string var1Name = var1Node->name;
    std::string var2Name = var2Node->name;
    std::string var1Label = getVarLabel(var1Name);
    std::string var2Label = getVarLabel(var2Name);

    processNode(assignNode->var2);

    if (evalType(var1Node) == "string") {
        if (var2Node->nodeType == ASTNodeType::StringLiteral) {
            emitCode("ld str_" + std::to_string(strCounter - 1));
            emitCode("st " + getVarLabel(var1Name));
        } else {
            // emitCode("st source_str");
            // emitCode("ld " + var1Label);
            // emitCode("st target_str");
            // emitCode("call str_copy");
            // emitCode("");
            emitCode("st " + getVarLabel(var1Name));
        }
    } else if (evalType(var1Node) == "int[]") {
        emitCode("st " + var1Label);
    } else {
        if (variables.find(var1Label) != variables.end())
            emitCode("st " + var1Label);
        else
            throw std::runtime_error("Could not find variable " + var1Name);
    }
}

void CodeGenerator::processBinaryOp(ASTNode* node) {
    BinaryOpNode* binaryOpNode = static_cast<BinaryOpNode*>(node);

    std::string op = binaryOpNode->op;
    ASTNode* left = binaryOpNode->left;
    ASTNode* right = binaryOpNode->right;

    processNode(left);

    pushToStack();

    processNode(right);

    emitCode("st temp_right");

    popFromStack();

    if (op == "+")
        emitCode("add temp_right");
    else if (op == "*")
        emitCode("mul temp_right");
    else if (op == "/")
        emitCode("div temp_right");
    else if (op == "%")
        emitCode("rem temp_right");
    else if (op == "-" || op == ">" || op == ">=" || op == "<" ||
                op == "<=" || op == "==" || op == "!=")
        emitCode("sub temp_right");
}

void CodeGenerator::pushToStack() {
    emitCode("push");
}

void CodeGenerator::popFromStack() {
    emitCode("pop");
}

void CodeGenerator::processUnaryOp(ASTNode* node) {
    UnaryOpNode* unaryOpNode = static_cast<UnaryOpNode*>(node);

    std::string op = unaryOpNode->op;
    
    processNode(unaryOpNode->operand);

    if (op == "!") {
        emitCode("st temp_right");
        emitCode("ldi 1");
        emitCode("sub temp_right");
    } else if (op == "-") {
        emitCode("not");
        emitCode("inc");
    } else {
        throw std::runtime_error("Unknown unary op " + op);
    }
}

void CodeGenerator::processIf(ASTNode* node) {
    IfNode* ifNode = static_cast<IfNode*>(node);

    ASTNode* condition = ifNode->condition;
    ASTNode* thenBlock = ifNode->thenBranch;
    ASTNode* elseBlock = ifNode->elseBranch;

    std::string elseLabel;
    if (elseBlock) elseLabel = getNewLabel();
    std::string endLabel = getNewLabel();

    processNode(condition);
    std::string notCondJmp = getNotConditionJump(condition);

    if (elseBlock) emitCode(notCondJmp + " " + elseLabel);
    else emitCode(notCondJmp + " " + endLabel);

    processNode(thenBlock);
    emitCode("jmp " + endLabel);

    if (elseBlock) {
        emitCodeLabel(elseLabel);
        processNode(elseBlock);
    }

    emitCodeLabel(endLabel);
}

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

void CodeGenerator::processWhile(ASTNode* node) {
    WhileNode* whileNode = static_cast<WhileNode*>(node);

    ASTNode* condition = whileNode->condition;
    ASTNode* body = whileNode->body;

    std::string startLabel = getNewLabel();
    std::string endLabel = getNewLabel();

    emitCodeLabel(startLabel);

    processNode(condition);
    std::string notCondJmp = getNotConditionJump(condition);

    emitCode(notCondJmp + " " + endLabel);

    processNode(body);

    emitCode("jmp " + startLabel);
    emitCodeLabel(endLabel);
}

void CodeGenerator::processBlock(ASTNode* node) {
    BlockNode* blockNode = static_cast<BlockNode*>(node);

    for (auto child : blockNode->children) {
        processNode(child);
    }
}

void CodeGenerator::processReservedFunction(ASTNode* node) {
    FunctionCallNode* functionCallNode = static_cast<FunctionCallNode*>(node);

    std::string funcName = functionCallNode->name;
    
    std::vector<std::string> paramTypes;
    for (auto& param: functionCallNode->parameters) {
        paramTypes.push_back(evalType(param));
    }

    if(!std::any_of(reservedFunctions.at(funcName).begin(),
                    reservedFunctions.at(funcName).end(),
                    [&](const FunctionSignature& sig) {
                        return sig.paramTypes == paramTypes;
                    })) {   
        throw std::runtime_error("Argument mismatch in reserved function " + funcName);
    }

    if (funcName == "in") {
        //TODO: handling \0 in strings
        std::string bufferLabel = "buffer" + std::to_string(bufferCounter);
        std::string bufferCountLabel = "buffer" + std::to_string(bufferCounter++);

        // emitData(bufferLabel + ": 0");
        // emitData(bufferCountLabel + "_count: 0");


        if (paramTypes.size() == 0)
            emitCode("ldi 0");
        else if (paramTypes.size() == 1)
            processNode(functionCallNode->parameters[0]);

        emitCode("st token_count");
        emitCode("call read_string");

        // emitCode("st " + bufferCountLabel);
        // emitCode("st token_count");
        // emitCode("call read_string");
        // emitCode("sub " + bufferCountLabel);
        // emitCode("st " + bufferLabel);
        // emitCode("");
    } else if (funcName == "out") {
        processNode(functionCallNode->parameters[0]);
        emitCode("st output_string");
        emitCode("call write_string");
    } else {
        throw std::runtime_error("Unimplemented reserved function " + funcName + " behavior");
    }
}

void CodeGenerator::processFunctionCall(ASTNode* node) {
    FunctionCallNode* functionCallNode = static_cast<FunctionCallNode*>(node);

    std::string funcName = functionCallNode->name;
    
    if (reservedFunctions.find(funcName) != reservedFunctions.end()) {
        processReservedFunction(node);
        return;
    }
    
    std::vector<std::string> paramTypes;
    for (auto* param : functionCallNode->parameters)
        paramTypes.push_back(evalType(param));
    FunctionData* funcData = findFunction(functionCallNode->name, paramTypes);

    for (size_t i = 0; i < functionCallNode->parameters.size(); i++) {
        processNode(functionCallNode->parameters[i]);
        emitCode("st arg_" + funcData->label + "_" + funcData->params[i].second);
    }

    emitCode("call func_" + funcData->label);
}

void CodeGenerator::processReturn(ASTNode* node) {
    ReturnNode* returnNode = static_cast<ReturnNode*>(node);

    ASTNode* returnValue = returnNode->returnValue;
    if (returnValue)
        processNode(returnValue);

    emitCode("ret");
}

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
                //TODO: check wtf is this
            }
        }
    } else {
        return "var_" + varName;
    }
}

void CodeGenerator::processIdentifier(ASTNode* node) {
    IdentifierNode* identifierNode = static_cast<IdentifierNode*>(node);

    std::string varName = identifierNode->name;
    emitCode("ld " + getVarLabel(varName));
}

void CodeGenerator::processNumberLiteral(ASTNode* node) {
    NumberLiteralNode* numberLiteralNode = static_cast<NumberLiteralNode*>(node);

    int value = numberLiteralNode->number;
    emitCode("ldi " + std::to_string(value));
}

void CodeGenerator::processBooleanLiteral(ASTNode* node) {
    BooleanLiteralNode* booleanLiteralNode = static_cast<BooleanLiteralNode*>(node);

    bool value = booleanLiteralNode->value;
    emitCode("ldi " + std::to_string(value ? 1 : 0));
}

void CodeGenerator::processStringLiteral(ASTNode* node) {
    StringLiteralNode* stringLiteralNode = static_cast<StringLiteralNode*>(node);

    std::string value = stringLiteralNode->value;
    std::string strLabel = "str_" + std::to_string(strCounter++);

    emitData(strLabel + ": \"" + value + "\\0\"");
    emitData(strLabel + "_size: " + std::to_string(value.size() + 1));
    emitCode("ldi " + strLabel);

    // emitCode("");
    // emitCode("ld " + strLabel);
    // emitCode("st source_str");
}

void CodeGenerator::processVoidLiteral(ASTNode* node) {
    return;
}

void CodeGenerator::processIntArrayLiteral(ASTNode* node) {
    IntArrayLiteralNode* intArrayLiteralNode = static_cast<IntArrayLiteralNode*>(node);

    std::string dataLabel = "arr_" + std::to_string(arrCounter++);
    std::string data = dataLabel + ": ";

    // for (auto& val : intArrayLiteralNode->values) {
    //     auto* number = static_cast<NumberLiteralNode*>(val);
    //     if (!number) throw std::runtime_error("Only int allowed in int[]");
    //     emitData("  .word " + std::to_string(number->number));
        
    // }

    for (size_t i = 0; i < intArrayLiteralNode->values.size(); i++) {
        auto* number = static_cast<NumberLiteralNode*>(intArrayLiteralNode->values[i]);
        if (!number) throw std::runtime_error("Only int allowed in int[]");
        data += std::to_string(number->number);
        if (i != intArrayLiteralNode->values.size() - 1) data += ", ";
    }

    // emitData("  .word " + std::to_string(intArrayLiteralNode->values.size()));
    // emitCode("lda " + dataLabel);

    emitData(data);
    emitData(dataLabel + "_size: " + std::to_string(intArrayLiteralNode->values.size()));
    emitCode("ldi " + dataLabel);
    // emitCode("lda " + dataLabel);
}

void CodeGenerator::processArrayGet(ASTNode* node) {
    ArrayGetNode* arrayGetNode = static_cast<ArrayGetNode*>(node);

    std::string arrayName = arrayGetNode->name;
    size_t index = arrayGetNode->index;

    emitCode("ldi " + std::to_string(index));
    emitCode("st temp_right");
    emitCode("ld " + getVarLabel(arrayName));
    emitCode("add temp_right");
    emitCode("st temp_right");
    emitCode("lda temp_right");


    // std::string arrayLabel = getVarLabel(arrayName);

    // emitCode("ld " + arrayLabel);

    // if (index > 0) {
    //     std::string tempIndexLabel = getNewLabel();
    //     emitData(tempIndexLabel + ": " + std::to_string(index));

    //     emitCode("add " + tempIndexLabel);
    // }
}

std::string CodeGenerator::assembleCode() {
    std::stringstream result;

    result << data << "\n";

    for (const auto& line : dataSection) {
        result << line << "\n";
    }

    result << "\n.text\n";
    result << ".org 0x20\n";
    result << interrupts;
    result << read_token;
    result << read_string;
    result << write_token;
    result << write_string;
    // result << buffer_read;
    // result << buffer_write;
    // result << str_copy;

    for (const auto& line : funcSection) {
        result << line << "\n";
    }

    for (const auto& line : codeSection) {
        result << line << "\n";
    }

    return result.str();
}