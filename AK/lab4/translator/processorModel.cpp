#include "processorModel.h"

ProcessorModel::ProcessorModel() {
    mux1.addInput(zero);
    mux1.addInput(registers.getRef(Registers::ACC));
    mux1.addInput(operand);
    mux1.addInput(registers.getRef(Registers::IP));

    mux2.addInput(zero);
    mux2.addInput(registers.getRef(Registers::AR));
    mux2.addInput(registers.getRef(Registers::DR));
    mux2.addInput(registers.getRef(Registers::SP));

    alu.setLeftInputGetter(mux1.makeGetter());
    alu.setRightInputGetter(mux2.makeGetter());

    latchRouter.setInput(alu.getResultValueRef());
    latchRouter.addOutput(registers.getRef(Registers::ACC));
    latchRouter.addOutput(registers.getRef(Registers::AR));
    latchRouter.addOutput(registers.getRef(Registers::DR));
    latchRouter.addOutput(registers.getRef(Registers::IP));
    latchRouter.addOutput(registers.getRef(Registers::SP));
}

ProcessorModel::~ProcessorModel() { }

void ProcessorModel::process() {
    memory.reset();
    registers.reset();
    operand = 0;
    textSize = dataSize = dataStart = 0;
    halted = false;
    tickCount = 0;
    microstep = 0;

    if (!binaryLoaded)
        throw std::runtime_error("Binary not loaded");

    while(!halted) {
        tick();
        tickCount++;
    }
}

void ProcessorModel::loadBinary(const std::string& filename) {
    binaryLoaded = true;
    
    std::ifstream in(filename, std::ios::binary);
    if (!in) throw std::runtime_error("Can't open file " + filename);

    uint8_t buf[3];
    size_t address = 0;

    if (!in.read(reinterpret_cast<char*>(buf), 3))
        throw std::runtime_error("Failed to read header");

    textSize = (buf[0] << 16) | (buf[1] << 8) | buf[2];
    dataStart = textSize;

    while (in.read(reinterpret_cast<char*>(buf), 3)) {
        if (address >= MEM_SIZE)
            throw std::runtime_error("Binary too large");
        memory[address++] = (buf[0] << 16) | (buf[1] << 8) | buf[2];
    }

    dataSize = address - textSize;
}

void ProcessorModel::tick() {
    switch (state) {
        case CPUState::FetchAR: {
            mux1.select(3);
            mux2.select(0);

            alu.perform(ALU::Operation::NOP);
            
            latchRouter.setLatches({0, 1, 0, 0, 0});
            latchRouter.propagate();

            state = CPUState::FetchIR;
            break;
        }
        case CPUState::FetchIR: {
            uint32_t instr = memory[registers.get(Registers::AR)];

            registers.set(Registers::IR, instr);
            
            opcode = (instr >> 19) & 0x1F;
            operand = instr & 0x7FFFF;
            //TODO: where do i calc opcode & operand? add to scheme tho
            // updateOperand();

            state = CPUState::Decode;
            break;
        }
        case CPUState::Decode: {
            //decode
            //TODO: think why i need function; maybe i have to do this for opcode too?

            instructionTick();
            if (instructionDone) {
                instructionDone = false;
                state = CPUState::IncrementIP;
            }
            break;
        }
        case CPUState::IncrementIP: {
            mux1.select(3);
            mux2.select(0);

            alu.perform(ALU::Operation::INC);

            latchRouter.setLatches({0, 0, 0, 1, 0});
            latchRouter.propagate();
            
            state = CPUState::FetchAR;
            break;
        }
        case CPUState::Halt: {
            break;
        }
    }
}

void ProcessorModel::instructionTick() {
    switch(opcode) {
        case OP_ADD:
        case OP_SUB:
        case OP_DIV:
        case OP_MUL:
        case OP_REM:
            switch (microstep) {
                case 0:
                    mux1.select(2);
                    mux2.select(0);

                    alu.perform(ALU::Operation::NOP);

                    latchRouter.setLatches({0, 1, 0, 0, 0});
                    latchRouter.propagate();

                    microstep++;
                    break;
                case 1:
                    registers.set(Registers::DR, memory[registers.get(Registers::AR)]);
                    microstep++;
                    break;
                case 2:
                    mux1.select(1);
                    mux2.select(2);

                    if (opcode == OP_ADD)
                        alu.perform(ALU::Operation::ADD);
                    else if (opcode == OP_SUB)
                        alu.perform(ALU::Operation::SUB);
                    else if (opcode == OP_DIV)
                        alu.perform(ALU::Operation::DIV);
                    else if (opcode == OP_MUL)
                        alu.perform(ALU::Operation::MUL);
                    else if (opcode == OP_REM)
                        alu.perform(ALU::Operation::REM);

                    latchRouter.setLatches({1, 0, 0, 0, 0});
                    latchRouter.propagate();

                    microstep++;
                    instructionDone = true;
                    break;
            }
            break;
        
        case OP_INC:
        case OP_DEC:
        case OP_NOT:
            mux1.select(1);
            mux2.select(0);
            
            if (opcode == OP_INC)
                alu.perform(ALU::Operation::INC);
            else if (opcode == OP_DEC)
                alu.perform(ALU::Operation::DEC);
            else if (opcode == OP_NOT)
                alu.perform(ALU::Operation::NOT);

            latchRouter.setLatches({1, 0, 0, 0, 0});
            latchRouter.propagate();

            instructionDone = true;
            break;

        case OP_CLA:
            mux1.select(0);
            mux2.select(0);

            alu.perform(ALU::Operation::NOP);

            latchRouter.setLatches({1, 0, 0, 0, 0});
            latchRouter.propagate();

            instructionDone = true;
            break;

        case OP_JMP:
            mux1.select(2);
            mux2.select(0);

            alu.perform(ALU::Operation::NOP);

            latchRouter.setLatches({0, 0, 0, 1, 0});
            latchRouter.propagate();

            instructionDone = true;
            break;

        case OP_PUSH:
            switch (microstep) {
                case 0:
                    mux1.select(0);
                    mux2.select(1);

                    alu.perform(ALU::Operation::ADD);

                    latchRouter.setLatches({0, 0, 1, 0, 0});
                    latchRouter.propagate();

                    microstep++;
                    break;
                case 1:
                    mux1.select(3);
                    mux2.select(0);

                    alu.perform(ALU::Operation::ADD);

                    latchRouter.setLatches({0, 1, 0, 0, 0});
                    latchRouter.propagate();

                    microstep++;
                    break;
                case 2:
                    memory[registers.get(Registers::AR)] = registers.get(Registers::DR);
                    microstep++;
                    break;
                case 3:
                    mux1.select(3);
                    mux2.select(0);

                    alu.perform(ALU::Operation::DEC);

                    latchRouter.setLatches({0, 0, 0, 0, 1});
                    latchRouter.propagate();
                    
                    microstep = 0;
                    instructionDone = true;
                    break;
            }
            break;

        default:
            throw std::runtime_error("not implemented");
    }
}

void ProcessorModel::updateOperand() {
    operand = registers.get(Registers::IR) & 0x7FFFF;
}