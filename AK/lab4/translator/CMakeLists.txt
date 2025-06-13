cmake_minimum_required(VERSION 3.10.0)
project(translator VERSION 0.1.0)

file(GLOB SOURCES *.cpp)

add_executable(translator ${SOURCES})
target_link_libraries(translator)
