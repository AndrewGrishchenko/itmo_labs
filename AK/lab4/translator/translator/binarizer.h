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
            {"add",  0b000001},
            {"sub",  0b000010},
            {"div",  0b000011},
            {"mul",  0b000100},
            {"rem",  0b000101},
            {"inc",  0b000110},
            {"dec",  0b000111},
            {"not",  0b001000},
            {"cla",  0b001001},
            {"jmp",  0b001010},
            {"cmp",  0b001011},
            {"jz",   0b001100},
            {"jnz",  0b001101},
            {"jg",   0b001110},
            {"jge",  0b001111},
            {"jl",   0b010000},
            {"jle",  0b010001},
            {"ja",   0b010010},
            {"jae",  0b010011},
            {"jb",   0b010100},
            {"jbe",  0b010101},
            {"push", 0b010110},
            {"pop",  0b010111},
            {"ld",   0b011000},
            {"lda",  0b011001},
            {"ldi",  0b011010},
            {"st",   0b011011},
            {"sta",  0b011100},
            {"call", 0b011101},
            {"ret",  0b011110},
            {"ei",   0b011111},
            {"di",   0b100000},
            {"iret", 0b100001},
            {"halt", 0b100010}
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

        static long parseNumber(const std::string& s) {
            if (s.size() > 2 && s.substr(0, 2) == "0x") {
                return std::stol(s, nullptr, 16);
            } else if (s.size() > 2 && s.substr(0, 2) == "0b") {
                return std::stol(s.substr(2), nullptr, 2);
            } else {
                return std::stol(s);
            }
        }
};

#endif