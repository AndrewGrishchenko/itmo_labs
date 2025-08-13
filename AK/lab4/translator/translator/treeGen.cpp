#include "treeGen.h"

TreeGenerator::TreeGenerator() { }

TreeGenerator::~TreeGenerator() { }

std::string TreeGenerator::tokenStr(Token token) {
    switch (token.type) {
        case TokenType::KeywordIf:
            return "KeywordIf";
        case TokenType::KeywordElse:
            return "KeywordElse";
        case TokenType::KeywordWhile:
            return "KeywordWhile";
        case TokenType::Identifier:
            return "Identifier";
        case TokenType::Equals:
            return "Equals";
        case TokenType::Number:
            return "Number";
        case TokenType::String:
            return "String";
        case TokenType::Boolean:
            return "Boolean";
        case TokenType::LParen:
            return "LParen";
        case TokenType::RParen:
            return "RParen";
        case TokenType::LBrace:
            return "LBrace";
        case TokenType::RBrace:
            return "RBrace";
        case TokenType::LogicAnd:
            return "LogicAnd";
        case TokenType::LogicOr:
            return "LogicOr";
        case TokenType::LogicEqual:
            return "LogicEqual";
        case TokenType::LogicGreater:
            return "LogicGreater";
        case TokenType::LogicGreaterEqual:
            return "LogicGreaterEqual";
        case TokenType::LogicLess:
            return "LogicLess";
        case TokenType::LogicLessEqual:
            return "LogicLessEqual";
        case TokenType::Semicolon:
            return "Semicolon";
        case TokenType::EndOfFile:
            return "EndOfFile";
        case TokenType::Plus:
            return "Plus";
        case TokenType::Minus:
            return "Minus";
        case TokenType::Multiply:
            return "Multiply";
        case TokenType::Divide:
            return "Divide";
        case TokenType::Rem:
            return "Rem";
        case TokenType::KeywordInt:
            return "KeywordInt";
        case TokenType::KeywordString:
            return "KeywordString";
        case TokenType::KeywordBool:
            return "KeywordBool";
        case TokenType::Comma:
            return "Comma";
        case TokenType::KeywordReturn:
            return "Return";
        case TokenType::KeywordVoid:
            return "Void";
        case TokenType::KeywordIntArr:
            return "IntArr";
        case TokenType::LBracket:
            return "LBracket";
        case TokenType::RBracket:
            return "RBracket";
        case TokenType::Dot:
            return "Dot";
        case TokenType::KeywordChar:
            return "KeywordChar";
        case TokenType::Char:
            return "Char";
        default:
            return "Unknown";
    }
}

ASTNode* TreeGenerator::makeTree(std::string data) {
    BlockNode* root = new BlockNode();

    std::vector<Token> tokens = tokenize(data);

    size_t pos = 0;

    while (pos < tokens.size() && tokens[pos].type != TokenType::EndOfFile) {
        root->addChild(parseStatement(tokens, pos));
    }

    return root;
}

ASTNode* TreeGenerator::parseAssignStatement(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected variable name");
    std::string varName = tokens[pos].value;
    pos++;

    ASTNode* var;
    if (tokens[pos].type == TokenType::LBracket) {
        pos++;

        ASTNode* expr = parseExpression(tokens, pos);
        
        if (tokens[pos].type != TokenType::RBracket)
            throw std::runtime_error("Expected ']'");
        pos++;

        var = new ArrayGetNode(new IdentifierNode(varName), expr);
    } else {
        var = new IdentifierNode(varName);
    }

    if (tokens[pos].type != TokenType::Equals)
        throw std::runtime_error("Expected '=', got " + tokenStr(tokens[pos]));
    pos++;

    ASTNode* expr = parseExpression(tokens, pos);

    if (tokens[pos].type != TokenType::Semicolon)
        throw std::runtime_error("Expected ';'");
    pos++;

    return new AssignNode(var, expr);
}

ASTNode* TreeGenerator::parseVarStatement(std::vector<Token> tokens, size_t& pos) {
    std::string type;
    if (tokens[pos].type == TokenType::KeywordInt)
        type = "int";
    else if (tokens[pos].type == TokenType::KeywordChar)
        type = "char";
    else if (tokens[pos].type == TokenType::KeywordString)
        type = "string";
    else if (tokens[pos].type == TokenType::KeywordBool)
        type = "bool";
    else if (tokens[pos].type == TokenType::KeywordIntArr)
        type = "int[]";
    else
        throw std::runtime_error("Expected data type");
    pos++;

    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected variable name");
    std::string varName = tokens[pos].value;
    pos++;

    if (tokens[pos].type != TokenType::Equals)
        throw std::runtime_error("Expected '='");
    pos++;

    ASTNode* expr;

    expr = parseExpression(tokens, pos);
    
    if (tokens[pos].type != TokenType::Semicolon)
        throw std::runtime_error("Expected ';'");
    pos++;
    
    return new VarDeclNode(type, varName, expr);
}

ASTNode* TreeGenerator::parseArray(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::LBrace)
        throw std::runtime_error("Expected '{'");
    pos++;

    std::vector<ASTNode*> values;
    while (tokens[pos].type != TokenType::RBrace) {
        values.push_back(parseExpression(tokens, pos));

        if (tokens[pos].type == TokenType::Comma)
            pos++;
        else if (tokens[pos].type != TokenType::RBrace)
            throw std::runtime_error("Expected ',' or '}'");
    }
    pos++;

    return new IntArrayLiteralNode(values);
}

ASTNode* TreeGenerator::parseStatement(std::vector<Token> tokens, size_t& pos) {
    ASTNode* node;
    
    if (tokens[pos].type == TokenType::KeywordInt ||
        tokens[pos].type == TokenType::KeywordChar ||
        tokens[pos].type == TokenType::KeywordString ||
        tokens[pos].type == TokenType::KeywordBool || 
        tokens[pos].type == TokenType::KeywordVoid ||
        tokens[pos].type == TokenType::KeywordIntArr) {
        if (pos + 2 < tokens.size() &&
            tokens[pos + 1].type == TokenType::Identifier &&
            tokens[pos + 2].type == TokenType::LParen) {
            return parseFunction(tokens, pos);
        } else {
            return parseVarStatement(tokens, pos);
        }
    } else if (tokens[pos].type == TokenType::KeywordIf) {
        return parseIf(tokens, pos);
    } else if (tokens[pos].type == TokenType::KeywordWhile) {
        return parseWhile(tokens, pos);
    } else if (tokens[pos].type == TokenType::KeywordBreak) {
        return parseBreak(tokens, pos);
    } else if (tokens[pos].type == TokenType::KeywordReturn) {
        return parseReturn(tokens, pos);
    } else if (tokens[pos].type == TokenType::Identifier) {
        if (pos + 1 < tokens.size() &&
            tokens[pos + 1].type == TokenType::LParen) {
            ASTNode* node = parseFunctionCall(tokens, pos);

            if (tokens[pos].type != TokenType::Semicolon)
                throw std::runtime_error("Expected ';'");
            pos++;
            
            return node;
        } else {
            return parseAssignStatement(tokens, pos);
        }

    } else {
        return parseExpression(tokens, pos);
    }
}

ASTNode* TreeGenerator::parseIf(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::KeywordIf)
        throw std::runtime_error("Expected 'if'");
    pos++;

    if (tokens[pos].type != TokenType::LParen)
        throw std::runtime_error("Expected '('");
    pos++;

    ASTNode* condition = parseExpression(tokens, pos);

    if (tokens[pos].type != TokenType::RParen)
        throw std::runtime_error("Expected ')'");
    pos++;

    ASTNode* thenBranch = parseBlock(tokens, pos);

    ASTNode* elseBranch = nullptr;
    if (pos < tokens.size() && tokens[pos].type == TokenType::KeywordElse) {
        pos++;
        elseBranch = parseBlock(tokens, pos);
    }

    return new IfNode(condition, thenBranch, elseBranch);
}

ASTNode* TreeGenerator::parseWhile(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::KeywordWhile)
        throw std::runtime_error("Expected 'while'");
    pos++;

    if (tokens[pos].type != TokenType::LParen)
        throw std::runtime_error("Expected '('");
    pos++;

    ASTNode* condition = parseExpression(tokens, pos);

    if (tokens[pos].type != TokenType::RParen)
        throw std::runtime_error("Expected ')");
    pos++;

    ASTNode* body = parseBlock(tokens, pos);

    return new WhileNode(condition, body);
}

ASTNode* TreeGenerator::parseBreak(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::KeywordBreak)
        throw std::runtime_error("Expected 'break'");
    pos++;

    if (tokens[pos].type != TokenType::Semicolon)
        throw std::runtime_error("Expected ';'");
    pos++;

    return new BreakNode();
}

ASTNode* TreeGenerator::parseBlock(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::LBrace)
        throw std::runtime_error("Expected '{'");
    pos++;

    BlockNode* block = new BlockNode();

    while (pos < tokens.size() && tokens[pos].type != TokenType::RBrace && tokens[pos].type != TokenType::EndOfFile) {
        block->children.push_back(parseStatement(tokens, pos));
    }

    if (tokens[pos].type != TokenType::RBrace)
        throw std::runtime_error("Expected '}'");
    pos++;

    return block;
}

ASTNode* TreeGenerator::parseParameter(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::KeywordInt &&
        tokens[pos].type != TokenType::KeywordChar &&
        tokens[pos].type != TokenType::KeywordString &&
        tokens[pos].type != TokenType::KeywordBool &&
        tokens[pos].type != TokenType::KeywordIntArr)
        throw std::runtime_error("Expected data type");
    std::string type = tokens[pos].value;
    pos++;

    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected identifier");
    std::string name = tokens[pos].value;
    pos++;

    return new ParameterNode(name, type);
}

ASTNode* TreeGenerator::parseFunction(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::KeywordInt &&
        tokens[pos].type != TokenType::KeywordChar &&
        tokens[pos].type != TokenType::KeywordString &&
        tokens[pos].type != TokenType::KeywordBool &&
        tokens[pos].type != TokenType::KeywordVoid &&
        tokens[pos].type != TokenType::KeywordIntArr)
        throw std::runtime_error("Expected data type");
    std::string returnType = tokens[pos].value;
    pos++;

    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected identifier");
    std::string name = tokens[pos].value;
    pos++;

    if (tokens[pos].type != TokenType::LParen)
        throw std::runtime_error("Expected '('");
    pos++;

    std::vector<ASTNode*> parameters;
    while (pos < tokens.size() && tokens[pos].type != TokenType::RParen) {
        parameters.push_back(parseParameter(tokens, pos));

        if (tokens[pos].type == TokenType::Comma) {
            pos++;
        } else if (tokens[pos].type != TokenType::RParen)
            throw std::runtime_error("Expected ',' or ')'");
    }

    if (tokens[pos].type != TokenType::RParen)
        throw std::runtime_error("Expected ')'");
    pos++;

    ASTNode* body = parseBlock(tokens, pos);

    return new FunctionNode(returnType, name, parameters, body);
}

ASTNode* TreeGenerator::parseFunctionCall(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected identifier");
    std::string name = tokens[pos].value;
    pos++;

    if (tokens[pos].type != TokenType::LParen)
        throw std::runtime_error("Expected '('");
    pos++;

    std::vector<ASTNode*> parameters;
    while (pos < tokens.size() && tokens[pos].type != TokenType::RParen) {
        parameters.push_back(parseExpression(tokens, pos));
        
        if (tokens[pos].type == TokenType::Comma) {
            pos++;
        } else if (tokens[pos].type != TokenType::RParen)
            throw std::runtime_error("Expected ',' or ')'");
    }

    if (tokens[pos].type != TokenType::RParen)
        throw std::runtime_error("Expected ')'");
    pos++;

    return new FunctionCallNode(name, parameters);
}

ASTNode* TreeGenerator::parseReturn(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::KeywordReturn)
        throw std::runtime_error("Expected 'return'");
    pos++;

    if (tokens[pos].type == TokenType::Semicolon) {
        pos++;
        return new ReturnNode(new VoidLiteralNode());
    } else {
        ASTNode* node = parseExpression(tokens, pos);

        if (tokens[pos].type != TokenType::Semicolon)
            throw std::runtime_error("Expected ';'");
        pos++;

        return new ReturnNode(node);
    }
}

ASTNode* TreeGenerator::parseArrayGet(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected identifier");
    ASTNode* object = new IdentifierNode(tokens[pos].value);
    pos++;

    if (tokens[pos].type != TokenType::LBracket)
        throw std::runtime_error("Expected '['");
    pos++;

    ASTNode* expr = parseExpression(tokens, pos);
    
    if (tokens[pos].type != TokenType::RBracket)
        throw std::runtime_error("Expected ']'");
    pos++;

    return new ArrayGetNode(object, expr);
}

ASTNode* TreeGenerator::parseMethodCall(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected identifier");
    IdentifierNode* objectNode = new IdentifierNode(tokens[pos].value);
    pos++;

    if (tokens[pos].type != TokenType::Dot)
        throw std::runtime_error("Expected dot");
    pos++;

    if (tokens[pos].type != TokenType::Identifier)
        throw std::runtime_error("Expected identifier");
    std::string methodName = tokens[pos].value;
    pos++;

    if (tokens[pos].type != TokenType::LParen)
        throw std::runtime_error("Expected '('");
    pos++;

    std::vector<ASTNode*> args;
    while (tokens[pos].type != TokenType::RParen) {
        args.push_back(parsePrimary(tokens, pos));

        if (tokens[pos].type == TokenType::Comma)
            pos++;
    }

    if (tokens[pos].type != TokenType::RParen)
        throw std::runtime_error("Expected ')'");
    pos++;

    return new MethodCallNode(objectNode, methodName, args);
}

ASTNode* TreeGenerator::parseExpression(std::vector<Token> tokens, size_t& pos) {
    return parseLogicOr(tokens, pos);
}

ASTNode* TreeGenerator::parseTerm(std::vector<Token> tokens, size_t& pos) {
    ASTNode* node = parseFactor(tokens, pos);

    while (pos < tokens.size() &&
          (tokens[pos].type == TokenType::Plus || tokens[pos].type == TokenType::Minus)) {
        std::string op = tokens[pos].value;
        pos++;
        ASTNode* right = parseFactor(tokens, pos);
        node = new BinaryOpNode(op, node, right);
    }

    return node;
}

ASTNode* TreeGenerator::parseFactor(std::vector<Token> tokens, size_t& pos) {
    ASTNode* node = parseUnary(tokens, pos);
    
    while (pos < tokens.size() &&
          (tokens[pos].type == TokenType::Multiply || 
           tokens[pos].type == TokenType::Divide ||
           tokens[pos].type == TokenType::Rem)) {
        std::string op = tokens[pos].value;
        pos++;
        ASTNode* right = parseUnary(tokens, pos);
        node = new BinaryOpNode(op, node, right); 
    }

    return node;
}

ASTNode* TreeGenerator::parseLogicOr(std::vector<Token> tokens, size_t& pos) {
    ASTNode* node = parseLogicAnd(tokens, pos);
    while (pos < tokens.size() && tokens[pos].type == TokenType::LogicOr) {
        std::string op = tokens[pos].value;
        pos++;
        ASTNode* right = parseLogicAnd(tokens, pos);
        node = new BinaryOpNode({op, node, right});
    }

    return node;
}

ASTNode* TreeGenerator::parseLogicAnd(std::vector<Token> tokens, size_t& pos) {
    ASTNode* node = parseEquality(tokens, pos);
    while (pos < tokens.size() && tokens[pos].type == TokenType::LogicAnd) {
        std::string op = tokens[pos].value;
        pos++;
        ASTNode* right = parseEquality(tokens, pos);
        node = new BinaryOpNode(op, node, right);
    }

    return node;
}

ASTNode* TreeGenerator::parseEquality(std::vector<Token> tokens, size_t& pos) {
    ASTNode* node = parseComparsion(tokens, pos);
    while (pos < tokens.size() && 
          (tokens[pos].type == TokenType::LogicEqual || tokens[pos].type == TokenType::LogicNotEqual)) {
        std::string op = tokens[pos].value;
        pos++;
        ASTNode* right = parseComparsion(tokens, pos);
        node = new BinaryOpNode(op, node, right);
    }

    return node;
}

ASTNode* TreeGenerator::parseComparsion(std::vector<Token> tokens, size_t& pos) {
    ASTNode* node = parseTerm(tokens, pos);
    while (pos < tokens.size() && 
          (tokens[pos].type == TokenType::LogicGreater || tokens[pos].type == TokenType::LogicGreaterEqual ||
           tokens[pos].type == TokenType::LogicLess || tokens[pos].type == TokenType::LogicLessEqual)) {
        std::string op = tokens[pos].value;
        pos++;
        ASTNode* right = parseTerm(tokens, pos);
        node = new BinaryOpNode(op, node, right);
    }

    return node;
}

ASTNode* TreeGenerator::parseUnary(std::vector<Token> tokens, size_t& pos) {
    if (pos < tokens.size() && 
       (tokens[pos].type == TokenType::LogicNot || tokens[pos].type == TokenType::Minus)) {
        std::string op = tokens[pos].value;
        pos++;
        ASTNode* operand = parseUnary(tokens, pos);
        return new UnaryOpNode(op, operand);
    }
    return parsePrimary(tokens, pos);
}

ASTNode* TreeGenerator::parsePrimary(std::vector<Token> tokens, size_t& pos) {
    if (tokens[pos].type == TokenType::Number) {
        int value = std::stoi(tokens[pos].value);
        pos++;
        return new NumberLiteralNode(value);
    } else if (tokens[pos].type == TokenType::Char) {
        char value = tokens[pos].value[0];
        pos++;
        return new CharLiteralNode(value);
    } else if (tokens[pos].type == TokenType::String) {
        std::string value = tokens[pos].value;
        pos++;
        return new StringLiteralNode(value);
    } else if (tokens[pos].type == TokenType::Boolean) {
        bool value = tokens[pos].value == "true";
        pos++;
        return new BooleanLiteralNode(value);
    } else if (tokens[pos].type == TokenType::Identifier) {
        if (pos + 1 < tokens.size() && tokens[pos + 1].type == TokenType::LParen) {
            return parseFunctionCall(tokens, pos);
        } else if (pos + 1 < tokens.size() && tokens[pos + 1].type == TokenType::LBracket) {
            return parseArrayGet(tokens, pos);
        } else if (pos + 1 < tokens.size() && tokens[pos + 1].type == TokenType::Dot) {
            return parseMethodCall(tokens, pos);
        } else {
            std::string name = tokens[pos].value;
            pos++;
            return new IdentifierNode(name);
        }
    } else if (tokens[pos].type == TokenType::LParen) {
        pos++;
        ASTNode* expr = parseExpression(tokens, pos);
        if (tokens[pos].type != TokenType::RParen)
            throw std::runtime_error("Expected ')'");    
        pos++;
        return expr;
    } else if (tokens[pos].type == TokenType::LBrace) {
        return parseArray(tokens, pos);
    }

    throw std::runtime_error("Unexpected token in factor: " + tokenStr(tokens[pos]));
}

std::vector<Token> TreeGenerator::tokenize(const std::string& input) {
    std::vector<Token> tokens;
    size_t pos = 0;

    while (pos < input.size()) {
        char current = input[pos];

        if (current == '\n') {
            pos++;
            continue;
        }

        if (isspace(current)) {
            pos++;
            continue;
        }

        if (isAlpha(current)) {
            size_t start = pos;
            while (pos < input.size() && isAlnum(input[pos])) pos++;

            std::string word = input.substr(start, pos - start);
            if (word == "if") {
                tokens.push_back({TokenType::KeywordIf, word});
            } else if (word == "else") {
                tokens.push_back({TokenType::KeywordElse, word});
            } else if (word == "while") {
                tokens.push_back({TokenType::KeywordWhile, word});
            } else if (word == "break") {
                tokens.push_back({TokenType::KeywordBreak, word});
            } else if (word == "true" || word == "false") {
                tokens.push_back({TokenType::Boolean, word});
            } else if (word == "void") {
                tokens.push_back({TokenType::KeywordVoid, word});
            } else if (word == "int") {
                size_t tempPos = pos;
                while (tempPos < input.size() && isspace(input[tempPos])) tempPos++;

                if (tempPos < input.size() && input[tempPos] == '[') {
                    tempPos++;
                    while (tempPos < input.size() && isspace(input[tempPos])) tempPos++;

                    if (tempPos < input.size() && input[tempPos] == ']') {
                        tempPos++;
                        pos = tempPos;
                        tokens.push_back({TokenType::KeywordIntArr, "int[]"});
                        continue;
                    }
                }

                tokens.push_back({TokenType::KeywordInt, word});
                continue;
            } else if (word == "char") {
                tokens.push_back({TokenType::KeywordChar, word});
            } else if (word == "string") {
                tokens.push_back({TokenType::KeywordString, word});
            } else if (word == "bool") {
                tokens.push_back({TokenType::KeywordBool, word});
            } else if (word == "return") {
                tokens.push_back({TokenType::KeywordReturn, word});
            } else {
                tokens.push_back({TokenType::Identifier, word});
            }

            continue;
        }

        if (isDigit(current)) {
            size_t start = pos;
            while (pos < input.size() && isDigit(input[pos])) pos++;
            tokens.push_back({TokenType::Number, input.substr(start, pos - start)});
            continue;
        }

        if (current == '"') {
            size_t start = ++pos;
            std::string str;

            while (pos < input.size() && input[pos] != '"') str += input[pos++];

            if (pos < input.size() && input[pos] == '"') {
                pos++;
                tokens.push_back({TokenType::String, str});
            } else {
                throw std::runtime_error("Expected \"");
            }

            continue;
        }

        if (current == '\'') {
            pos++;

            if (pos >= input.size())
                throw std::runtime_error("expected \'");

            char char_value;

            if (pos < input.size() && input[pos] == '\\') {
                pos++;

                if (pos >= input.size())
                    throw std::runtime_error("incomplete char literal");

                switch (input[pos]) {
                    case 'n':
                        char_value = '\n';
                        break;
                    case 't':
                        char_value = '\t';
                        break;
                    case '\\':
                        char_value = '\\';
                        break;
                    case '\'':
                        char_value = '\'';
                        break;
                    default:
                        throw std::runtime_error("unsuppoerted escapte-sequence: " + std::to_string(input[pos]));
                }
            } else {
                if (pos >= input.size())
                    throw std::runtime_error("incomplete char literal");

                char_value = input[pos];
            }
            pos++;

            if (pos >= input.size() || input[pos] != '\'')
                throw std::runtime_error("expected \'");
            pos++;

            tokens.push_back({TokenType::Char, std::string(1, char_value)});
            continue;
        }

        switch (current) {
            case '=':
                if (pos + 1 < input.size() && input[pos + 1] == '=') {
                    tokens.push_back({TokenType::LogicEqual, "=="});
                    pos += 2;
                } else {
                    tokens.push_back({TokenType::Equals, "="});
                    pos++;
                }
                break;
            case ';':
                tokens.push_back({TokenType::Semicolon, ";"});
                pos++;
                break;
            case '.':
                tokens.push_back({TokenType::Dot, "."});
                pos++;
                break;
            case ',':
                tokens.push_back({TokenType::Comma, ","});
                pos++;
                break;
            case '(':
                tokens.push_back({TokenType::LParen, "("});
                pos++;
                break;
            case ')':
                tokens.push_back({TokenType::RParen, ")"});
                pos++;
                break;
            case '{':
                tokens.push_back({TokenType::LBrace, "{"});
                pos++;
                break;
            case '}':
                tokens.push_back({TokenType::RBrace, "}"});
                pos++;
                break;
            case '[':
                tokens.push_back({TokenType::LBracket, "["});
                pos++;
                break;
            case ']':
                tokens.push_back({TokenType::RBracket, "]"});
                pos++;
                break;
            case '!':
                if (pos + 1 < input.size() && input[pos + 1] == '=') {
                    tokens.push_back({TokenType::LogicNotEqual, "!="});
                    pos += 2;
                } else {
                    tokens.push_back({TokenType::LogicNot, "!"});
                    pos++;
                }
                break;
            case '+':
                tokens.push_back({TokenType::Plus, "+"});
                pos++;
                break;
            case '-':
                tokens.push_back({TokenType::Minus, "-"});
                pos++;
                break;
            case '*':
                tokens.push_back({TokenType::Multiply, "*"});
                pos++;
                break;
            case '/':
                tokens.push_back({TokenType::Divide, "/"});
                pos++;
                break;
            case '%':
                tokens.push_back({TokenType::Rem, "%"});
                pos++;
                break;
            case '&':
                if (pos + 1 < input.size() && input[pos + 1] == '&') {
                    tokens.push_back({TokenType::LogicAnd, "&&"});
                    pos += 2;
                } else {
                    tokens.push_back({TokenType::Unknown, "&"});
                    pos++;
                }
                break;
            case '|':
                if (pos + 1 < input.size() && input[pos + 1] == '|') {
                    tokens.push_back({TokenType::LogicOr, "||"});
                    pos += 2;
                } else {
                    tokens.push_back({TokenType::Unknown, "|"});
                    pos++;
                }
                break;
            case '>':
                if (pos + 1 < input.size() && input[pos + 1] == '=') {
                    tokens.push_back({TokenType::LogicGreaterEqual, ">="});
                    pos += 2;
                } else {
                    tokens.push_back({TokenType::LogicGreater, ">"});
                    pos++;
                }
                break;
            case '<':
                if (pos + 1 < input.size() && input[pos + 1] == '=') {
                    tokens.push_back({TokenType::LogicLessEqual, "<="});
                    pos += 2;
                } else {
                    tokens.push_back({TokenType::LogicLess, "<"});
                    pos++;
                }
                break;
            default:
                tokens.push_back({TokenType::Unknown, std::string(1, current)});
                pos++;
                break;
        }
    }

    tokens.push_back({TokenType::EndOfFile, ""});
    return tokens;
}