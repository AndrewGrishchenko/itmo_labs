#ifndef _PROCESSOR_MODEL_H
#define _PROCESSOR_MODEL_H

#include <cstdint>
#include <cstddef>
#include <string>
#include <fstream>
#include <array>
#include <vector>
#include <functional>

#include <iostream>
#include <iomanip>

constexpr size_t MEM_SIZE = 1 << 19;

class Memory {
    public:
        Memory() {
            reset();
        }
        ~Memory() { }

        void reset() {
            data.fill(0);
        }

        void write(size_t address, uint32_t value) {
            if (address >= MEM_SIZE)
                throw std::out_of_range("Memory write out of bounds");
            data[address] = value;
        }

        uint32_t read(size_t address) const {
            if (address >= MEM_SIZE)
                throw std::out_of_range("Memory read out of bounds");
            return data[address];
        }

        uint32_t& operator[](size_t address) {
            if (address >= MEM_SIZE)
                throw std::out_of_range("Memory access out of bounds");
                
            return data[address];
        }

        const uint32_t& operator[](size_t address) const {
            if (address >= MEM_SIZE)
                throw std::out_of_range("Memory access out of bounds");
            return data[address];
        }
    private:
        std::array<uint32_t, MEM_SIZE> data;
        
        uint32_t memory[MEM_SIZE] = {0};
};

class ALU {
    public:
        ALU() { }
        ~ALU() { }

        enum class Operation {
            ADD,
            SUB,
            MUL,
            DIV,
            REM,
            INC,
            DEC,
            NOT,
            AND,
            OR,
            XOR,
            SHL,
            SHR,
            NOP
        };

        std::string opStr(Operation op) {
            switch (op) {
                case Operation::ADD:
                    return "ADD";
                case Operation::SUB:
                    return "SUB";
                case Operation::MUL:
                    return "MUL";
                case Operation::DIV:
                    return "DIV";
                case Operation::REM:
                    return "REM";
                case Operation::INC:
                    return "INC";
                case Operation::DEC:
                    return "DEC";
                case Operation::NOT:
                    return "NOT";
                case Operation::AND:
                    return "AND";
                case Operation::OR:
                    return "OR";
                case Operation::XOR:
                    return "XOR";
                case Operation::SHL:
                    return "SHL";
                case Operation::SHR:
                    return "SHR";
                case Operation::NOP:
                    return "NOP";
                default:
                    return "unknown";
            }
        }

        void setLeftInputGetter(std::function<uint32_t&()> getter) {
            leftGetter = std::move(getter);
        }

        void setRightInputGetter(std::function<uint32_t&()> getter) {
            rightGetter = std::move(getter);
        }

        void perform(Operation operation, bool writeFlags = false) {
            uint32_t left = leftGetter();
            uint32_t right = rightGetter();
            uint32_t value = 0;
            bool N = false, Z = false, V = false, C = false;
            
            switch (operation) {
                case Operation::ADD: {
                    uint64_t tmp = static_cast<uint64_t>(left) + right;
                    value = tmp & 0xFFFFFFFF;
                    C = tmp > 0xFFFFFFFF;
                    V = ((left ^ value) & (right ^ value)) >> 31;
                    break;
                }
                case Operation::SUB: {
                    uint64_t tmp = static_cast<uint64_t>(left) - right;
                    value = tmp & 0xFFFFFFFF;
                    C = left >= right;
                    V = ((left ^ right) & (left ^ value)) >> 31;
                    // V = (((left ^ right) & (left ^ value)) & 0x80000000) != 0;
                    break;
                }
                case Operation::MUL:
                    value = left * right;
                    C = false;
                    V = false;
                    //TODO: think about C & V
                    break;
                case Operation::DIV:
                    value = right ? left / right : 0;
                    break;
                case Operation::REM:
                    value = right ? left % right : 0;
                    break;
                case Operation::INC:
                    value = left + right + 1;
                    break;
                case Operation::DEC:
                    value = left + right - 1;
                    break;
                case Operation::NOT:
                    value = ~(left + right);
                    break;
                case Operation::AND:
                    value = left & right;
                    break;
                case Operation::OR:
                    value = left | right;
                    break;
                case Operation::XOR:
                    value = left ^ right;
                    break;
                case Operation::SHL:
                    value = left << right;
                    C = (left >> (32 - right)) & 1;
                    break;
                case Operation::SHR:
                    value = left >> right;
                    C = (left >> (right - 1)) & 1;
                    break;
                case Operation::NOP:
                    value = left + right;
                    break;
            }

            N = value >> 31;
            Z = value == 0;

            // result = { value, N, Z, V, C };
            result = value;
            if (writeFlags) {
                *flagRefs[0] = N;
                *flagRefs[1] = Z;
                *flagRefs[2] = V;
                *flagRefs[3] = C;
            }

            std::cout << "ALU: 0x" << std::hex << left << " " << opStr(operation) << " 0x" << right << " = 0x" << value << std::dec << "\n";
            std::cout << "NZVC: " << (*flagRefs[0] ? 1 : 0) << (*flagRefs[1] ? 1 : 0) << (*flagRefs[2] ? 1 : 0) << (*flagRefs[3] ? 1 : 0) << "\n";

            // std::cout << "ALU PERFORMED WITH RESULT = " << result << "\n";
        }

        // struct Result {
        //     uint32_t value;
        //     bool N, Z, V, C;
        // };

        // Result getResult() const {
        //     return result;
        // }

        // uint32_t& getResultValueRef() {
        //     return result.value;
        // }

        uint32_t getResult() const {
            return result;
        }

        uint32_t& getResultRef() {
            return result;
        }

        void setFlagRefs(bool& n, bool& z, bool& v, bool& c) {
            flagRefs[0] = &n;
            flagRefs[1] = &z;
            flagRefs[2] = &v;
            flagRefs[3] = &c;
        }

    private:
        std::function<uint32_t&()> leftGetter;
        std::function<uint32_t&()> rightGetter;
        // Result result;
        uint32_t result = 0;
        bool* flagRefs[4];
        //TODO: smth to do w dummies
};

class MUX {
    public:
        MUX() { }
        ~MUX() { }

        void addInput(uint32_t& value) {
            inputs.emplace_back(value);
        }

        void select(size_t index) {
            if (index >= inputs.size()) throw std::out_of_range("MUX select out of range");
            selectedIndex = index;
            // std::cout << "MUX SELECTED " << index << " (" << inputs[selectedIndex].get() << ")\n";
        }

        uint32_t& getSelected() const {
            return inputs[selectedIndex].get();
        }

        std::function<uint32_t&()> makeGetter() {
            return [this]() -> uint32_t& {
                return this->getSelected();
            };
        }
    private:
        std::vector<std::reference_wrapper<uint32_t>> inputs;
        size_t selectedIndex = 0;
};

class Registers {
    public:
        Registers() {
            reset();
        }
        ~Registers() { }

        enum RegName : size_t {
            ACC, IR, AR, DR, IP, SP,
            REG_COUNT
        };

        void reset() {
            regs.fill(0);
            regs[SP] = 0x7FFFF;
            N = false;
            Z = false;
            V = false;
            C = false;
        }

        uint32_t get(RegName reg) const {
            if (reg >= REG_COUNT)
                throw std::out_of_range("Invalid register");
            return regs[reg];
        }

        uint32_t& getRef(RegName reg) {
            if (reg >= REG_COUNT)
                throw std::out_of_range("Invalid register");
            return regs[reg];
        }

        void set(RegName reg, uint32_t value) {
            if (reg >= REG_COUNT)
                throw std::out_of_range("Invalid register");
            regs[reg] = value;
        }

        bool getN() const { return N; }
        bool& getNRef() { return N; }
        void setN(bool val) { N = val; }

        bool getZ() const { return Z; }
        bool& getZRef() { return Z; }
        void setZ(bool val) { Z = val; }

        bool getV() const { return V; }
        bool& getVRef() { return V; }
        void setV(bool val) { V = val; }

        bool getC() const { return C; }
        bool& getCRef() { return C; }
        void setC(bool val) { C = val; }

        void dump() const {
            static const char* names[] = {"ACC", "IR", "AR", "DR", "IP", "SP"};
            for (size_t i = 0; i < REG_COUNT; i++) {
                std::cout << names[i] << " = 0x" << std::hex << regs[i] << std::dec << "\n";
            }
            std::cout << "Flags: N=" << N << " Z=" << Z << " V=" << V << " C=" << C << "\n";
        }

    private:
        std::array<uint32_t, REG_COUNT> regs;
        bool N = false, Z = false, V = false, C = false;
};

class LatchRouter {
    public:
        LatchRouter() { }
        ~LatchRouter() { }

        void setInput(std::reference_wrapper<uint32_t> value) {
            input = value;
        }

        void addOutput(std::reference_wrapper<uint32_t> output) {
            outputs.emplace_back(output, false);
        }

        void setLatch(size_t index, bool enabled) {
            if (index >= outputs.size())
                throw std::out_of_range("Latch index out of range");
            outputs[index].second = enabled;
        }

        void setLatches(const std::vector<bool>& latchStates) {
            if (latchStates.size() != outputs.size())
                throw std::invalid_argument("Latch state count doesn't match output count");
            
            for (size_t i = 0; i < outputs.size(); i++) {
                outputs[i].second = latchStates[i];
            }
        }

        // void setLatch(size_t index, bool enabled) {
        //     if (index >= outputs.size())
        //         throw std::out_of_range("Latch index out of range");
        //     outputs[index].second = enabled;
        // }

        // void setLatches(const std::vector<int>& latchStates) {
        //     if (latchStates.size() != outputs.size())
        //         throw std::invalid_argument("Latch state count doesn't match output count");
            
        //     for (size_t i = 0; i < outputs.size(); i++) {
        //         *outputs[i].second = latchStates[i] == 1 ? true : false;
        //     }
        // }

        // void setLatches(const std::unordered_map<Registers::RegName, bool>& latchStates) {
        //     for (const auto& [reg, enabled] : latchStates) {
        //         setLatch(reg, enabled);
        //     }
        // }
        //TODO: think

        // void setLatches(const std::unordered_map<Registers::RegName, int>& latchStates) {
        //     for (const auto& [reg, enabled] : latchStates) {
        //         setLatch(reg, enabled);
        //     }
        // }

        // void propagate() {
        //     for (auto& [ref, latch] : outputs) {
        //         if (*latch)
        //             ref.get() = value;
        //     }
        // }

        void propagate() {
            // std::cout << "PROPAGATING LATCHES:\n";
            for (auto& [ref, latch] : outputs) {
                if (latch) {
                    // std::cout << ref.get() << " = " << input.get() << "\n";
                    ref.get() = input.get();
                }
                    
            }
        }

    private:
        std::reference_wrapper<uint32_t> input = dummyInput;
        std::vector<std::pair<std::reference_wrapper<uint32_t>, bool>> outputs;
        // std::unordered_map<Registers::RegName, std::pair<std::reference_wrapper<uint32_t>, bool>> outputs;
        uint32_t dummyInput = 0;
};

class ProcessorModel {
    public:
        ProcessorModel();
        ~ProcessorModel();

        void loadBinary(const std::string& filename);
        void process();
    private:
        size_t textSize = 0;
        size_t dataStart = 0;
        size_t dataSize = 0;
        uint32_t entryPoint = 0;

        uint32_t zero = 0;
        uint8_t opcode = 0;
        uint32_t operand = 0;

        bool halted = false;
        bool binaryLoaded = false;
        size_t tickCount = 0;
        size_t microstep = 0;

        Memory memory;
        Registers registers;
        ALU alu;
        MUX mux1, mux2;
        LatchRouter latchRouter;

        enum class CPUState {
            FetchAR,
            FetchIR,
            Decode,
            IncrementIP,
            Halt
        };

        CPUState state = CPUState::FetchAR;
        void tick();

        void updateOperand();

        enum Opcode : uint8_t {
            OP_ADD  = 0b00000,
            OP_SUB  = 0b00001,
            OP_DIV  = 0b00010,
            OP_MUL  = 0b00011,
            OP_REM  = 0b00100,
            OP_INC  = 0b00101,
            OP_DEC  = 0b00110,
            OP_NOT  = 0b00111,
            OP_CLA  = 0b01000,
            OP_JMP  = 0b01001,
            OP_JZ   = 0b01010,
            OP_JNZ  = 0b01011,
            OP_JG   = 0b01100,
            OP_JGE  = 0b01101,
            OP_JL   = 0b01110,
            OP_JLE  = 0b01111,
            OP_PUSH = 0b10000,
            OP_POP  = 0b10001,
            OP_LD   = 0b10010,
            OP_LDA  = 0b10011,
            OP_LDI  = 0b10100,
            OP_ST   = 0b10101,
            OP_STA  = 0b10110,
            OP_IN   = 0b10111,
            OP_OUT  = 0b11000,
            OP_CALL = 0b11001,
            OP_RET  = 0b11010,
            OP_HALT = 0b11011
        };
        

        bool instructionDone = false;
        void instructionTick();

        std::string stateStr() {
            switch (state) {
                case CPUState::FetchAR:
                    return "FetchAR";
                case CPUState::FetchIR:
                    return "FetchIR";
                case CPUState::Decode:
                    return "Decode";
                case CPUState::IncrementIP:
                    return "IncrementIP";
                case CPUState::Halt:
                    return "Halt";
                default:
                    return "";
            }
        }
        void memDump();
        void allDump();
};

#endif