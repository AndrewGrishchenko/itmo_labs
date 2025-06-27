#ifndef _CODE_GENERATOR_H
#define _CODE_GENERATOR_H

#include "ASTNode.hpp"
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
                {"string", {"int"}},
                {"string", {}}
            }},
            {"out", {
                {"void", {"int"}},
                {"void", {"string"}}
            }}
        };

        int labelCounter = 0;
        int strCounter = 0;
        int arrCounter = 0;
        int bufferCounter = 0;

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

        void processIntArrayLiteral(ASTNode* node);
        void processArrayGet(ASTNode* node);

        std::string data = ".data\n"
                           "  default_vector: default_interrupt\n"
                           "  input_vector: input_interrupt\n\n"
                           "  end_symb: 10\n"
                           "  temp_right: 0\n"
                           "  input_addr: 0x10\n"
                           "  output_addr: 0x11\n"
                           "  output_string: 0\n"
                           "  output_string_i: 0\n"
                           "  token: 0\n"
                           "  token_i: 0\n"
                           "  token_count: 0\n"
                           "  token_buffer_count: 20\n"
                           "  token_buffer_i: 0\n"
                           "  token_buffer: .zero 20\n";

        std::string interrupts = "default_interrupt:\n"
                                 "  iret\n"
                                 "input_interrupt:\n"
                                 "  lda input_addr\n"
                                 "  st token\n"
                                 "  iret\n\n";

        std::string read_token = "read_token:\n"
                                 "  ei\n"
                                 "  ld token\n"
                                 "  jz read_token\n"
                                 "  di\n"
                                 "  ret\n\n";

        std::string read_string = "read_string:\n"
                                  "  ldi token_buffer\n"
                                  "  add token_buffer_i\n"
                                  "  st token_i\n"
                                  "read_string_do:\n"
                                  "  ld token_buffer_i\n"
                                  "  sub token_buffer_count\n"
                                  "  jz read_string_overflow\n\n"
                                  "  call read_token\n\n"
                                  "  ld token\n"
                                  "  sub end_symb\n"
                                  "  jz read_string_ret\n\n"
                                  "  ldi token_buffer\n"
                                  "  add token_buffer_i\n"
                                  "  st temp_right\n"
                                  "  ld token\n"
                                  "  sta temp_right\n\n"
                                  "  ld token_buffer_i\n"
                                  "  inc\n"
                                  "  st token_buffer_i\n\n"
                                  "  ldi 0\n"
                                  "  st token\n\n"
                                  "  ldi token_buffer\n"
                                  "  add token_buffer_i\n"
                                  "  sub token_i\n"
                                  "  sub token_count\n"
                                  "  jz read_string_ret\n\n"
                                  "  jmp read_string_do\n"
                                  "read_string_ret:\n"
                                  "  ldi token_buffer\n"
                                  "  add token_buffer_i\n"
                                  "  st temp_right\n"
                                  "  ldi 0\n"
                                  "  sta temp_right\n"
                                  "  st token\n\n"
                                  "  ld token_buffer_i\n"
                                  "  inc\n"
                                  "  st token_buffer_i\n\n"
                                  "  ld token_i\n"
                                  "  ret\n"
                                  "read_string_overflow:\n"
                                  "  halt\n\n";

        std::string write_token = "write_token:\n"
                                  "  ld token\n"
                                  "  sta output_addr\n"
                                  "  ret\n\n";
        
        std::string write_string = "write_string:\n"
                                   "  ldi 0\n"
                                   "  st output_string_i\n"
                                   "write_string_do:\n"
                                   "  ld output_string\n"
                                   "  add output_string_i\n"
                                   "  st temp_right\n"
                                   "  lda temp_right\n"
                                   "  st token\n"
                                   "  jz write_string_ret\n"
                                   "  call write_token\n\n"
                                   "  ld output_string_i\n"
                                   "  inc\n"
                                   "  st output_string_i\n\n"
                                   "  jmp write_string_do\n"
                                   "write_string_ret:\n"
                                   "  ret\n\n";

        std::string assembleCode();
};

#endif