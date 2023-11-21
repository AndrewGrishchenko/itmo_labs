#ISU 408498
#SMILE :-{p

import re

regex = r":-{p"

tests = ["blbalbal  balb :-{p fs:(D@ XC:<DK :-{p >SMAIJR: SO:-{p) ):-}<", #3
         "Lorem ipsum dolor, :} consectetur :-{p adipiscing elit. :{p", #1
         "consectetur adipiscing elit :<p", #0
         "sed :-{pdo eius:-{pmod te:_mp{por", #2
         "incididunt :-{put labore;-{p"] #1

#expected results:
#3
#1
#0
#2
#1

for test in tests:
    print(len(re.findall(regex, test)))