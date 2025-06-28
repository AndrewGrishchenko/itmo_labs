cmake_minimum_required(VERSION 3.10.0)
project(translator VERSION 0.1.0)

file(GLOB TRANSLATOR_SOURCES
    translator/*.h
    translator/*.cpp
)
add_executable(translator ${TRANSLATOR_SOURCES})

file(GLOB MACHINE_SOURCES
    machine/*.h
    machine/*.cpp
)
add_executable(machine ${MACHINE_SOURCES})

include(FetchContent)
FetchContent_Declare(
    googletest
    URL https://github.com/google/googletest/archive/refs/tags/v1.14.0.zip
    DOWNLOAD_EXTRACT_TIMESTAMP true
)
FetchContent_MakeAvailable(googletest)

enable_testing()
add_subdirectory(tests)