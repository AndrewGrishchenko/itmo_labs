#ifndef _CODE_GENERATOR_H
#define _CODE_GENERATOR_H

#include "ASTNode.h"
#include "semanticAnalyzer.h"

#include <unordered_map>
#include <vector>
#include <sstream>
#include <algorithm>
#include <memory>

class CodeGenerator {
    public:
        CodeGenerator();
        ~CodeGenerator();

        std::string generateCode(ASTNode* root);
    private:
        std::string evalType(ASTNode* node);

        struct FunctionSignature {
            std::string returnType;
            std::vector<std::string> paramTypes;

            bool operator==(const FunctionSignature& other) const {
                return returnType == other.returnType && 
                    paramTypes == other.paramTypes;
            }
        };

        struct FunctionData {
            std::string name;
            std::string label;
            std::string returnType;
            std::vector<std::pair<std::string, std::string>> params;

            bool operator==(const FunctionData& other) const {
                return returnType == other.returnType &&
                       params == other.params;
            }
        };

        FunctionData* findFunction(const std::string& name, std::vector<std::string> paramTypes);
        const FunctionSignature* findReservedFunction(const std::string& name, std::vector<std::string> paramTypes);

        std::vector<std::string> dataSection;
        std::vector<std::string> codeSection;
        std::vector<std::string> funcSection;
        std::unordered_map<std::string, std::string> variables;
        std::unordered_map<std::string, int> functionLabels;

        std::unordered_map<std::string, std::vector<FunctionData>> functions;
        
        const std::unordered_map<std::string, std::vector<FunctionSignature>> reservedFunctions = {
            {"in", {
                {"string", {}}
            }},
            {"out", {
                {"void", {"int"}},
                {"void", {"string"}}
            }}
        };

        int labelCounter = 0;
        int tempVarCounter = 0;
        int stackOffset = 0;

        std::shared_ptr<FunctionData> currentFunction;

        std::string getNewLabel();

        std::string nodeStr(ASTNode* node);

        void processNode(ASTNode* node);

        void emitCode(const std::string& line);
        void emitCodeLabel(const std::string& label);
        void emitData(const std::string& line);

        void processRoot(ASTNode* node);
        void processFunction(ASTNode* node);
        void processVarDecl(ASTNode* node);
        void processAssignment(ASTNode* node);
        void processBinaryOp(ASTNode* node);

        void pushToStack();
        void popFromStack();
        void processUnaryOp(ASTNode* node);
        void processIf(ASTNode* node);
        
        std::string getNotConditionJump(ASTNode* node);
        
        void processWhile(ASTNode* node);
        void processBlock(ASTNode* node);
        void processReservedFunction(ASTNode* node);
        void processFunctionCall(ASTNode* node);
        void processReturn(ASTNode* node);

        std::string getVarLabel(const std::string& varName);

        void processIdentifier(ASTNode* node);
        void processNumberLiteral(ASTNode* node);
        void processBooleanLiteral(ASTNode* node);
        void processStringLiteral(ASTNode* node);
        void processVoidLiteral(ASTNode* node);

        std::string buffer_read = "buffer_read:\n"
                                  "  ld buffer_start\n"
                                  "  st buffer\n"
                                  "buffer_read_do:\n"
                                  "  in\n"
                                  "  sta buffer\n\n"
                                  "  sub end_symb\n"
                                  "  jz buffer_read_end\n\n"
                                  "  ld buffer\n"
                                  "  inc\n"
                                  "  st buffer\n\n"
                                  "  jmp buffer_read_do\n"
                                  "buffer_read_end:\n"
                                  "  ret\n\n";
        
        std::string buffer_write = "buffer_write:\n"
                                   "  ld buffer_start\n"
                                   "  st buffer\n"
                                   "buffer_write_do:\n"
                                   "  lda buffer\n"
                                   "  out\n\n"
                                   "  sub end_symb\n"
                                   "  jz buffer_write_end\n\n"
                                   "  ld buffer\n"
                                   "  inc\n"
                                   "  st buffer\n\n"
                                   "  jmp buffer_write_do\n"
                                   "buffer_write_end:\n"
                                   "  ret\n\n";

        std::string str_copy = "str_copy:\n"
                               "  ld source_str\n"
                               "  st buffer_start\n\n"
                               "  ld target_str\n"
                               "  st target_buffer\n"
                               "str_copy_do:\n"
                               "  lda buffer_start\n"
                               "  sta target_buffer\n\n"
                               "  sub end_symb\n"
                               "  jz str_copy_end\n\n"
                               "  ld buffer_start\n"
                               "  inc\n"
                               "  st buffer_start\n\n"
                               "  ld target_buffer\n"
                               "  inc\n"
                               "  st target_buffer\n"
                               "  jmp str_copy_do\n"
                               "str_copy_end:\n"
                               "  ret\n\n";

        std::string assembleCode();
};

#endif