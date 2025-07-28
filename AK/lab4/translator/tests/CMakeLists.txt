file(GLOB TEST_CASE_DIRS RELATIVE ${CMAKE_CURRENT_SOURCE_DIR}/cases ${CMAKE_CURRENT_SOURCE_DIR}/cases/*)

add_executable(golden_tests golden_tests.cpp)
target_link_libraries(golden_tests gtest_main)

target_compile_definitions(golden_tests PRIVATE
    TRANSLATOR_PATH="$<TARGET_FILE:translator>"
    MACHINE_PATH="$<TARGET_FILE:machine>"
    TEST_CASES_DIR="${CMAKE_CURRENT_SOURCE_DIR}/cases"
)

include(GoogleTest)
gtest_discover_tests(golden_tests)