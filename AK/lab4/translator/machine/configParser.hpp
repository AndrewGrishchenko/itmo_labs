#pragma once

#include <fstream>
#include <sstream>
#include <stdexcept>
#include <unordered_map>
#include <algorithm>
#include <cctype>

enum InputMode {
    NONE,
    MODE_TOKEN,
    MODE_STREAM
};

struct MachineConfig {
    std::string input_file;
    InputMode input_mode = InputMode::NONE;
    size_t schedule_start = -1;
    size_t schedule_offset = -1;
    std::string output_file;
    std::string log_file;
};

inline std::string trim(const std::string& s) {
    size_t start = s.find_first_not_of(" \t\r\n");
    size_t end = s.find_last_not_of(" \t\r\n");
    return (start == std::string::npos) ? "" : s.substr(start, end - start + 1);
}

inline MachineConfig parseConfig(std::string fileName) {
    std::ifstream in(fileName);
    if (!in.is_open()) {
        throw std::runtime_error("Failed to open config file: " + fileName);
    }

    MachineConfig config;
    std::string line;
    while (std::getline(in, line)) {
        size_t colon = line.find(':');
        if (colon == std::string::npos) continue;

        std::string key = trim(line.substr(0, colon));
        std::string value = trim(line.substr(colon + 1));

        if (key == "input_file") {
            config.input_file = value;
        } else if (key == "input_mode") {
            if (value == "token") config.input_mode = InputMode::MODE_TOKEN;
            else if (value == "stream") config.input_mode = InputMode::MODE_STREAM;
            else throw std::runtime_error("Invalid input_mode: " + value);
        } else if (key == "schedule_start") {
            config.schedule_start = std::stoi(value);
        } else if (key == "schedule_offset") {
            config.schedule_offset = std::stoi(value);
        } else if (key == "output_file") {
            config.output_file = value;
        } else if (key == "log_file") {
            config.log_file = value;
        } else {
            throw std::runtime_error("Unknown config key: " + key);
        }
    }

    return config;
}