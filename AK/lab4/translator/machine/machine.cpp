#include "processorModel.h"

int main(int argc, char* argv[]) {
    if (argc != 2)
        throw std::runtime_error("Usage: ./machine <filename>");

    ProcessorModel processorModel;
    processorModel.loadBinary(argv[1]);
    processorModel.process();
}