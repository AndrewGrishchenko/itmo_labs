#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <string>

#include "translator.h"

int main() {
    std::freopen("input.txt", "r", stdin);

    std::string line, data = "";
    while (getline(std::cin, line)) {
        if (line.empty()) break;

        data += line + "\n";
    }

    Translator translator;
    translator.translate(data);

    
}