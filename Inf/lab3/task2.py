#ISU 408498
#VARIANT 0

import re

regex1 = r"(?:.*([уеыаояию]).*){5,}"
regex2 = r"(.+)/(.+)/(.+)"

tests = ["Вечер за окном.",
         "Вечер за окном. / Еще один день прожит. / Жизнь скоротечна...",
         "Просто текст",
         "Как вишня расцвела! / Она с коня согнала / И князя-гордеца."]

#expected results:

for test in tests:
    
    test2 = re.findall(regex2, test)
    if len(test2) != 3:
        print("Не хайку. Должно быть 3 строки.")
    else:
        if re.findall(regex1, test2):
            print("1")
        else:
            print("2")