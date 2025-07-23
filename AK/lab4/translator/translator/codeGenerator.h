#ifndef _CODE_GENERATOR_H
#define _CODE_GENERATOR_H

#include "ASTNode.hpp"
#include "ASTVisitor.hpp"
#include "semanticAnalyzer.h"

#include <unordered_map>
#include <vector>
#include <sstream>
#include <algorithm>
#include <memory>

class CodeGenerator : ASTVisitor {
    public:
        CodeGenerator();
        ~CodeGenerator();

        std::string generateCode(ASTNode* root);

        void visit(VarDeclNode& node) override;
        void visit(NumberLiteralNode& node) override;
        void visit(CharLiteralNode& node) override;
        void visit(StringLiteralNode& node) override;
        void visit(BooleanLiteralNode& node) override;
        void visit(VoidLiteralNode& node) override;
        void visit(IntArrayLiteralNode& node) override;
        void visit(ArrayGetNode& node) override;
        void visit(MethodCallNode& node) override;
        void visit(IdentifierNode& node) override;
        void visit(AssignNode& node) override;
        void visit(BinaryOpNode& node) override;
        void visit(UnaryOpNode& node) override;
        void visit(IfNode& node) override;
        void visit(WhileNode& node) override;
        void visit(BreakNode& node) override;
        void visit(BlockNode& node) override;
        void visit(ParameterNode& node) override;
        void visit(FunctionNode& node) override;
        void visit(FunctionCallNode& node) override;
        void visit(ReturnNode& node) override;
    private:
        void visitWithLabels(ASTNode* node, const std::string& trueL, const std::string& falseL);
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
        const FunctionSignature* findReservedFunction(const std::string& name, const std::vector<std::string>& paramTypes, const std::string& expectedReturnType);

        void processReservedFunctionCall(FunctionCallNode& node);
        void processRegularFunctionCall(FunctionCallNode& node);

        std::vector<std::string> breakLabels;

        std::vector<std::string> dataSection;
        std::vector<std::string> codeSection;
        std::vector<std::string> funcSection;
        std::unordered_map<std::string, std::string> variables;
        std::unordered_map<std::string, int> functionLabels;

        std::unordered_map<std::string, std::vector<FunctionData>> functions;
        
        const std::unordered_map<std::string, std::vector<FunctionSignature>> reservedFunctions = {
            {"in", {
                {"int", {"int"}},
                {"int", {}},
                {"char", {}},
                {"string", {"int"}},
                {"string", {}},
                {"int[]", {"int"}},
                {"int[]", {}}
            }},
            {"out", {
                {"void", {"int"}},
                {"void", {"char"}},
                {"void", {"int[]"}},
                {"void", {"string"}}
            }}
        };

        const std::unordered_map<std::string, std::unordered_map<std::string, FunctionSignature>> typeMethods = {
            {"int[]", {
                {"size", {"int", {}}}
            }},

            {"string", {
                {"size", {"int", {}}}
            }}
        };

        int labelCounter = 0;
        int strCounter = 0;
        int arrCounter = 0;
        int bufferCounter = 0;

        std::shared_ptr<FunctionData> currentFunction;

        std::string currentTrueLabel;
        std::string currentFalseLabel;

        std::string getNewLabel();

        std::string mangleFunctionName(const std::string& name, const std::vector<std::string>& paramTypes);

        std::string nodeStr(ASTNode* node);

        // void processNode(ASTNode* node);

        void emitCode(const std::string& line);
        void emitCodeLabel(const std::string& label);
        void emitData(const std::string& line);

        // void processRoot(ASTNode* node);
        // void processFunction(ASTNode* node);
        // void processVarDecl(ASTNode* node);
        // void processAssignment(ASTNode* node);
        // void processBinaryOp(ASTNode* node);

        void pushToStack();
        void popFromStack();
        // void processUnaryOp(ASTNode* node);
        // void processIf(ASTNode* node);
        
        std::string getNotConditionJump(ASTNode* node);

        // void processWhile(ASTNode* node);
        // void processBlock(ASTNode* node);
        // void processReservedFunction(ASTNode* node);
        // void processFunctionCall(ASTNode* node);
        // void processReturn(ASTNode* node);

        std::string getVarLabel(const std::string& varName);

        // void processIdentifier(ASTNode* node);
        // void processNumberLiteral(ASTNode* node);
        // void processBooleanLiteral(ASTNode* node);
        // void processStringLiteral(ASTNode* node);
        // void processVoidLiteral(ASTNode* node);

        // void processIntArrayLiteral(ASTNode* node);
        // void processArrayGet(ASTNode* node);

        std::string data = ".data\n"
                           "  default_vector: default_interrupt\n"
                           "  input_vector: input_interrupt\n\n"
                           "  temp_right: 0\n"
                           "  temp_ret_addr: 0\n"
                           "  input_addr: 0x10\n"
                           "  output_addr: 0x11\n\n"
                           "  const_eot: 4\n"
                           "  const_space: 32\n\n"
                           "  const_10: 10\n"
                           "  const_48: 48\n\n"
                           "  token: 0\n\n"
                           "  read_int_val: 0\n"
                           "  read_delim: 10\n"
                           "  read_arr_stop: 0\n\n"
                           "  write_int_count: 0\n"
                           "  write_i: 0\n\n"
                           "  input_count: 0\n"
                           "  input_ptr: 0\n\n"
                           "  input_buffer_i: 0\n"
                           "  input_buffer_size: 10\n"
                           "  input_buffer: .zero 10\n";

        std::string interrupts = "default_interrupt:\n"
                                 "  iret\n"
                                 "input_interrupt:\n"
                                 "  lda input_addr\n"
                                 "  st token\n"
                                 "  iret\n\n";

        std::string read_char = "read_char:\n"
                                 "  ei\n"
                                 "  ld token\n"
                                 "  jz read_char\n"
                                 "  di\n"
                                 "  ret\n\n";

        std::string read_int = "read_int:\n"
                               "  ldi 0\n"
                               "  st read_int_val\n"
                               "read_int_do:\n"
                               "  call read_char\n \n"
                               "  ld token\n"
                               "  cmp const_eot\n"
                               "  jz read_int_stop\n\n"
                               "  cmp read_delim\n"
                               "  jz read_int_stop\n\n"
                               "  cmp const_space\n"
                               "  jz read_int_ret\n\n"
                               "  cmp const_10\n"
                               "  jz read_int_ret\n\n"
                               "  ld read_int_val\n"
                               "  mul const_10\n"
                               "  st read_int_val\n\n"
                               "  ld token\n"
                               "  sub const_48\n"
                               "  add read_int_val\n"
                               "  st read_int_val\n\n"
                               "  ldi 0\n"
                               "  st token\n\n"
                               "  jmp read_int_do\n"
                               "read_int_stop:\n"
                               "  ldi 1\n"
                               "  st read_arr_stop\n"
                               "read_int_ret:\n"
                               "  ldi 0\n"
                               "  st token\n\n"
                               "  ld read_int_val\n"
                               "  ret\n\n";

        std::string write_to_buf = "write_to_buf:\n"
                                   "  push\n\n"
                                   "  ldi input_buffer\n"
                                   "  add input_buffer_i\n"
                                   "  st temp_right\n\n"
                                   "  pop\n"
                                   "  sta temp_right\n\n"
                                   "  ld input_buffer_i\n"
                                   "  inc\n"
                                   "  st input_buffer_i\n\n"
                                   "  ret\n\n";

        std::string read_string = "read_string:\n"
                                  "  ldi input_buffer\n"
                                  "  add input_buffer_i\n"
                                  "  st input_ptr\n"
                                  "read_string_do:\n"
                                  "  ld input_buffer_i\n"
                                  "  cmp input_buffer_size\n"
                                  "  jz read_string_overflow\n\n"
                                  "  call read_char\n\n"
                                  "  ld token\n"
                                  "  cmp const_eot\n"
                                  "  jz read_string_ret\n\n"
                                  "  cmp read_delim\n"
                                  "  jz read_string_ret\n\n"
                                  "  call write_to_buf\n\n"
                                  "  ldi 0\n"
                                  "  st token\n\n"
                                  "  ldi input_buffer\n"
                                  "  add input_buffer_i\n"
                                  "  sub input_ptr\n"
                                  "  sub input_count\n"
                                  "  jz read_string_ret\n\n"
                                  "  jmp read_string_do\n"
                                  "read_string_overflow:\n"
                                  "  halt\n"
                                  "read_string_ret:\n"
                                  "  ldi 0\n"
                                  "  st token\n"
                                  "  st input_count\n"
                                  "  call write_to_buf\n\n"
                                  "  ld input_ptr\n\n"
                                  "  ret\n\n";

        std::string read_arr = "read_arr:\n"
                               "  ldi input_buffer\n"
                               "  add input_buffer_i\n"
                               "  st input_ptr\n\n"                      
                               "  ldi 0\n"
                               "  st read_arr_stop\n"
                               "read_arr_do:\n"
                               "  ld input_buffer_i\n"
                               "  cmp input_buffer_size\n"
                               "  jz read_arr_overflow\n\n"
                               "  call read_int\n\n"
                               "  call write_to_buf\n\n"
                               "  ld read_arr_stop\n"
                               "  jnz read_arr_ret\n\n"
                               "  ldi input_buffer\n"
                               "  add input_buffer_i\n"
                               "  sub input_ptr\n"
                               "  sub input_count\n"
                               "  jz read_arr_ret\n\n"
                               "  jmp read_arr_do\n"
                               "read_arr_overflow:\n"
                               "  halt\n"
                               "read_arr_ret:\n"
                               "  ldi 0\n"
                               "  st input_count\n"
                               "  call write_to_buf\n\n"
                               "  ld input_ptr\n\n"
                               "  ret\n\n";

        std::string write_char = "write_char:\n"
                                 "  sta output_addr\n"
                                 "  ret\n\n";

        std::string write_int = "write_int:\n"
                                "  st read_int_val\n"
                                "  ldi 0\n"
                                "  st write_int_count\n"
                                "write_int_div:\n"
                                "  ld read_int_val\n"
                                "  jz write_int_write\n"
                                "  rem const_10\n"
                                "  push\n\n"
                                "  ld read_int_val\n"
                                "  div const_10\n"
                                "  st read_int_val\n\n"
                                "  ld write_int_count\n"
                                "  inc\n"
                                "  st write_int_count\n\n"
                                "  jmp write_int_div\n"
                                "write_int_write:\n"
                                "  ld write_int_count\n"
                                "  jz write_int_ret\n"
                                "  dec\n"
                                "  st write_int_count\n\n"
                                "  pop\n"
                                "  add const_48\n"
                                "  sta output_addr\n\n"
                                "  jmp write_int_write\n"
                                "write_int_ret:\n"
                                "  ret\n\n";

        std::string write_string = "write_string:\n"
                                   "  st input_ptr\n"
                                   "  ldi 0\n"
                                   "  st write_i\n"
                                   "write_string_do:\n"
                                   "  ld input_ptr\n"
                                   "  add write_i\n"
                                   "  st temp_right\n"
                                   "  lda temp_right\n\n"
                                   "  jz write_string_ret\n"
                                   "  sta output_addr\n\n"
                                   "  ld write_i\n"
                                   "  inc\n"
                                   "  st write_i\n\n"
                                   "  jmp write_string_do\n"
                                   "write_string_ret:\n"
                                   "  ret\n\n";

        std::string write_arr = "write_arr:\n"
                                "  st input_ptr\n\n"
                                "  lda input_ptr\n"
                                "  jz write_arr_ret\n"
                                "  call write_int\n\n"
                                "  ldi 1\n"
                                "  st write_i\n"
                                "write_arr_do:\n"
                                "  ld input_ptr\n"
                                "  add write_i\n"
                                "  st temp_right\n"
                                "  lda temp_right\n"
                                "  jz write_arr_ret\n\n"
                                "  ld const_space\n"
                                "  sta output_addr\n\n"
                                "  lda temp_right\n"
                                "  call write_int\n\n"
                                "  ld write_i\n"
                                "  inc\n"
                                "  st write_i\n\n"
                                "  jmp write_arr_do\n"
                                "write_arr_ret:\n"
                                "  ret\n\n";

        std::string arr_size = "arr_size:\n"
                               "  st input_ptr\n\n"
                               "  ldi 0\n"
                               "  st write_i\n"
                               "arr_size_do:\n"
                               "  ld input_ptr\n"
                               "  add write_i\n"
                               "  st temp_right\n"
                               "  lda temp_right\n"
                               "  jz arr_size_ret\n\n"
                               "  ld write_i\n"
                               "  inc\n"
                               "  st write_i\n\n"
                               "  jmp arr_size_do\n"
                               "arr_size_ret:\n"
                               "  ld write_i\n"
                               "  ret\n\n";

        std::string assembleCode();
};

#endif