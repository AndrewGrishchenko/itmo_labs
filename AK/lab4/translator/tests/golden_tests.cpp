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

class GoldenTest : public testing::TestWithParam<std::string> {};

class GoldenTestRunner : public testing::TestWithParam<std::string> {
    protected:
        void RunTest(const std::string& category) {
            std::string caseName = GetParam();

            fs::path caseDir = fs::path(TEST_CASES_DIR) / category / caseName;
            fs::path expectedDir = caseDir / "expected";

            fs::path configFile = caseDir / "config.cfg";
            fs::path inputFile = caseDir / "input.txt";
            fs::path programFile = caseDir / "program.txt";
            fs::path binaryFile = caseDir / "program.bin";
            
            fs::path outputFile = caseDir / "output.txt";
            fs::path reprFile = caseDir / "repr.txt";
            fs::path hashFile = caseDir / "hash.txt";
            
            fs::path expectedOutputFile = expectedDir / "output.txt";
            fs::path expectedReprFile = expectedDir / "repr.txt";
            fs::path expectedHashFile = expectedDir / "hash.txt";

            runCommand(std::string(TRANSLATOR_PATH) + " " + programFile.string() + " " + binaryFile.string());
            runCommandInDir(std::string(MACHINE_PATH) + " " + configFile.string() + " " + binaryFile.string(), caseDir);

            std::string actualOutput = readFile(outputFile);
            std::string expectedOutput = readFile(expectedOutputFile);
            EXPECT_EQ(actualOutput, expectedOutput);

            std::string actualRepr = readFile(reprFile);
            std::string expectedRepr = readFile(expectedReprFile);
            EXPECT_EQ(actualRepr, expectedRepr);

            std::string actualHash = readFile(hashFile);
            std::string expectedHash = readFile(expectedHashFile);
            EXPECT_EQ(actualHash, expectedHash);

            fs::remove(binaryFile);
            fs::remove(outputFile);
            fs::remove(reprFile);
            fs::remove(hashFile);
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
    "recursion",
    "overload"
));

INSTANTIATE_TEST_SUITE_P(Algo, AlgoTests, ::testing::Values(
    "sort",
    "palindrome"
));