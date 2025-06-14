#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <fstream>
#include <string>

#include "translator.h"

int main() {

    std::ifstream file("input.txt");
    std::stringstream buffer;
    buffer << file.rdbuf();
    std::string data = buffer.str();

    Translator translator;
    translator.makeTree(data);

    
}