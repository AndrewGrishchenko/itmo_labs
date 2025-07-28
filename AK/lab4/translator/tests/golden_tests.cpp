#include <gtest/gtest.h>
#include <filesystem>
#include <fstream>
#include <sstream>
#include <cstdlib>
#include <string>

namespace fs = std::filesystem;

std::string readFile(const fs::path& path) {
    std::ifstream in(path);
    if (!in) throw std::runtime_error("Failed to open file: " + path.string());
    std::stringstream buffer;
    buffer << in.rdbuf();
    return buffer.str();
}

std::string runCommand(const std::string& cmd) {
    int code = std::system(cmd.c_str());
    if (code != 0) {
        throw std::runtime_error("Command failed: " + cmd);
    }
    return "";
}

void runCommandInDir(const std::string& cmd, const fs::path& dir) {
    std::string full = "cd " + dir.string() + " && " + cmd;
    int ret = std::system(full.c_str());
    if (ret != 0)
        throw std::runtime_error("Command failed: " + full);
}

fs::path makeConfig(const fs::path& caseDir,
                    const fs::path& inputFile,
                    const fs::path& outputFile,
                    const fs::path& binFile) {

    fs::path tempConfig = caseDir / "auto_config.cfg";
    std::ofstream cfg(tempConfig);
    if (!cfg) throw std::runtime_error("Failed to write config");

    if (fs::exists(inputFile)) {
        cfg << "input_file: " << inputFile << "\n";
        cfg << "input_mode: stream\n";
        cfg << "schedule_start: 1900\n";
        cfg << "schedule_offset: 200\n";
    }

    cfg << "output_file: " << outputFile << "\n";

    return tempConfig;
}

class GoldenTest : public testing::TestWithParam<std::string> {};

class GoldenTestRunner : public testing::TestWithParam<std::string> {
    protected:
        void RunTest(const std::string& category) {
            std::string caseName = GetParam();
            fs::path caseDir = fs::path(TEST_CASES_DIR) / category / caseName;

            fs::path input = caseDir / "input.txt";
            fs::path output = caseDir / "output.txt";
            fs::path program = caseDir / "program.txt";
            fs::path binary = caseDir / "program.bin";
            fs::path config = caseDir / "config.cfg";
            fs::path expected = caseDir / "expected.txt";

            if (fs::exists(output)) fs::remove(output);
            if (fs::exists(binary)) fs::remove(binary);

            runCommand(std::string(TRANSLATOR_PATH) + " " + program.string() + " " + binary.string());

            fs::path autoConfig = makeConfig(caseDir, input, output, binary);

            runCommandInDir(std::string(MACHINE_PATH) + " auto_config.cfg program.bin", caseDir);

            std::string actualOut = readFile(output);
            std::string expectedOut = readFile(expected);

            EXPECT_EQ(actualOut, expectedOut);

            fs::remove(autoConfig);
            fs::remove(binary);
            fs::remove(output);
        }
};

class BasicTests : public GoldenTestRunner {};
class ControlFlowTests : public GoldenTestRunner {};
class FunctionTests : public GoldenTestRunner {};
class AlgoTests : public GoldenTestRunner {};

TEST_P(BasicTests, OutputMatchesExpected) {
    RunTest("basics");
}

TEST_P(ControlFlowTests, OutputMatchesExpected) {
    RunTest("control_flow");
}

TEST_P(FunctionTests, OutputMatchesExpected) {
    RunTest("function");
}

TEST_P(AlgoTests, OutputMatchesExpected) {
    RunTest("algo");
}

INSTANTIATE_TEST_SUITE_P(Basics, BasicTests, ::testing::Values(
    "hello",
    "hello_user_name",
    "cat",
    "array",
    "arithmetic"
));

INSTANTIATE_TEST_SUITE_P(ControlFlow, ControlFlowTests, ::testing::Values(
    "while_break",
    "nested_while"
));

INSTANTIATE_TEST_SUITE_P(Function, FunctionTests, ::testing::Values(
    // "recursion",
    "overload"
));

INSTANTIATE_TEST_SUITE_P(Algo, AlgoTests, ::testing::Values(
    "sort"
));

// INSTANTIATE_TEST_SUITE_P(AllCases, GoldenTest, ::testing::Values(
//     "hello",
//     "cat",
//     "hello_user_name",
//     "sort"
// ));
