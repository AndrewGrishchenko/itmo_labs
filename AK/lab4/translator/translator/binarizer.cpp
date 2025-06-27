#include "binarizer.h"

Binarizer::Binarizer() { }
Binarizer::~Binarizer() { }

void Binarizer::parse(const std::string& inputData) {
    std::istringstream iss(inputData);
    std::string line;

    enum class Section { None, Text, Data };
    Section current = Section::None;

    textStart = 1;
    dataStart = 0;

    size_t textSize = 0;
    size_t dataSize = 0;

    labelAddress.clear();
    dataAddress.clear();
    instructions.clear();
    dataSection.clear();

    while (std::getline(iss, line)) {
        stripComment(line);
        trim(line);
        if (line.empty()) continue;

        if (line == ".text") {
            current = Section::Text;
            continue;
        }
        else if (line == ".data") {
            current = Section::Data;
            continue;
        }

        if (line.size() > 4 && line.substr(0,4) == ".org") {
            std::string addrStr = line.substr(4);
            trim(addrStr);
            size_t orgAddr = 0;
            if (!isNumber(addrStr))
                throw std::runtime_error(".org must have numeric address: " + addrStr);
            orgAddr = parseNumber(addrStr);
            if (current == Section::Text)
                textStart = orgAddr;
            else if (current == Section::Data)
                dataStart = orgAddr;
            continue;
        }

        if (line.back() == ':') {
            std::string label = line.substr(0, line.size()-1);
            trim(label);

            size_t absoluteAddr = 0;
            if (current == Section::Text) {
                absoluteAddr = textStart + textSize;
                labelAddress[label] = absoluteAddr;
            }
            else if (current == Section::Data) {
                absoluteAddr = dataStart + dataSize;
                dataAddress[label] = absoluteAddr;
            }
            continue;
        }

        if (current == Section::Text) {
            textSize++;
        }
        else if (current == Section::Data) {
            dataSize++;
        }
    }

    dataStart = textStart + textSize;

    instructions.resize(textSize);
    dataSection.resize(dataSize);
    iss.clear();
    iss.seekg(0);
    current = Section::None;

    size_t textCursor = 0;
    size_t dataCursor = 0;

    while (std::getline(iss, line)) {
        stripComment(line);
        trim(line);
        if (line.empty()) continue;

        if (line == ".text") {
            current = Section::Text;
            textCursor = 0;
            continue;
        }
        else if (line == ".data") {
            current = Section::Data;
            continue;
        }

        if (line.size() > 4 && line.substr(0,4) == ".org") {
            std::string addrStr = line.substr(4);
            trim(addrStr);
            size_t orgAddr = 0;
            if (!isNumber(addrStr))
                throw std::runtime_error(".org must have numeric address: " + addrStr);
            orgAddr = parseNumber(addrStr);

            if (current == Section::Text) {
                textCursor = orgAddr - textStart;
            } else if (current == Section::Data) {
                dataCursor = orgAddr - dataStart;
            }
            continue;
        }

        if (line.back() == ':')
            continue;

        if (current == Section::Text) {
            std::istringstream ls(line);
            std::string mnemonic, operandStr;
            ls >> mnemonic >> operandStr;
            toLower(mnemonic);

            if (opcodeMap.find(mnemonic) == opcodeMap.end())
                throw std::runtime_error("Unknown opcode: " + mnemonic);

            uint8_t opcode = opcodeMap.at(mnemonic);
            uint32_t operand = 0;

            if (!operandStr.empty()) {
                if (isNumber(operandStr)) {
                    operand = parseNumber(operandStr);
                }
                else if (labelAddress.count(operandStr)) {
                    operand = labelAddress[operandStr];
                }
                else if (dataAddress.count(operandStr)) {
                    operand = dataAddress[operandStr];
                }
                else {
                    throw std::runtime_error("Unknown operand label: " + operandStr);
                }
            }

            instructions[textCursor] = {opcode, operand};
            textCursor++;
        }
        else if (current == Section::Data) {
            auto colonPos = line.find(':');
            if (colonPos == std::string::npos)
                throw std::runtime_error("Invalid data entry: " + line);

            std::string label = line.substr(0, colonPos);
            std::string valueStr = line.substr(colonPos + 1);
            trim(label);
            trim(valueStr);

            uint32_t value = 0;

            if (valueStr.size() >= 5 && valueStr.substr(0,5) == ".zero") {
                int count = std::stoi(valueStr.substr(5));
                if (count <= 0)
                    throw std::runtime_error("Invalid .zero count: " + valueStr);

                size_t index = dataCursor;
                dataAddress[label] = dataStart + index;
                dataCursor += count;
            }
            else if (isNumber(valueStr)) {
                value = parseNumber(valueStr);
                size_t index = dataCursor;
                if (dataSection.size() <= index)
                    dataSection.resize(index + 1);
                dataSection[index] = value;
                dataAddress[label] = dataStart + index;
                dataCursor++;
            }
            else if (valueStr.front() == '"' && valueStr.back() == '"') {
                std::string str = valueStr.substr(1, valueStr.size() - 2);
                std::vector<uint8_t> bytes;

                for (size_t i = 0; i < str.size(); ++i) {
                    if (str[i] == '\\') {
                        if (i + 1 >= str.size())
                            throw std::runtime_error("Invalid escape sequence in string: " + valueStr);
                        ++i;
                        switch (str[i]) {
                            case '0': bytes.push_back('\0'); break;
                            case 'n': bytes.push_back('\n'); break;
                            case 't': bytes.push_back('\t'); break;
                            case '\\': bytes.push_back('\\'); break;
                            case '"': bytes.push_back('"'); break;
                            default:
                                throw std::runtime_error("Unsupported escape sequence: \\" + std::string(1, str[i]));
                        }
                    } else {
                        bytes.push_back(static_cast<uint8_t>(str[i]));
                    }
                }

                size_t index = dataCursor;
                if (dataSection.size() < index + bytes.size())
                    dataSection.resize(index + bytes.size());
                for (size_t j = 0; j < bytes.size(); ++j) {
                    dataSection[index + j] = bytes[j];
                }
                dataAddress[label] = dataStart + index;
                dataCursor += bytes.size();
            }
            else if (valueStr.find(',') != std::string::npos) {
                std::vector<std::string> parts;
                std::stringstream ss(valueStr);
                std::string item;
                while (std::getline(ss, item, ',')) {
                    trim(item);
                    parts.push_back(item);
                }

                size_t index = dataCursor;
                dataAddress[label] = dataStart + index;

                for (const auto& part : parts) {
                    uint32_t value = 0;
                    if (isNumber(part)) {
                        value = parseNumber(part);
                    } else if (labelAddress.count(part)) {
                        value = labelAddress[part];
                    } else if (dataAddress.count(part)) {
                        value = dataAddress[part];
                    } else {
                        throw std::runtime_error("Unknown array value: " + part);
                    }

                    if (dataSection.size() <= dataCursor)
                        dataSection.resize(dataCursor + 1);
                    dataSection[dataCursor++] = value;
                }
            }
            else if (labelAddress.count(valueStr)) {
                value = labelAddress[valueStr];
                size_t index = dataCursor;
                if (dataSection.size() <= index)
                    dataSection.resize(index + 1);
                dataSection[index] = value;
                dataAddress[label] = dataStart + index;
                dataCursor++;
            }
            else if (dataAddress.count(valueStr)) {
                value = dataAddress[valueStr];
                size_t index = dataCursor;
                if (dataSection.size() <= index)
                    dataSection.resize(index + 1);
                dataSection[index] = value;
                dataAddress[label] = dataStart + index;
                dataCursor++;
            }
            else {
                throw std::runtime_error("Unknown data value: " + valueStr);
            }
        }
    }

    if (!labelAddress.count("_start"))
        throw std::runtime_error("Missing _start label");

    uint32_t startAddr = labelAddress["_start"];
    uint8_t jmpOpcode = opcodeMap.at("jmp");

    if (instructions.size() < 1)
        instructions.resize(1, {0,0});
}

void Binarizer::writeToFile(const std::string& filename) const {
    std::ofstream out(filename, std::ios::binary);
    if (!out.is_open()) throw std::runtime_error("Failed to open output file");

    if (labelAddress.find("_start") == labelAddress.end())
        throw std::runtime_error("Unable to find _start label");

    uint32_t codeSize = static_cast<uint32_t>(textStart + instructions.size());
    uint32_t dataSize = static_cast<uint32_t>(dataSection.size());

    out.put((codeSize >> 16) & 0xFF);
    out.put((codeSize >> 8) & 0xFF);
    out.put(codeSize & 0xFF);

    out.put((dataSize >> 16) & 0xFF);
    out.put((dataSize >> 8) & 0xFF);
    out.put(dataSize & 0xFF);

    size_t memSize = codeSize + dataSize;
    std::vector<uint32_t> mem(memSize, 0);

    uint32_t startAddr = labelAddress.at("_start");
    uint8_t jmpOpcode = opcodeMap.at("jmp");
    mem[0] = (jmpOpcode << 19) | (startAddr & 0x7FFFF);

    for (size_t i = 0; i < instructions.size(); i++) {
        uint32_t raw = (instructions[i].opcode << 19) | (instructions[i].operand & 0x7FFFF);
        mem[textStart + i] = raw;
    }

    for (size_t i = 0; i < dataSection.size(); i++) {
        uint32_t value = dataSection[i];
        if (value >= (1 << 24))
            throw std::runtime_error("Data value too big for 3 bytes");
        mem[dataStart + i] = value;
    }

    for (uint32_t val : mem) {
        out.put((val >> 16) & 0xFF);
        out.put((val >> 8) & 0xFF);
        out.put(val & 0xFF);
    }
}

void Binarizer::dump() {
    std::ifstream in("program.bin", std::ios::binary);
    if (!in.is_open())
        throw std::runtime_error("Failed to open binary file");

    uint8_t buf[3];

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read codeSize from header");

    uint32_t codeSize = (uint32_t(buf[0]) << 16) | (uint32_t(buf[1]) << 8) | uint32_t(buf[2]);
    std::cout << "HEADER: codeSize = 0x" << std::hex << codeSize << std::dec << "\n";

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read dataSize from header");

    uint32_t dataSize = (uint32_t(buf[0]) << 16) | (uint32_t(buf[1]) << 8) | uint32_t(buf[2]);
    std::cout << "HEADER: dataSize = 0x" << std::hex << dataSize << std::dec << "\n";

    int addr = 0;
    while (true) {
        if (!in.read(reinterpret_cast<char*>(buf), 3)) {
            if (in.eof()) break;
            throw std::runtime_error("Failed to read 3 bytes from file");
        }

        uint32_t raw = (uint32_t(buf[0]) << 16) | (uint32_t(buf[1]) << 8) | uint32_t(buf[2]);

        std::cout << std::setw(4) << std::setfill('0') << std::hex << addr << ": ";

        if (addr < static_cast<int>(codeSize)) {
            uint8_t opcode = raw >> 19;
            uint32_t operand = raw & 0x7FFFF;
            std::cout << "raw=0x" << std::setw(6) << std::setfill('0') << raw
                      << " opcode=0x" << std::hex << int(opcode)
                      << " operand=0x" << operand << std::dec << "\n";
        } else if (addr < static_cast<int>(codeSize + dataSize)) {
            std::cout << "DATA=0x" << std::uppercase << std::hex << raw << std::dec << "\n";
        } else {
            std::cout << "UNKNOWN=0x" << std::hex << raw << std::dec << "\n";
        }
        ++addr;
    }
}