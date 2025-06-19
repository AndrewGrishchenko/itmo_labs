// #include "codeGenerator.h"

// CodeGenerator::CodeGenerator() { }

// CodeGenerator::~CodeGenerator() { }

// std::string CodeGenerator::makeIntLabel(const std::string& name) {
//     return name + "__" + std::to_string(scopeCounter);
// }

// std::string CodeGenerator::makeStringLabel(const std::string& name) {
//     return name + "__" + std::to_string(scopeCounter);
// }

// void CodeGenerator::emit(const std::string& line) {
//     code.push_back(line);
// }

// void CodeGenerator::emitData(const std::string& line) {
//     dataSection.push_back(line);
// }

// void CodeGenerator::declareVariable(const std::string& name) {
//     if (!declaredVariables[name]) {
//         emitData(name + ": .word 0");
//         declaredVariables[name] = true;
//     }
// }

// void CodeGenerator::push_ac() {
//     emit("st sp");
//     emit("dec sp");
// }

// void CodeGenerator::pop_ac() {
//     emit("inc sp");
//     emit("ld sp");
// }

// void CodeGenerator::generate(ASTNode* node) {
//     if (!node) return;
//     switch(node->nodeType) {
//         case ASTNodeType::Root:
//             generateRoot(node);
//             break;
//         case ASTNodeType::VarDecl:
//             generateVarDecl(node);
//             break;
//         case ASTNodeType::NumberLiteral:
//             generateNumberLiteral(node);
//             break;
//         case ASTNodeType::BooleanLiteral:
//             generateBooleanLiteral(node);
//             break;
//         case ASTNodeType::StringLiteral:
//             generateStringLiteral(node);
//             break;
//         case ASTNodeType::Identifier:
//             generateIdentifier(node);
//             break;
//         case ASTNodeType::Assignment:
//             generateAssignment(node);
//             break;
//         case ASTNodeType::BinaryOp:
//             generateBinaryOp(node);
//             break;
//         case ASTNodeType::Block:
//             generateBlock(node);
//             break;
//         case ASTNodeType::If:
//             generateIf(node);
//             break;
//         case ASTNodeType::While:
//             generateWhile(node);
//             break;
//         case ASTNodeType::FunctionCall:
//             generateFunctioncall(node);
//             break;
//         case ASTNodeType::Return:
//             generateReturn(node);
//             break;
//         case ASTNodeType::Function:
//             generateFunction(node);
//             break;
//         default:
//             throw std::runtime_error("unknown");
//     }
// }

// void CodeGenerator::generateRoot(ASTNode* node) {
//     RootNode* root = static_cast<RootNode*>(node);

//     for (auto* child : root->children) {
//         generate(child);
//     }
// }

// void CodeGenerator::generateVarDecl(ASTNode* node) {
//     VarDeclNode* vdn = static_cast<VarDeclNode*>(node);

//     emitData(analyzeExpression(vdn->value));
// }

// std::string CodeGenerator::analyzeExpression(ASTNode* node) {
//     switch(node->nodeType) {
//         case ASTNodeType::NumberLiteral: {
//             NumberLiteralNode* nln = static_cast<NumberLiteralNode*>(node);
//             return std::to_string(nln->number);
//         }
//         case ASTNodeType::StringLiteral: {
//             StringLiteralNode* sln = static_cast<StringLiteralNode*>(node);
//             return sln->value + "\0";
//         }
//         default:
//             throw std::runtime_error("aaa");
//     }
// }

// void CodeGenerator::generateNumberLiteral(ASTNode* node) {}
// void CodeGenerator::generateBooleanLiteral(ASTNode* node) {}
// void CodeGenerator::generateStringLiteral(ASTNode* node) {}

// void CodeGenerator::generateIdentifier(ASTNode* node) {}
// void CodeGenerator::generateAssignment(ASTNode* node) {}

// void CodeGenerator::generateBinaryOp(ASTNode* node) {}
// void CodeGenerator::generateUnaryOp(ASTNode* node)  {}

// void CodeGenerator::generateBlock(ASTNode* node) {}
// void CodeGenerator::generateIf(ASTNode* node) {}
// void CodeGenerator::generateWhile(ASTNode* node) {}
// void CodeGenerator::generateFunctioncall(ASTNode* node)  {}
// void CodeGenerator::generateReturn(ASTNode* node) {}
// void CodeGenerator::generateFunction(ASTNode* node) {}