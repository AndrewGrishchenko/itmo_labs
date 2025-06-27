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

    
    // instructions.resize(textStart + textSize, {0,0});
    instructions.resize(textSize);
    dataSection.resize(dataSize);
    iss.clear();
    iss.seekg(0);
    current = Section::None;

    size_t textCursor = 0;
    size_t dataCursor = 0;

    std::cout << "dataStart: 0x" << std::hex << dataStart << std::dec << "\n";
    std::cout << "textStart: 0x" << std::hex << textStart << std::dec << "\n";
    std::cout << "instruction size " << instructions.size() << "\n";
    std::cout << "data size " << dataSection.size() << "\n";
    
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
            // dataCursor = 0;
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

            // size_t absoluteAddr = textStart + textCursor;
            // if (instructions.size() <= absoluteAddr)
                // instructions.resize(absoluteAddr + 1, {0,0});

            // instructions[absoluteAddr] = {opcode, operand};
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
                // if (dataSection.size() < index + count)
                //     dataSection.resize(index + count, 0);
                // dataCursor += count;
                dataAddress[label] = dataStart + index;
                std::cout << "dataCursor = 0x" << std::hex << dataCursor + dataStart << " + " << std::dec << count << " = ";
                dataCursor += count;
                std::cout << std::hex << "0x" << dataCursor + dataStart << std::dec << "\n";

            }
            else if (isNumber(valueStr)) {
                value = parseNumber(valueStr);
                size_t index = dataCursor;
                if (dataSection.size() <= index)
                    dataSection.resize(index + 1);
                // std::cout << "dataSection at 0x" << std::hex << dataStart + index << std::dec << std::endl;
                dataSection[index] = value;
                dataAddress[label] = dataStart + index;
                dataCursor++;
            }
            else if (valueStr.front() == '"' && valueStr.back() == '"') {
                std::string str = valueStr.substr(1, valueStr.size() - 2); // убрать кавычки
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
    // instructions[0] = {jmpOpcode, startAddr};
    std::cout << "JUMP BLYAT 0x" << std::hex << static_cast<int>(instructions[0].opcode) << " 0x" << instructions[0].operand << "\n";

    std::cout << "Sections start addresses:\n";
    std::cout << " textStart = 0x" << std::hex << textStart << "\n";
    std::cout << " dataStart = 0x" << dataStart << std::dec << "\n";

    std::cout << "Labels:\n";
    for (auto& [name, addr] : labelAddress)
        std::cout << " " << name << " = 0x" << std::hex << addr << std::dec << "\n";

    std::cout << "Instructions (" << instructions.size() << "):\n";
    for (size_t i = 0; i < instructions.size(); i++)
        std::cout << std::hex << i << ": opcode=" << (int)instructions[i].opcode << " operand=" << instructions[i].operand << std::dec << "\n";

    std::cout << "Data (" << dataSection.size() << "):\n";
    for (size_t i = 0; i < dataSection.size(); i++)
        std::cout << std::hex << (i) << ": " << dataSection[i] << std::dec << "\n";
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
    std::cout << "jmp opcode: 0x" << std::hex << static_cast<int>(jmpOpcode) << std::dec << "\n";
    mem[0] = (jmpOpcode << 19) | (startAddr & 0x7FFFF);

    //.text
    // for (const auto& instr : instructions) {
    //     uint32_t raw = (instr.opcode << 19) | (instr.operand & 0x7FFFF);
    //     out.put((raw >> 16) & 0xFF);
    //     out.put((raw >> 8) & 0xFF);
    //     out.put(raw & 0xFF);
    // }
    for (size_t i = 0; i < instructions.size(); i++) {
        uint32_t raw = (instructions[i].opcode << 19) | (instructions[i].operand & 0x7FFFF);
        mem[textStart + i] = raw;
        // std::cout << "at address 0x" << std::hex << (textStart + i) << std::dec << "\n";
        // std::cout << "raw instr: 0x" << std::hex << raw << std::dec << "\n";
        // std::cout << "instr opcode 0x" << std::hex << instructions[i].opcode << " operand 0x" << instructions[i].operand << "\n";
    }

    //.data
    // for (uint32_t value : dataSection) {
    //     if (value >= (1 << 24))
    //         throw std::runtime_error("Data value too big for 3 bytes");
        
    //     out.put((value >> 16) & 0xFF);
    //     out.put((value >> 8) & 0xFF);
    //     out.put(value & 0xFF);
    // }
    for (size_t i = 0; i < dataSection.size(); i++) {
        uint32_t value = dataSection[i];
        if (value >= (1 << 24))
            throw std::runtime_error("Data value too big for 3 bytes");
        mem[dataStart + i] = value;
        // std::cout << "at address 0x" << std::hex << (dataStart + i) << std::dec << "\n";
        // std::cout << "raw data: 0x" << std::hex << value << std::dec << "\n";
    }

    size_t i = 0;
    for (uint32_t val : mem) {
        out.put((val >> 16) & 0xFF);
        out.put((val >> 8) & 0xFF);
        out.put(val & 0xFF);
        std::cout << "MEM[0x" << std::hex << i << "] = 0x" << val << std::dec << "\n";
        i++;
    }
}

std::string Binarizer::toAsm() const {
    std::stringstream oss;

    if (!dataSection.empty()) {
        oss << ".data\n";

        std::unordered_map<size_t, std::string> dataAddrToLabel;
        for (const auto& [label, addr] : dataAddress) {
            dataAddrToLabel[addr] = label;
        }

        for (size_t i = 0; i < dataSection.size(); ++i) {
            if (dataAddrToLabel.count(i))
                oss << "   " << dataAddrToLabel[i] << ":";
            oss << " 0x" << std::uppercase << std::hex << dataSection[i] << "\n";
        }
    }

    if (!instructions.empty()) {
        oss << "\n.text\n";

        std::unordered_map<size_t, std::string> textAddrToLabel;
        for (const auto& [label, addr] : labelAddress) {
            textAddrToLabel[addr] = label;
        }

        for (size_t i = 0; i < instructions.size(); ++i) {
            if (textAddrToLabel.count(i))
                oss << textAddrToLabel[i] << ":\n";

            const auto& instr = instructions[i];

            std::string name = "UNKNOWN";
            for (const auto& [mnemonic, opcode] : opcodeMap) {
                if (opcode == instr.opcode) {
                    name = mnemonic;
                    break;
                }
            }

            oss << "   " << name;
            if (noOperandMnemonics.find(name) == noOperandMnemonics.end()) {
                oss << " 0x" << std::uppercase << std::hex << instr.operand;
            }
            oss << "\n";
        }
    }

    return oss.str();
}

// void Binarizer::dump() {
//     std::ifstream in("program.bin", std::ios::binary);
//     if (!in.is_open())
//         throw std::runtime_error("Failed to open binary file");

//     uint8_t buf[3];

//     //header
//     if (!in.read(reinterpret_cast<char*>(buf), 3))
//         throw std::runtime_error("Failed to read header\n");

//     uint32_t codeSize = (buf[0] << 16) | (buf[1] << 8) | buf[2];
//     std::cout << "HEADER: codeSize = 0x" << std::hex << codeSize << std::dec << "\n";

//     if (!in.read(reinterpret_cast<char*>(buf), 3))
//         throw std::runtime_error("Failed to read header\n");
    
//     uint32_t dataSize = (buf[0] << 16) | (buf[1] << 8) | buf[2];
//     std::cout << "HEADER: dataSize = 0x" << std::hex << dataSize << std::dec << "\n";

//     int addr = 0;
//     while (in.read(reinterpret_cast<char*>(buf), 3)) {
//         uint32_t raw = (buf[0] << 16) | (buf[1] << 8) | buf[2];
//         std::cout << std::setw(4) << std::setfill('0') << std::hex << addr << ": ";

//         std::cout << "raw=0x" << std::hex << raw << std::dec << " ";
//         if (addr < codeSize) {
//             uint8_t opcode = raw >> 19;
//             uint32_t operand = raw & 0x7FFFF;
//             std::cout << "opcode=0x" << std::hex << +opcode << " operand=0x" << operand << "\n";
//         } else {
//             std::cout << "DATA=0x" << std::uppercase << std::hex << raw << "\n";
//         }

//         ++addr;
//     }
// }

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