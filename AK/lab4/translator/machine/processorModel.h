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

#include "configParser.hpp"

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

        uint32_t& atRef(const size_t& addressRef) {
            if (addressRef >= MEM_SIZE)
                throw std::runtime_error("Memory access out of bounds");
            return data[addressRef];
        }

        const uint32_t& atRef(const size_t& addressRef) const {
            if (addressRef >= MEM_SIZE)
                throw std::runtime_error("Memory access out of bounds");
            return data[addressRef];
        }

        std::function<uint32_t&()> makeGetterAtRef(const size_t& addressRef) {
            return [this, &addressRef]() -> uint32_t& {
                return this->atRef(addressRef);
            };
        }

        std::function<uint32_t&()> makeDynamicGetter(std::function<uint32_t&()> addressGetter) {
            return [this, addressGetter]() -> uint32_t& {
                return this->atRef(addressGetter());
            };
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

        void setOperation(Operation operation) {
            this->operation = operation;
        }

        void setWriteFlags(bool writeFlags) {
            this->writeFlags = writeFlags;
        }

        void perform() {
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
                    break;
                }
                case Operation::MUL:
                    value = left * right;
                    C = false;
                    V = false;
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

            result = value;
            if (writeFlags) {
                *flagRefs[0] = N;
                *flagRefs[1] = Z;
                *flagRefs[2] = V;
                *flagRefs[3] = C;
            }
        }

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
        
        Operation operation;
        uint32_t result = 0;
        bool* flagRefs[4];
        bool writeFlags;
};

class MUX {
    public:
        MUX() { }
        ~MUX() { }

        void addInput(uint32_t& value) {
            inputs.emplace_back(value);
        }

        void replaceInput(size_t index, uint32_t& value) {
            if (index >= inputs.size())
                throw std::out_of_range("MUX replace input out of range");
            inputs[index] = value;
        }

        void select(size_t index) {
            if (index >= inputs.size()) throw std::out_of_range("MUX select out of range");
            selectedIndex = index;
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

class Latch {
    public:
        Latch() {
            sourceGetter = [this]() -> uint32_t& {
                return source.get();
            };
            targetGetter = [this]() -> uint32_t& {
                return this->target.get();
            };
        }
        ~Latch() { }

        void setSource(std::reference_wrapper<uint32_t> source) {
            this->source = source;
            sourceGetter = [this]() -> uint32_t& {
                return this->source.get();
            };
        }

        void setSourceGetter(std::function<uint32_t&()> sourceGetter) {
            this->sourceGetter = std::move(sourceGetter);
        }

        void setTarget(std::reference_wrapper<uint32_t> target) {
            this->target = target;
            targetGetter = [this]() -> uint32_t& {
                return this->target.get();
            };
        }

        void setTargetGetter(std::function<uint32_t&()> targetGetter) {
            this->targetGetter = std::move(targetGetter);
        }

        void setEnabled(bool enabled) {
            this->enabled = enabled;
        }

        void propagate() {
            if (enabled)
                targetGetter() = sourceGetter();
        }

    private:
        std::reference_wrapper<uint32_t> source = dummyInput;
        std::reference_wrapper<uint32_t> target = dummyInput;
        
        std::function<uint32_t&()> sourceGetter;
        std::function<uint32_t&()> targetGetter;
        
        bool enabled = false;
        uint32_t dummyInput = 0;
};

class LatchRouter {
    public:
        LatchRouter() { }
        ~LatchRouter() { }

        void addLatch(Latch& latch) {
            latches.emplace_back(latch);
        }

        template<typename... Latches>
        void setLatches(Latches&... ls) {
            latches = { std::ref(ls)... };
        }

        void setLatchState(size_t index, bool enabled) {
            if (index >= latches.size())
                throw std::invalid_argument("Latch index out of range");
            latches[index].get().setEnabled(enabled);
        }

        void setLatchStates(const std::vector<bool>& latchStates) {
            if (latchStates.size() != latches.size())
                throw std::invalid_argument("Latch state count doesn't match output count");

            for (size_t i = 0; i < latches.size(); i++)
                latches[i].get().setEnabled(latchStates[i]);
        }

        void propagate() {
            for (auto& latchRef : latches)
                latchRef.get().propagate();
        }

    private:
        std::vector<std::reference_wrapper<Latch>> latches;
};

class InterruptHandler {
    public:
        InterruptHandler() { }
        ~InterruptHandler() { }

        void connect(Latch& latchALU_SPC, Latch& latchSPC_PC, Latch& latchVec_PC) {
            this->latchALU_SPC = &latchALU_SPC;
            this->latchSPC_PC = &latchSPC_PC;
            this->latchVec_PC = &latchVec_PC;
            
            this->latchVec_PC->setSource(inputVec);
        }

        enum class IRQType {
            NONE = 0,
            IO_INPUT = 1
        };

        void setIRQ(IRQType irq) { 
            if (!ipc)
                this->irq = irq;
        }

        bool& getIERef() { return ie; }
        bool& getIPCRef() { return ipc; }

        uint32_t& getSPCRef() { return SPC; }

        void setVectorTable(uint32_t defaultVec, uint32_t inputVec) {
            this->defaultVec = defaultVec;
            this->inputVec = inputVec;
        }

        bool shouldInterrupt() const {
            return ie && irq != IRQType::NONE && !ipc;
        }

        bool isEnteringInterrupt() {
            return intState == InterruptState::Executing;
        }

        void step();

    private:
        IRQType irq = IRQType::NONE;
        bool ie = false;
        bool ipc = false;

        uint32_t dummyInput = 0;
        std::reference_wrapper<uint32_t> PC = dummyInput;

        uint32_t defaultVec, inputVec;
        
        Latch* latchALU_SPC = nullptr;
        Latch* latchSPC_PC = nullptr;
        Latch* latchVec_PC = nullptr;
        uint32_t SPC;

        enum class InterruptState {
            SavingPC,
            Executing,
            Restoring
        };
        InterruptState intState = InterruptState::SavingPC;
};

class IOSimulator {
    public:
        IOSimulator() { }
        ~IOSimulator() { }

        void connect(InterruptHandler& interruptHandler, Memory& memory) {
            this->interruptHandler = &interruptHandler;
            this->memory = &memory;
        }

        void connectOutput(std::ostringstream& outputData) {
            this->outputData = &outputData;
        }

        struct IOScheduleEntry {
            size_t tick;
            int token;
        };

        void addInput(IOScheduleEntry entry) {
            inputSchedule.push_back(entry);
        }

        void setMixedOutput(bool mixed) {
            this->mixed = mixed;
        }

        void check(size_t tick) {
            for (const auto& entry : inputSchedule) {
                if (entry.tick == tick) {
                    interruptHandler->setIRQ(InterruptHandler::IRQType::IO_INPUT);
                    memory->write(input_address, entry.token);

                    if (!mixed) break;

                    if (state != 2) {
                        if (state == 1)
                            *outputData << '\n';
                        *outputData << "> ";
                        state = 2;
                    }

                    *outputData << static_cast<char>(entry.token);
                    if (entry.token == '\n')
                        state = 0;
                }
            }

            if (memory->read(output_address) != 0x0) {
                char token = memory->read(output_address);
                outputSchedule.push_back({tick, static_cast<int>(token)});
                memory->write(output_address, 0);

                if (!mixed) {
                    *outputData << token;
                    return;
                }

                if (state != 1) {
                    if (state == 2)
                        *outputData << '\n';
                    *outputData << "< ";
                    state = 1;
                }

                *outputData << token;
                if (token == '\n')
                    state = 0;
            }
        }

        std::string getTokenOutput() {
            std::ostringstream data;
            data << "[";
            for (size_t i = 0; i < outputSchedule.size(); i++) {
                data << "(" << outputSchedule[i].tick << ", '";
                char token = static_cast<char>(outputSchedule[i].token);
                if (token == '\n')
                    data << "\\n";
                else if (token == '\t')
                    data << "\\t";
                else
                    data << token;
                data << "')";
                if (i < outputSchedule.size() - 1) data << ", ";
            }
            data << "]";
            return data.str();
        }

    private:
        InterruptHandler* interruptHandler = nullptr;
        Memory* memory = nullptr;

        std::vector<IOScheduleEntry> inputSchedule;
        std::vector<IOScheduleEntry> outputSchedule;
        
        const size_t input_address = 0x10;
        const size_t output_address = 0x11;

        std::ostringstream* outputData = nullptr;
        bool mixed = false;
        int state = 0;
};

class CU {
    public:
        CU() { }
        ~CU() { }

        void connect(InterruptHandler& interruptHandler, MUX& mux1, MUX& mux2, ALU& alu, LatchRouter& latchRouter, Latch& latchMEM_IR, Latch& latchMEM_DR, Latch& latchDR_MEM) {
            this->interruptHandler = &interruptHandler;
            this->mux1 = &mux1;
            this->mux2 = &mux2;
            this->alu = &alu;
            this->latchRouter = &latchRouter;
            this->latchMEM_IR = &latchMEM_IR;
            this->latchMEM_DR = &latchMEM_DR;
            this->latchDR_MEM = &latchDR_MEM;
            
            this->mux1->replaceInput(2, operand);
        }

        void setLog(std::ostringstream& logData) {
            this->logData = &logData;
        }

        void setIRInput(const uint32_t& IR) {
            this->IR = &IR;
        }

        void setFlagsInput(const bool& N, const bool& Z, const bool& V, const bool& C) {
            this->N = &N;
            this->Z = &Z;
            this->V = &V;
            this->C = &C;
        }

        bool isHalted() {
            return halted;
        }

        void decode();
    
    private:
        InterruptHandler* interruptHandler = nullptr;

        MUX* mux1 = nullptr;
        MUX* mux2 = nullptr;
        ALU* alu = nullptr;
        LatchRouter* latchRouter = nullptr;
        Latch* latchMEM_IR = nullptr;
        Latch* latchMEM_DR = nullptr;
        Latch* latchDR_MEM = nullptr;

        const uint32_t* IR = nullptr;
        const bool* N = nullptr;
        const bool* Z = nullptr;
        const bool* V = nullptr;
        const bool* C = nullptr;

        enum class CPUState {
            FetchAR,
            FetchIR,
            Decode,
            IncrementIP,
            Halt
        };

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

        CPUState state = CPUState::FetchAR;

        bool instructionDone = false;
        void instructionTick();

        size_t microstep = 0;

        uint8_t opcode = 0;
        uint32_t operand = 0;

        bool halted = false;

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
            OP_CALL = 0b10111,
            OP_RET  = 0b11000,
            OP_EI   = 0b11001,
            OP_DI   = 0b11010,
            OP_IRET = 0b11011,
            OP_HALT = 0b11100
        };

        std::ostringstream* logData = nullptr;

        void log(std::string line) {
            if (logData) (*logData) << line << "\n";
        }
};

class ProcessorModel {
    public:
        ProcessorModel(MachineConfig cfg);
        ~ProcessorModel();

        void loadBinary(const std::string& filename);
        void process();
    private:
        MachineConfig cfg;

        size_t textSize = 0;
        size_t dataStart = 0;
        size_t dataSize = 0;
        uint32_t entryPoint = 0;

        uint32_t zero = 0;
        size_t tickCount = 0;

        bool halted = false;
        bool binaryLoaded = false;

        Memory memory;
        Registers registers;
        ALU alu;
        MUX mux1, mux2;
        
        Latch latchALU_DR, latchALU_AR, latchALU_SP, latchALU_AC, latchALU_PC, latchALU_SPC;
        LatchRouter latchRouter;
        Latch latchMEM_IR, latchMEM_DR, latchDR_MEM;
        Latch latchSPC_PC;
        Latch latchVec_PC;
        
        CU cu;
        InterruptHandler interruptHandler;

        IOSimulator iosim;

        void tick();

        std::string memDump();
        std::string registerDump();

        void parseInput();
  
        std::ostringstream outputData;
        std::ostringstream logData;

        void writeOutput(std::string& line) { outputData << line << "\n"; }
        void writeLog(std::string& line) { logData << line << "\n"; }
};

#endif