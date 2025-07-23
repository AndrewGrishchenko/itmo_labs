#ifndef _TREE_GEN_H
#define _TREE_GEN_H

#include <string>
#include <sstream>
#include <vector>

#include <iostream>

#include "ASTNode.hpp"
#include "semanticAnalyzer.h"

enum class TokenType {
    // Keywords
    KeywordIf,
    KeywordElse,
    KeywordWhile,
    KeywordBreak,
    KeywordReturn,
    KeywordVoid,

    // Data types
    KeywordInt,
    KeywordChar,
    KeywordString,
    KeywordBool,
    KeywordIntArr,

    // Utility
    Identifier,
    Equals,
    Number,
    Char,
    String,
    Boolean,

    // Delimeters
    LParen,
    RParen,
    LBrace,
    RBrace,
    LBracket,
    RBracket,
    Semicolon,
    Dot,
    Comma,

    // Logic operators
    LogicNot,
    LogicAnd,
    LogicOr,
    LogicEqual,
    LogicNotEqual,
    LogicGreater,
    LogicGreaterEqual,
    LogicLess,
    LogicLessEqual,
    
    // Operators
    Plus,
    Minus,
    Multiply,
    Divide,
    Rem,
    
    // Utility
    EndOfFile,
    Unknown
};

struct Token {
    TokenType type;
    std::string value;

    Token(TokenType type, std::string value)
        : type(type), value(value) { }
};

class TreeGenerator {
    public:
        TreeGenerator();
        ~TreeGenerator();

        ASTNode* makeTree(std::string data);

    private:
        std::vector<Token> tokenize(const std::string& input);

        ASTNode* parseVarStatement(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseAssignStatement(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseBlock(std::vector<Token> tokens, size_t& pos);

        ASTNode* parseStatement(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseIf(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseWhile(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseBreak(std::vector<Token> tokens, size_t& pos);

        ASTNode* parseParameter(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseFunction(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseCallParameter(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseFunctionCall(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseReturn(std::vector<Token> tokens, size_t& pos);

        ASTNode* parseExpression(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseArray(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseArrayGet(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseArraySize(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseMethodCall(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseTerm(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseFactor(std::vector<Token> tokens, size_t& pos);

        ASTNode* parseLogicOr(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseLogicAnd(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseEquality(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseComparsion(std::vector<Token> tokens, size_t& pos);
        ASTNode* parseUnary(std::vector<Token> tokens, size_t& pos);
        ASTNode* parsePrimary(std::vector<Token> tokens, size_t& pos);

        std::string tokenStr(Token token);

        bool isAlpha(char c) {
            return std::isalpha(static_cast<unsigned char>(c)) || c == '_';
        }

        bool isDigit(char c) {
            return std::isdigit(static_cast<unsigned char>(c));
        }

        bool isAlnum(char c) {
            return isAlpha(c) || isDigit(c);
        }
};

#endif