#include "processorModel.h"
#include "configParser.hpp"

int main(int argc, char* argv[]) {
    if (argc != 3)
        throw std::runtime_error("Usage: ./machine <config> <binary>");

    MachineConfig cfg = parseConfig(argv[1]);
    ProcessorModel processorModel(cfg);
    processorModel.loadBinary(argv[2]);
    processorModel.process();
}