cmake_minimum_required(VERSION 3.20)

project(DynamicServer)

add_executable(DynamicServer src/main.cpp gen/Dynamic.cpp)
set_target_properties(DynamicServer PROPERTIES CXX_STANDARD 11)
target_compile_definitions(DynamicServer PRIVATE ICE_CPP11_MAPPING)
target_link_libraries(DynamicServer PRIVATE /opt/Ice-3.7.9/lib/x86_64-linux-gnu/libIce++11.so)
target_include_directories(DynamicServer PRIVATE /opt/Ice-3.7.9/include PRIVATE gen)
