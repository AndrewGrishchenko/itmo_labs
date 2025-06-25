#include "binarizer.h"

Binarizer::Binarizer() { }
Binarizer::~Binarizer() { }

void Binarizer::parse(const std::string& data) {
    std::istringstream iss(data);
    std::string line;
    size_t textAddr = 0;
    size_t dataAddr = 0;
    Section current = Section::None;

    while (std::getline(iss, line)) {
        stripComment(line);
        trim(line);
        if (line.empty()) continue;

        if (line == ".text") {
            current = Section::Text;
            continue;
        } else if (line == ".data") {
            current = Section::Data;
            continue;
        }

        if (line.back() == ':') {
            std::string label = line.substr(0, line.size() - 1);
            trim(label);
            if (current == Section::Text)
                labelAddress[label] = textAddr;
            else if (current == Section::Data)
                dataAddress[label] = dataAddr;
        } else {
            if (current == Section::Text)
                textAddr++;
            else if (current == Section::Data)
                dataAddr++;
        }
    }

    iss.clear();
    iss.seekg(0);
    current = Section::None;

    while (std::getline(iss, line)) {
        stripComment(line);
        trim(line);
        if (line.empty()) continue;

        if (line == ".text") {
            current = Section::Text;
            continue;
        } else if (line == ".data") {
            current = Section::Data;
            continue;
        }

        if (line.back() == ':') continue;

        if (current == Section::Text) {
            std::istringstream ls(line);
            std::string mnemonic, operandStr;
            ls >> mnemonic >> operandStr;
            toLower(mnemonic);

            if (opcodeMap.find(mnemonic) == opcodeMap.end())
                throw std::runtime_error("Unknown opcode " + mnemonic);
            
            uint8_t opcode = opcodeMap.at(mnemonic);
            uint32_t operand = 0;

            if (!operandStr.empty()) {
                if (isNumber(operandStr))
                    operand = parseNumber(operandStr);
                else if (labelAddress.count(operandStr))
                    operand = static_cast<uint32_t>(labelAddress.at(operandStr));
                else if (dataAddress.count(operandStr))
                    operand = static_cast<uint32_t>(dataAddress.at(operandStr));
                else
                    throw std::runtime_error("Unknown operand at label " + operandStr);
            }

            if (operand >= (1 << 19))
                throw std::runtime_error("Operand out of 19-bit range " + std::to_string(operand));

            instructions.push_back({opcode, operand});
        } else if (current == Section::Data) {
            auto colonPos = line.find(':');
            if (colonPos == std::string::npos)
                throw std::runtime_error("Invalid data entry (missing colon): " + line);

            std::string label = line.substr(0, colonPos);
            std::string valueStr = line.substr(colonPos + 1);

            trim(label);
            trim(valueStr);

            if (label.empty() || valueStr.empty())
                throw std::runtime_error("Invalid data format in line: " + line);

            // if (!isNumber(valueStr))
            //     throw std::runtime_error("Invalid numeric value in data entry: " + line);

            // uint32_t value = static_cast<uint32_t>(parseNumber(valueStr));
            // if (value >= (1 << 24))
            //     throw std::runtime_error("Data value too large (max 24 bits): " + std::to_string(value));

            // dataAddress[label] = textAddr + dataSection.size();
            // dataSection.push_back(value);

            dataAddress[label] = textAddr + dataSection.size();

            std::stringstream ss(valueStr);
            std::string item;
            while (std::getline(ss, item, ',')) {
                trim(item);
                if (!isNumber(item))
                    throw std::runtime_error("Invalid number in data entry: " + item);

                uint32_t value = static_cast<uint32_t>(parseNumber(item));
                if (value >= (1 << 24))
                    throw std::runtime_error("Data value too large (max 24 bits): " + std::to_string(value));

                dataSection.push_back(value);
            }
        }
    }
}

void Binarizer::writeToFile(const std::string& filename) const {
    std::ofstream out(filename, std::ios::binary);
    if (!out.is_open()) throw std::runtime_error("Failed to open output file");

    //header: .text size
    uint32_t textSize = static_cast<uint32_t>(instructions.size());
    if (textSize >= (1 << 24)) //TODO: idk
        throw std::runtime_error(".text section too large");

    if (labelAddress.find("_start") == labelAddress.end())
        throw std::runtime_error("Unable to find _start label");
    
    uint32_t entryPoint = static_cast<uint32_t>(labelAddress.at("_start"));
    if (entryPoint >= textSize)
        throw std::runtime_error("_start is outside of .text section");

    out.put((textSize >> 16) & 0xFF);
    out.put((textSize >> 8) & 0xFF);
    out.put(textSize & 0xFF);

    out.put((entryPoint >> 16) & 0xFF);
    out.put((entryPoint >> 8) & 0xFF);
    out.put(entryPoint & 0xFF);

    //.text
    for (const auto& instr : instructions) {
        uint32_t raw = (instr.opcode << 19) | (instr.operand & 0x7FFFF);
        out.put((raw >> 16) & 0xFF);
        out.put((raw >> 8) & 0xFF);
        out.put(raw & 0xFF);
    }

    //.data
    for (uint32_t value : dataSection) {
        if (value >= (1 << 24))
            throw std::runtime_error("Data value too big for 3 bytes");
        
        out.put((value >> 16) & 0xFF);
        out.put((value >> 8) & 0xFF);
        out.put(value & 0xFF);
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

void Binarizer::dump() {
    std::ifstream in("program.bin", std::ios::binary);
    if (!in.is_open())
        throw std::runtime_error("Failed to open binary file");

    uint8_t buf[3];

    //header
    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read header\n");

    uint32_t textSize = (buf[0] << 16) | (buf[1] << 8) | buf[2];
    std::cout << "HEADER: textSize = " << textSize << "\n";

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read header\n");
    
    uint32_t entryPoint = (buf[0] << 16) | (buf[1] << 8) | buf[2];
    std::cout << "HEADER: entryPoint = 0x" << std::hex << entryPoint << std::dec << "\n";

    int addr = 0;
    while (in.read(reinterpret_cast<char*>(buf), 3)) {
        uint32_t raw = (buf[0] << 16) | (buf[1] << 8) | buf[2];
        std::cout << std::setw(4) << std::setfill('0') << std::hex << addr << ": ";

        std::cout << "raw=0x" << std::hex << raw << std::dec << " ";
        if (addr < textSize) {
            uint8_t opcode = raw >> 19;
            uint32_t operand = raw & 0x7FFFF;
            std::cout << "opcode=0x" << std::hex << +opcode << " operand=0x" << operand << "\n";
        } else {
            std::cout << "DATA=0x" << std::uppercase << std::hex << raw << "\n";
        }

        ++addr;
    }
}