#ISU 408498
#VARIANT 3

import re

group = "P0000"
regex = rf'(.).* \1\.\1\. {group}'

tests = ["Петров П.П. P0000",
         "Анищенко А.А. P33113",
         "Примеров Е.В. P0000",
         "Иванов И.И. P0000"]

#expected results:
# Анищенко А.А. P33113
# Примеров Е.В. P0000

for test in tests:
    if not re.compile(regex).match(test):
        print(test)


