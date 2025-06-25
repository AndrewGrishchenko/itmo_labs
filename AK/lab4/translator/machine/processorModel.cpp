#include "processorModel.h"

ProcessorModel::ProcessorModel() {
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

    latchRouter.setLatches(latchALU_AC, latchALU_AR, latchALU_DR, latchALU_PC, latchALU_SP);

    
    auto memoryGetter = [this]() -> uint32_t& {
        return this->memory.atRef(this->registers.getRef(Registers::AR));
    };

    // latchMEM_IR.setSourceGetter(memory.makeGetterAtRef(registers.getRef(Registers::AR)));
    latchMEM_IR.setSourceGetter(memoryGetter);
    latchMEM_IR.setTarget(registers.getRef(Registers::IR));

    latchMEM_DR.setSourceGetter(memoryGetter);
    latchMEM_DR.setTarget(registers.getRef(Registers::DR));

    latchDR_MEM.setSource(registers.getRef(Registers::DR));
    latchDR_MEM.setTargetGetter(memoryGetter);

    // latchRouter.setInput(alu.getResultRef());
    // latchRouter.addOutput(registers.getRef(Registers::ACC));
    // latchRouter.addOutput(registers.getRef(Registers::AR));
    // latchRouter.addOutput(registers.getRef(Registers::DR));
    // latchRouter.addOutput(registers.getRef(Registers::IP));
    // latchRouter.addOutput(registers.getRef(Registers::SP));

    cu.connect(mux1, mux2, alu, latchRouter, latchMEM_IR, latchMEM_DR, latchDR_MEM);
    cu.setFlagsInput(registers.getNRef(),
                     registers.getZRef(),
                     registers.getVRef(),
                     registers.getCRef());
    cu.setIRInput(registers.getRef(Registers::IR));
}

ProcessorModel::~ProcessorModel() { }

void ProcessorModel::process() {
    // memory.reset();
    // registers.reset();
    // operand = 0;
    // textSize = dataSize = dataStart = 0;
    // halted = false;
    // tickCount = 0;
    // microstep = 0;

    if (!binaryLoaded)
        throw std::runtime_error("Binary not loaded");

    while(!cu.isHalted()) {
        tick();
        tickCount++;
    }

    allDump();
    std::cout << "Completed in " << tickCount << " ticks\n";
}

void ProcessorModel::loadBinary(const std::string& filename) {
    std::ifstream in(filename, std::ios::binary);
    if (!in) throw std::runtime_error("Can't open binary " + filename);

    uint8_t buf[3];
    size_t address = 0;

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read textSize from header");
    textSize = (buf[0] << 16) | (buf[1] << 8) | buf[2];

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read entryPoint from header");
    entryPoint = (buf[0] << 16) | (buf[1] << 8) | buf[2];

    dataStart = textSize;
    registers.set(Registers::IP, entryPoint);

    std::cout << "BINARY LOAD:\n";
    std::cout << "textSize: 0x" << std::hex << textSize << std::dec << "\n";
    std::cout << "dataStart: 0x" << std::hex << dataStart << std::dec << "\n";
    std::cout << "entryPoint: 0x" << std::hex << entryPoint << std::dec << "\n\n";
    allDump();

    while (in.read(reinterpret_cast<char*>(buf), 3)) {
        if (address >= MEM_SIZE)
            throw std::runtime_error("Binary too large");
        memory[address++] = (buf[0] << 16) | (buf[1] << 8) | buf[2];
        std::cout << "MEM[" << std::hex << address - 1 << "] = " << memory[address - 1] << std::dec << "\n"; 
    }
    std::cout << "\n";

    dataSize = address - textSize;
    binaryLoaded = true;
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

void ProcessorModel::allDump() {
    std::cout << "\nALL DUMP:\n";
    
    std::cout << "REGISTERS:\n";
    std::cout << "ACC: " << std::hex << "0x" << registers.get(Registers::ACC) << std::dec << "\n";
    std::cout << "IR: " << std::hex << "0x" << registers.get(Registers::IR) << std::dec << "\n";
    std::cout << "AR: " << std::hex << "0x" << registers.get(Registers::AR) << std::dec << "\n";
    std::cout << "DR: " << std::hex << "0x" << registers.get(Registers::DR) << std::dec << "\n";
    std::cout << "IP: " << std::hex << "0x" << registers.get(Registers::IP) << std::dec << "\n";
    std::cout << "SP: " << std::hex << "0x" << registers.get(Registers::SP) << std::dec << "\n";
    std::cout << "\n";

    memDump();
}

void ProcessorModel::tick() {
    // std::cout << "ticking state " << stateStr() << "\n";
    
    cu.decode();

    std::cout << "LATCH_MEM_IR ";
    latchMEM_IR.propagate();

    std::cout << "LATCH_MEM_DR ";
    latchMEM_DR.propagate();

    std::cout << "LATCH_DR_MEM ";
    latchDR_MEM.propagate();
    alu.perform();
    latchRouter.propagate();
    
    latchMEM_IR.setEnabled(false);
    latchMEM_DR.setEnabled(false);
    latchDR_MEM.setEnabled(false);
    alu.setWriteFlags(false);
    latchRouter.setLatchStates({0, 0, 0, 0, 0});

    allDump();
}

void CU::decode() {
    std::cout << "ticking state " << stateStr() << "\n";

    switch(state) {
        case CPUState::FetchAR: {
            mux1->select(3);
            mux2->select(0);

            alu->setOperation(ALU::Operation::NOP);
            // alu.perform(ALU::Operation::NOP);

            latchRouter->setLatchStates({0, 1, 0, 0, 0});
            // latchRouter->propagate();

            state = CPUState::FetchIR;
            break;
        }
        case CPUState::FetchIR: {
            // uint32_t instr = memory[registers.get(Registers::AR)];
            // registers.set(Registers::IR, instr);

            latchMEM_IR->setEnabled(true);

            // opcode = (instr >> 19) & 0x1F;
            // operand = instr & 0x7FFFF;
            
            //TODO: where do i calc opcode & operand? add to scheme tho
            // updateOperand();

            state = CPUState::Decode;
            break;
        }
        case CPUState::Decode: {
            //decode
            //TODO: think why i need function; maybe i have to do this for opcode too?

            // std::cout << "instr = 0x" << std::hex << static_cast<int>(registers.get(Registers::IR)) << std::dec << "\n";
            // std::cout << "opcode = 0x" << std::hex << static_cast<int>(opcode) << std::dec << "\n";
            // std::cout << "operand = 0x" << std::hex << static_cast<int>(operand) << std::dec << "\n";
            
            // latchMEM_DR->setEnabled(false);

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
            // alu.perform(ALU::Operation::INC);

            latchRouter->setLatchStates({0, 0, 0, 1, 0});
            // latchRouter->propagate();
            
            state = CPUState::FetchAR;
            break;
        }
        case CPUState::Halt: {
            break;
        }
    }
}

void CU::instructionTick() {
    std::cout << "microstep #" << microstep << "\n";
    // std::cout << "tick #" << microstep << " of opcode = " << std::hex << "0x" << static_cast<int>(opcode) << " & operand = " << operand << std::dec << "\n";

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
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    // registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
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
                        // alu.perform(ALU::Operation::ADD, true);
                    else if (opcode == OP_SUB)
                        alu->setOperation(ALU::Operation::SUB);
                        // alu.perform(ALU::Operation::SUB, true);
                    else if (opcode == OP_DIV)
                        alu->setOperation(ALU::Operation::DIV);
                        // alu.perform(ALU::Operation::DIV, true);
                    else if (opcode == OP_MUL)
                        alu->setOperation(ALU::Operation::MUL);
                        // alu.perform(ALU::Operation::MUL, true);
                    else if (opcode == OP_REM)
                        alu->setOperation(ALU::Operation::REM);
                        // alu.perform(ALU::Operation::REM, true);

                    latchRouter->setLatchStates({1, 0, 0, 0, 0});
                    // latchRouter->propagate();

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
            
            if (opcode == OP_INC)
                alu->setOperation(ALU::Operation::INC);
                // alu.perform(ALU::Operation::INC, true);
                //TODO: think should i save flags in these cases
            else if (opcode == OP_DEC)
                alu->setOperation(ALU::Operation::DEC);
                // alu.perform(ALU::Operation::DEC, true);
            else if (opcode == OP_NOT)
                alu->setOperation(ALU::Operation::NOT);
                // alu.perform(ALU::Operation::NOT, true);

            latchRouter->setLatchStates({1, 0, 0, 0, 0});
            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_CLA:
            mux1->select(0);
            mux2->select(0);

            alu->setOperation(ALU::Operation::NOP);
            // alu.perform(ALU::Operation::NOP);

            latchRouter->setLatchStates({1, 0, 0, 0, 0});
            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_JMP:
            mux1->select(2);
            mux2->select(0);

            alu->setOperation(ALU::Operation::DEC);
            // alu.perform(ALU::Operation::DEC);

            latchRouter->setLatchStates({0, 0, 0, 1, 0});
            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_JZ:
            mux1->select(2);
            mux2->select(0);

            if (*Z) {
                latchRouter->setLatchStates({0, 0, 0, 1, 0});
                alu->setOperation(ALU::Operation::DEC);
                // alu.perform(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
                // alu.perform(ALU::Operation::NOP);
            }
            
            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_JNZ:
            mux1->select(2);
            mux2->select(0);

            if (!*Z) {
                latchRouter->setLatchStates({0, 0, 0, 1, 0});
                alu->setOperation(ALU::Operation::DEC);
                // alu.perform(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
                // alu.perform(ALU::Operation::NOP);
            }
            
            // latchRouter->propagate();

            instructionDone = true;
            break;
        
        case OP_JG:
            mux1->select(2);
            mux2->select(0);

            if (!*Z && *N == *V) {
                latchRouter->setLatchStates({0, 0, 0, 1, 0});
                // alu.perform(ALU::Operation::DEC);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0});
                alu->setOperation(ALU::Operation::NOP);
                // alu.perform(ALU::Operation::NOP);
            }

            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_JGE:
            mux1->select(2);
            mux2->select(0);

            if (*N == *V) {
                latchRouter->setLatchStates({0, 0, 0, 1, 0});
                // alu.perform(ALU::Operation::DEC);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0});
                // alu.perform(ALU::Operation::NOP);
                alu->setOperation(ALU::Operation::NOP);
            }

            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_JL:
            mux1->select(2);
            mux2->select(0);

            if (*N != *V) {
                latchRouter->setLatchStates({0, 0, 0, 1, 0});
                // alu.perform(ALU::Operation::DEC);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0});
                // alu.perform(ALU::Operation::NOP);
                alu->setOperation(ALU::Operation::NOP);
            }

            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_JLE:
            mux1->select(2);
            mux2->select(0);

            std::cout << "СЮДА БЛЯТЬ\n";
            if (*Z || *N != *V) {
                latchRouter->setLatchStates({0, 0, 0, 1, 0});
                // alu.perform(ALU::Operation::DEC);
                alu->setOperation(ALU::Operation::DEC);
            } else {
                latchRouter->setLatchStates({0, 0, 0, 0, 0});
                // alu.perform(ALU::Operation::NOP);
                alu->setOperation(ALU::Operation::NOP);
            }

            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_PUSH:
            switch (microstep) {
                case 0:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    mux1->select(1);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 0, 1, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 2:
                    // memory[registers.get(Registers::AR)] = registers.get(Registers::DR);
                    latchDR_MEM->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchDR_MEM->setEnabled(false);

                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::DEC);
                    // alu.perform(ALU::Operation::DEC);

                    latchRouter->setLatchStates({0, 0, 0, 0, 1});
                    // latchRouter->propagate();
                    
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
                    // alu.perform(ALU::Operation::INC);

                    latchRouter->setLatchStates({0, 0, 0, 0, 1});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 2:
                    // registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({1, 0, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_LD:
            //TODO: think flags in nop, i.e. ld value; jz ...
            switch (microstep) {
                case 0:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    // registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({1, 0, 0, 0, 0});
                    // latchRouter->propagate();

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
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    latchMEM_DR->setEnabled(true);
                    // registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 3:
                    latchMEM_DR->setEnabled(true);
                    // registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
                    microstep++;
                    break;
                case 4:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({1, 0, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_LDI:
            mux1->select(2);
            mux2->select(0);

            alu->setOperation(ALU::Operation::NOP);
            // alu.perform(ALU::Operation::NOP);

            latchRouter->setLatchStates({1, 0, 0, 0, 0});
            // latchRouter->propagate();

            instructionDone = true;
            break;

        case OP_ST:
            switch (microstep) {
                case 0:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    mux1->select(1);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 0, 1, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 2:
                    latchDR_MEM->setEnabled(true);
                    // memory[registers.get(Registers::AR)] = registers.get(Registers::DR);
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
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    latchMEM_DR->setEnabled(true);
                    // registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
                    microstep++;
                    break;
                case 2:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 3:
                    mux1->select(1);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 0, 1, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 4:
                    // memory[registers.get(Registers::AR)] = registers.get(Registers::DR);
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
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    mux1->select(3);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 0, 1, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 2:
                    // memory[registers.get(Registers::AR)] = registers.get(Registers::DR);
                    latchDR_MEM->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchDR_MEM->setEnabled(false);

                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::DEC);
                    // alu.perform(ALU::Operation::DEC);

                    latchRouter->setLatchStates({0, 0, 0, 0, 1});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 4:
                    mux1->select(2);
                    mux2->select(0);

                    alu->setOperation(ALU::Operation::DEC);
                    // alu.perform(ALU::Operation::DEC);

                    latchRouter->setLatchStates({0, 0, 0, 1, 0});
                    // latchRouter->propagate();

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
                    // alu.perform(ALU::Operation::INC);

                    latchRouter->setLatchStates({0, 0, 0, 0, 1});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 1:
                    mux1->select(0);
                    mux2->select(3);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 1, 0, 0, 0});
                    // latchRouter->propagate();

                    microstep++;
                    break;
                case 2:
                    // registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
                    latchMEM_DR->setEnabled(true);
                    microstep++;
                    break;
                case 3:
                    latchMEM_DR->setEnabled(false);

                    mux1->select(0);
                    mux2->select(2);

                    alu->setOperation(ALU::Operation::NOP);
                    // alu.perform(ALU::Operation::NOP);

                    latchRouter->setLatchStates({0, 0, 0, 1, 0});
                    // latchRouter->propagate();

                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        case OP_HALT:
            halted = true;
            instructionDone = true;
            break;

        default:
            throw std::runtime_error("not implemented");
    }
}

void ProcessorModel::updateOperand() {
    // operand = registers.get(Registers::IR) & 0x7FFFF;
}