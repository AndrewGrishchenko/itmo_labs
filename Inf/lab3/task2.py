#ISU 408498
#VARIANT 0

import re

tests = ["Небо печально / Утро казалось чужим",
         "Небо печально / Утро казалось чужим / Без солнца лучей",
         "не хокку",
         "На голой ветке / Ворон сидит одиноко / Осенний вечер"]

#expected results:
# Не хайку. Должно быть 3 строки.
# Хайку!
# Не хайку. Должно быть 3 строки.
# Не хайку.

for test in tests:
    hokku = test.split('/')
    if len(hokku) != 3:
        print("Не хайку. Должно быть 3 строки.")
    else:
        if [len(re.findall(r"[ёуеыаоэяию]", part, re.IGNORECASE)) for part in hokku] == [5, 7, 5]:
            print("Хайку!")
        else:
            print("Не хайку.")