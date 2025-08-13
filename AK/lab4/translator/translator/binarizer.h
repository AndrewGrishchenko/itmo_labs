#ifndef _BINARIZER_H
#define _BINARIZER_H

#include <iostream>
#include <vector>
#include <cstdint>
#include <unordered_map>
#include <unordered_set>
#include <sstream>
#include <ranges>
#include <algorithm>
#include <fstream>

#include <iomanip>

class Binarizer {
    public:
        Binarizer() { }

        void parse(const std::string& data);
        void writeToFile(const std::string& filename) const;
        void dump();
    private:
        struct Instruction {
            uint8_t opcode;
            uint32_t operand;
        };

        const std::unordered_map<std::string, uint8_t> opcodeMap = {
            {"add",  0b00001},
            {"sub",  0b00010},
            {"div",  0b00011},
            {"mul",  0b00100},
            {"rem",  0b00101},
            {"inc",  0b00110},
            {"dec",  0b00111},
            {"not",  0b01000},
            {"cla",  0b01001},
            {"jmp",  0b01010},
            {"cmp",  0b01011},
            {"jz",   0b01100},
            {"jnz",  0b01101},
            {"jg",   0b01110},
            {"jge",  0b01111},
            {"jl",   0b10000},
            {"jle",  0b10001},
            {"push", 0b10010},
            {"pop",  0b10011},
            {"ld",   0b10100},
            {"lda",  0b10101},
            {"ldi",  0b10110},
            {"st",   0b10111},
            {"sta",  0b11000},
            {"call", 0b11001},
            {"ret",  0b11010},
            {"ei",   0b11011},
            {"di",   0b11100},
            {"iret", 0b11101},
            {"halt", 0b11110}
        };

        enum class Section {None, Text, Data, InterruptTable};

        size_t textStart;
        size_t dataStart;

        std::vector<uint32_t> dataSection;
        std::unordered_map<std::string, size_t> dataAddress;

        std::vector<Instruction> instructions;
        std::unordered_map<std::string, size_t> labelAddress;

        static void trim(std::string& s) {
            size_t start = s.find_first_not_of(" \t\r\n");
            size_t end = s.find_last_not_of(" \t\r\n");
            s = (start == std::string::npos) ? "" : s.substr(start, end - start + 1);
        }

        static void stripComment(std::string& s) {
            size_t pos = s.find(';');
            if (pos != std::string::npos) s = s.substr(0, pos);
        }

        static void toLower(std::string& s) {
            std::transform(s.begin(), s.end(), s.begin(), ::tolower);
        }

        static bool isNumber(const std::string& s) {
            if (s.empty()) return false;

            if (s.size() > 2 && s[0] == '0' && (s[1] == 'x' || s[1] == 'X')) {
                return std::all_of(s.begin() + 2, s.end(), ::isxdigit);
            }
            if (s.size() > 2 && s[0] == '0' && (s[1] == 'b' || s[1] == 'B')) {
                return std::all_of(s.begin() + 2, s.end(), [](char c) { return c == '0' || c == '1'; });
            }

            size_t start = (s[0] == '-') ? 1 : 0;
            return std::all_of(s.begin() + start, s.end(), ::isdigit);
        }

        static int parseNumber(const std::string& s) {
            if (s.size() > 2 && s.substr(0, 2) == "0x") {
                return std::stoi(s, nullptr, 16);
            } else if (s.size() > 2 && s.substr(0, 2) == "0b") {
                return std::stoi(s.substr(2), nullptr, 2);
            } else {
                return std::stoi(s);
            }
        }
};

#endif