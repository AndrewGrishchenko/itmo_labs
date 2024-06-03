from pyKey import press, sendSequence
from time import sleep

def write(key):
    sendSequence(key)
    press("F5")
    sleep(0.05)

def enter_address(address):
    sendSequence(address)
    press("F4")

def enter_program(program):
    for kop in program:
        write(kop)

def enter_value(address, value):
    sendSequence(address)
    press("F4")
    write(value)

sleep(1)

program = ["0200", "EE18", "AE15", "0C00", "D686", "0800", "4E13", "EE12", "AF0E", "0C00", "D686", "0800", "0740", "4E0C", "EE0B", "AE09", "0C00",
           "D686", "0800", "0700", "4E05", "EE04", "0100"]

values = ["ACAB", "7CAB", "8976"]

enter_address("05A9")
enter_program(program)
enter_program(values)
enter_value("05C3", "E22D")


p_program = ["AC01", "F308", "6E0A", "F206", "F005", "4E07", "4C01", "4C01", "4E05", "CE01", "AE02", "EC01", "0A00", "F60F", "0019"]
enter_address("0686")
enter_program(p_program)