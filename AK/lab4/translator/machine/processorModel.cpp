#include "processorModel.h"

ProcessorModel::ProcessorModel(MachineConfig cfg)
    : cfg(cfg) {
    parseInput();
    iosim.connectOutput(outputData);
    iosim.setMixedOutput(cfg.mixed);

    mux1.addInput(zero);
    mux1.addInput(registers.getRef(Registers::ACC));
    mux1.addInput(zero);
    mux1.addInput(registers.getRef(Registers::IP));

    mux2.addInput(zero);
    mux2.addInput(registers.getRef(Registers::AR));
    mux2.addInput(registers.getRef(Registers::DR));
    mux2.addInput(registers.getRef(Registers::SP));

    alu.setLeftInputGetter(mux1.makeGetter());
    alu.setRightInputGetter(mux2.makeGetter());
    alu.setFlagRefs(registers.getNRef(),
                    registers.getZRef(),
                    registers.getVRef(),
                    registers.getCRef());

    latchALU_AC.setSource(alu.getResultRef());
    latchALU_AC.setTarget(registers.getRef(Registers::ACC));
    
    latchALU_AR.setSource(alu.getResultRef());
    latchALU_AR.setTarget(registers.getRef(Registers::AR));

    latchALU_DR.setSource(alu.getResultRef());
    latchALU_DR.setTarget(registers.getRef(Registers::DR));

    latchALU_PC.setSource(alu.getResultRef());
    latchALU_PC.setTarget(registers.getRef(Registers::IP));
    
    latchALU_SP.setSource(alu.getResultRef());
    latchALU_SP.setTarget(registers.getRef(Registers::SP));

    latchALU_SPC.setSource(alu.getResultRef());
    latchALU_SPC.setTarget(interruptHandler.getSPCRef());

    latchRouter.setLatches(latchALU_AC, latchALU_AR, latchALU_DR, latchALU_PC, latchALU_SP, latchALU_SPC);

    auto memoryGetter = [this]() -> uint32_t& {
        return this->memory.atRef(this->registers.getRef(Registers::AR));
    };

    latchMEM_IR.setSourceGetter(memoryGetter);
    latchMEM_IR.setTarget(registers.getRef(Registers::IR));

    latchMEM_DR.setSourceGetter(memoryGetter);
    latchMEM_DR.setTarget(registers.getRef(Registers::DR));

    latchDR_MEM.setSource(registers.getRef(Registers::DR));
    latchDR_MEM.setTargetGetter(memoryGetter);

    latchSPC_PC.setSource(interruptHandler.getSPCRef());
    latchSPC_PC.setTarget(registers.getRef(Registers::IP));

    latchVec_PC.setTarget(registers.getRef(Registers::IP));

    interruptHandler.connect(latchALU_SPC, latchSPC_PC, latchVec_PC);
    
    iosim.connect(interruptHandler, memory);

    cu.connect(interruptHandler, mux1, mux2, alu, latchRouter, latchMEM_IR, latchMEM_DR, latchDR_MEM);
    cu.setFlagsInput(registers.getNRef(),
                     registers.getZRef(),
                     registers.getVRef(),
                     registers.getCRef());
    cu.setIRInput(registers.getRef(Registers::IR));
}

ProcessorModel::~ProcessorModel() { }

void ProcessorModel::parseInput() {
    if (cfg.input_file.empty()) return;
    std::ifstream inputFile(cfg.input_file);
    if (!inputFile.is_open()) throw std::runtime_error("Unable to open " + cfg.input_file);
    if (cfg.input_mode == InputMode::NONE) throw std::runtime_error("input_mode not specified");

    if (cfg.input_mode == InputMode::MODE_STREAM) {
        if (cfg.schedule_start == -1) throw std::runtime_error("schedule_start not specified");
        if (cfg.schedule_offset == -1) throw std::runtime_error("schedule_offset not specified");

        char token;
        size_t currentTick = cfg.schedule_start;
        while (inputFile.get(token)) {
            iosim.addInput({currentTick, token});
            currentTick += cfg.schedule_offset;
        }
    } else if (cfg.input_mode == InputMode::MODE_TOKEN) {
        std::string line;
        while (std::getline(inputFile, line)) {
            if (line.empty()) continue;

            std::istringstream iss(line);
            size_t tick;
            std::string tokenStr;
            char token;

            if (!(iss >> tick >> tokenStr))
                throw std::runtime_error("Input file parse error");
            
            if (tokenStr == "\\n") token = '\n';
            else if (tokenStr == "\\t") token = '\t';
            else if (tokenStr.size() == 1) token = tokenStr[0];
            
            iosim.addInput({tick, token});
        }
    }
}

void ProcessorModel::process() {
    if (!binaryLoaded)
        throw std::runtime_error("Binary not loaded");

    while(!cu.isHalted()) {
        tick();
        tickCount++;
    }

    logData << "Output tokens:" << std::endl;
    logData << iosim.getTokenOutput() << std::endl;

    std::cout << "Completed in " << tickCount << " ticks\n";

    if (!cfg.output_file.empty()) {
        std::ofstream outputFile(cfg.output_file);
        if (outputFile) outputFile << outputData.str();
        else std::cerr << "Unable to open " << cfg.output_file << std::endl;
        outputFile.close();
        std::cout << "Wrote output to " << cfg.output_file << " in "
                  << (cfg.mixed ? "mixed" : "normal") << " mode " << std::endl;
    }

    if (!cfg.log_file.empty()) {
        std::ofstream logFile(cfg.log_file);
        if (logFile) logFile << logData.str();
        else std::cerr << "Unable to open " << cfg.log_file << std::endl;
        logFile.close();
        std::cout << "Wrote log to " << cfg.log_file << std::endl;
    }
}

void ProcessorModel::loadBinary(const std::string& filename) {
    std::ifstream in(filename, std::ios::binary);
    if (!in) throw std::runtime_error("Can't open binary file: " + filename);

    uint8_t buf[3];

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read textSize from header");
    size_t textSize = (buf[0] << 16) | (buf[1] << 8) | buf[2];

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read dataSize from header");
    size_t dataSize = (buf[0] << 16) | (buf[1] << 8) | buf[2];

    if (textSize + dataSize > MEM_SIZE)
        throw std::runtime_error("Binary too large for memory");

    for (size_t addr = 0; addr < textSize; addr++) {
        if (!in.read(reinterpret_cast<char*>(buf), 3))
            throw std::runtime_error("Unexpected EOF while reading instructions");
        memory[addr] = (buf[0] << 16) | (buf[1] << 8) | buf[2];
    }

    for (size_t addr = textSize; addr < textSize + dataSize; addr++) {
        if (!in.read(reinterpret_cast<char*>(buf), 3))
            throw std::runtime_error("Unexpected EOF while reading data");
        memory[addr] = (buf[0] << 16) | (buf[1] << 8) | buf[2];
    }

    this->textSize = textSize;
    this->dataSize = dataSize;
    this->dataStart = textSize;

    binaryLoaded = true;

    uint32_t defaultVector = memory[dataStart + 0];
    uint32_t inputVector = memory[dataStart + 1];

    interruptHandler.setVectorTable(defaultVector, inputVector);
}


void ProcessorModel::memDump() {
    std::cout << "MEMDUMP:\n";
    std::cout << "textSize: " << std::hex << textSize << std::dec << std::endl;
    std::cout << "dataSize: " << std::hex << dataSize << std::dec << std::endl;
    size_t address = 0;
    while (address < textSize + dataSize) {
        std::cout << "MEM[" << std::hex << address << "] = 0x" << memory[address++] << std::dec << "\n";
    }

    address = 0x7FFFF;
    int count = 5;
    while (count >= 0) {
        std::cout << "MEM[" << std::hex << address << "] = 0x" << memory[address--] << std::dec << "\n";
        count--;
    }

    std::cout << "\n\n";
}

std::string ProcessorModel::registerDump() {
    std::ostringstream result;
    result << std::hex;
    result << "AC: 0x" << registers.get(Registers::ACC) << std::endl;
    result << "IR: 0x" << registers.get(Registers::IR) << std::endl;
    result << "AR: 0x" << registers.get(Registers::AR) << std::endl;
    result << "DR: 0x" << registers.get(Registers::DR) << std::endl;
    result << "PC: 0x" << registers.get(Registers::IP) << std::endl;
    result << "SP: 0x" << registers.get(Registers::SP) << std::endl;
    result << "NZVC: " << registers.getN()
                       << registers.getZ()
                       << registers.getV()
                       << registers.getC() << std::endl;
    return result.str();
}

void ProcessorModel::tick() {
    logData << "tick #" << tickCount << "\n";
    
    iosim.check(tickCount);
    cu.decode();

    latchSPC_PC.propagate();
    latchVec_PC.propagate();

    latchMEM_IR.propagate();
    latchMEM_DR.propagate();
    latchDR_MEM.propagate();
    alu.perform();
    latchRouter.propagate();
    

    latchSPC_PC.setEnabled(false);
    latchVec_PC.setEnabled(false);

    latchMEM_IR.setEnabled(false);
    latchMEM_DR.setEnabled(false);
    latchDR_MEM.setEnabled(false);
    alu.setOperation(ALU::Operation::NOP);
    alu.setWriteFlags(false);
    latchRouter.setLatchStates({0, 0, 0, 0, 0, 0});

    logData << registerDump() << std::endl;
}

void InterruptHandler::step() {
    switch (irq) {
        case IRQType::IO_INPUT: {
            switch (intState) {
                case InterruptState::SavingPC:
                    ipc = true;
                    latchALU_SPC->setEnabled(true);
                    intState = InterruptState::Executing;
                    break;
                case InterruptState::Executing:
                    latchVec_PC->setEnabled(true);
                    intState = InterruptState::Restoring;
                    break;
                case InterruptState::Restoring:
                    latchSPC_PC->setEnabled(true);
                    ipc = false;
                    irq = IRQType::NONE;
                    intState = InterruptState::SavingPC;
                    break;
            }
            break;
        }

        case IRQType::NONE:
            break;
    }
}

void CU::decode() {
    log("State " + stateStr());

    if ((interruptHandler->shouldInterrupt() || interruptHandler->isEnteringInterrupt()) && state == CPUState::FetchAR) {
        interruptHandler->step();
        return;
    }

    switch(state) {
        case CPUState::FetchAR: {
            mux1->select(3);
            mux2->select(0);

            alu->setOperation(ALU::Operation::NOP);
            
            latchRouter->setLatchState(1, 1);
            
            state = CPUState::FetchIR;
            break;
        }
        case CPUState::FetchIR: {
            latchMEM_IR->setEnabled(true);

            state = CPUState::Decode;
            break;
        }
        case CPUState::Decode: {
            opcode = ((*IR) >> 19) & 0x1F;
            operand = (*IR) & 0x7FFFF;

            instructionTick();
            if (instructionDone) {
                instructionDone = false;
                state = CPUState::IncrementIP;
            }

            break;
        }
        case CPUState::IncrementIP: {
            mux1->select(3);
            mux2->select(0);

            alu->setOperation(ALU::Operation::INC);
            
            latchRouter->setLatchState(3, 1);
            
            state = CPUState::FetchAR;
            break;
        }
        case CPUState::Halt: {
            break;
        }
    }
}

void CU::instructionTick() {
    log("Instruction step #" + microstep);
    
    switch(opcode) {
        case OP_ADD:
        case OP_SUB:
        case OP_DIV:
        case OP_MUL:
        case OP_REM:
            switch (microstep) {
                case 0:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 1:
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(1);
                    mux2->select(2);

                    alu->setWriteFlags(true);
                    if (opcode == OP_ADD)
                        alu->setOperation(ALU::Operation::ADD);
                    else if (opcode == OP_SUB)
                        alu->setOperation(ALU::Operation::SUB);
                    else if (opcode == OP_DIV)
                        alu->setOperation(ALU::Operation::DIV);
                    else if (opcode == OP_MUL)
                        alu->setOperation(ALU::Operation::MUL);
                    else if (opcode == OP_REM)
                        alu->setOperation(ALU::Operation::REM);

                    latchRouter->setLatchState(0, 1);

                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;
        
        case OP_INC:
        case OP_DEC:
        case OP_NOT:
            mux1->select(1);
            mux2->select(0);
            
            alu->setWriteFlags(true);
            if (opcode == OP_INC)
                alu->setOperation(ALU::Operation::INC);
            else if (opcode == OP_DEC)
                alu->setOperation(ALU::Operation::DEC);
            else if (opcode == OP_NOT)
                alu->setOperation(ALU::Operation::NOT);

            latchRouter->setLatchState(0, 1);

            instructionDone = true;
            break;

        case OP_CLA:
            mux1->select(0);
            mux2->select(0);

            alu->setOperation(ALU::Operation::NOP);

            latchRouter->setLatchState(0, 1);

            instructionDone = true;
            break;

        case OP_JMP:
            mux1->select(2);
            mux2->select(0);

            alu->setOperation(ALU::Operation::DEC);

            latchRouter->setLatchState(3, 1);

            instructionDone = true;
            break;

        case OP_JZ:
            mux1->select(2);
            mux2->select(0);

            if (*Z) {
                latchRouter->setLatchState(3, 1);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
            }

            instructionDone = true;
            break;

        case OP_JNZ:
            mux1->select(2);
            mux2->select(0);

            if (!*Z) {
                latchRouter->setLatchState(3, 1);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
            }

            instructionDone = true;
            break;
        
        case OP_JG:
            mux1->select(2);
            mux2->select(0);

            if (!*Z && *N == *V) {
                latchRouter->setLatchState(3, 1);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
            }

            instructionDone = true;
            break;

        case OP_JGE:
            mux1->select(2);
            mux2->select(0);

            if (*N == *V) {
                latchRouter->setLatchState(3, 1);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
            }

            instructionDone = true;
            break;

        case OP_JL:
            mux1->select(2);
            mux2->select(0);

            if (*N != *V) {
                latchRouter->setLatchState(3, 1);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
            }

            instructionDone = true;
            break;

        case OP_JLE:
            mux1->select(2);
            mux2->select(0);

            if (*Z || *N != *V) {
                latchRouter->setLatchState(3, 1);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
            }

            instructionDone = true;
            break;

        case OP_PUSH:
            switch (microstep) {
                case 0:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::NOP);
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 1:
                    mux1->select(1);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(2, 1);
                    
                    microstep++;
                    break;
                case 2:
                    latchDR_MEM->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchDR_MEM->setEnabled(false);

                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::DEC);
                    
                    latchRouter->setLatchState(4, 1);
                    
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_POP:
            switch (microstep) {
                case 0:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::INC);
                    
                    latchRouter->setLatchState(4, 1);
                    
                    microstep++;
                    break;
                case 1:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(0, 1);
                    
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_LD:
            switch (microstep) {
                case 0:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 1:
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    alu->setWriteFlags(true);

                    latchRouter->setLatchState(0, 1);

                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_LDA:
            switch (microstep) {
                case 0:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);

                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 1:
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 3:
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 4:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    alu->setWriteFlags(true);
                    
                    latchRouter->setLatchState(0, 1);
                    
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_LDI:
            mux1->select(2);
            mux2->select(0);

            alu->setOperation(ALU::Operation::NOP);

            latchRouter->setLatchState(0, 1);

            instructionDone = true;
            break;

        case OP_ST:
            switch (microstep) {
                case 0:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);

                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 1:
                    mux1->select(1);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(2, 1);
                    
                    microstep++;
                    break;
                case 2:
                    latchDR_MEM->setEnabled(true);
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_STA:
            switch (microstep) {
                case 0:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 1:
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);

                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 3:
                    mux1->select(1);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(2, 1);
                    
                    microstep++;
                    break;
                case 4:
                    latchDR_MEM->setEnabled(true);
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_CALL:
            switch (microstep) {
                case 0:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 1:
                    mux1->select(3);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(2, 1);
                    
                    microstep++;
                    break;
                case 2:
                    latchDR_MEM->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchDR_MEM->setEnabled(false);

                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::DEC);
                    
                    latchRouter->setLatchState(4, 1);
                    
                    microstep++;
                    break;
                case 4:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::DEC);
                    
                    latchRouter->setLatchState(3, 1);
                    
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_RET:
            switch (microstep) {
                case 0:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::INC);
                    
                    latchRouter->setLatchState(4, 1);
                    
                    microstep++;
                    break;
                case 1:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(1, 1);
                    
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    
                    latchRouter->setLatchState(3, 1);
                    
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_EI:
            interruptHandler->getIERef() = true;
            instructionDone = true;
            break;

        case OP_DI:
            interruptHandler->getIERef() = false;
            instructionDone = true;
            break;

        case OP_IRET:
            interruptHandler->step();
            alu->setOperation(ALU::Operation::DEC);
            latchRouter->setLatchState(5, 1);
            instructionDone = true;
            break;

        case OP_HALT:
            halted = true;
            instructionDone = true;
            break;

        default:
            throw std::runtime_error("Unknown opcode");
    }
}