#!/bin/bash

red() {
    git config --local user.name red
    git config --local user.email red@example.com
}

blue() {
    git config --local user.name blue
    git config --local user.email blue@example.com
}

rm -rf .git
rm -f .gitignore

touch .gitignore
echo -e "commits" >> .gitignore
echo -e "git.sh" >> .gitignore

rm -rf src
mkdir src

git init

red
git checkout -b branch1
unzip -o commits/commit0.zip -d src
git add .
git commit -m "r0" --allow-empty

git checkout -b branch5
unzip -o commits/commit1.zip -d src
git add .
git commit -m "r1" --allow-empty

blue
git checkout -b branch4
unzip -o commits/commit2.zip -d src
git add .
git commit -m "r2" --allow-empty

git checkout -b branch2
unzip -o commits/commit3.zip -d src
git add .
git commit -m "r3" --allow-empty

red
git checkout -b branch6
unzip -o commits/commit4.zip -d src
git add .
git commit -m "r4" --allow-empty

blue
git checkout branch4
unzip -o commits/commit5.zip -d src
git add .
git commit -m "r5" --allow-empty

red
git checkout -b branch7
unzip -o commits/commit6.zip -d src
git add .
git commit -m "r6" --allow-empty

git checkout branch6
unzip -o commits/commit7.zip -d src
git add .
git commit -m "r7" --allow-empty

git checkout branch7
unzip -o commits/commit8.zip -d src
git add .
git commit -m "r8" --allow-empty

blue
git checkout branch4
unzip -o commits/commit9.zip -d src
git add .
git commit -m "r9" --allow-empty

red
git checkout -b branch3
unzip -o commits/commit10.zip -d src
git add .
git commit -m "r10" --allow-empty

blue
git checkout branch4
unzip -o commits/commit11.zip -d src
git add .
git commit -m "r11" --allow-empty

red
git checkout branch6
unzip -o commits/commit12.zip -d src
git add .
git commit -m "r12" --allow-empty

blue
git checkout branch2
unzip -o commits/commit13.zip -d src
git add .
git commit -m "r13" --allow-empty

red
git checkout branch1
unzip -o commits/commit14.zip -d src
git add .
git commit -m "r14" --allow-empty

blue
git checkout branch2
unzip -o commits/commit15.zip -d src
git add .
git commit -m "r15" --allow-empty

red
git checkout branch5
unzip -o commits/commit16.zip -d src
git add .
git commit -m "r16" --allow-empty

git checkout branch7
unzip -o commits/commit17.zip -d src
git add .
git commit -m "r17" --allow-empty

blue
git checkout branch4
unzip -o commits/commit18.zip -d src
git add .
git commit -m "r18" --allow-empty

red
git checkout branch7
unzip -o commits/commit19.zip -d src
git add .
git commit -m "r19" --allow-empty

git checkout branch3
unzip -o commits/commit20.zip -d src
git add .
git commit -m "r20" --allow-empty

git checkout branch7
unzip -o commits/commit21.zip -d src
git add .
git commit -m "r21" --allow-empty

unzip -o commits/commit22.zip -d src
git add .
git commit -m "r22" --allow-empty

unzip -o commits/commit23.zip -d src
git add .
git commit -m "r23" --allow-empty

unzip -o commits/commit24.zip -d src
git add .
git commit -m "r24" --allow-empty

git checkout branch3
unzip -o commits/commit25.zip -d src
git add .
git commit -m "r25" --allow-empty

git checkout branch5
unzip -o commits/commit26.zip -d src
git add .
git commit -m "r26" --allow-empty

git checkout branch7
unzip -o commits/commit27.zip -d src
git add .
git commit -m "r27" --allow-empty

blue
git checkout branch4
unzip -o commits/commit28.zip -d src
git add .
git commit -m "r28" --allow-empty

red
git checkout branch7
unzip -o commits/commit29.zip -d src
git add .
git commit -m "r29" --allow-empty

git checkout branch1
unzip -o commits/commit30.zip -d src
git add .
git commit -m "r30" --allow-empty

unzip -o commits/commit31.zip -d src
git add .
git commit -m "r31" --allow-empty

git checkout branch7
unzip -o commits/commit32.zip -d src
git add .
git commit -m "r32" --allow-empty

git checkout branch5
unzip -o commits/commit33.zip -d src
git add .
git commit -m "r33" --allow-empty

blue
git checkout branch4
unzip -o commits/commit34.zip -d src
git add .
git commit -m "r34" --allow-empty

git checkout branch2
unzip -o commits/commit35.zip -d src
git add .
git commit -m "r35" --allow-empty

red
git checkout branch6
unzip -o commits/commit36.zip -d src
git add .
git commit -m "r36" --allow-empty

blue
git checkout branch2
git merge --no-commit -Xours branch6
unzip -o commits/commit37.zip -d src
git add .
git commit -m "r37" --allow-empty

git checkout branch4
git merge --no-commit -Xours branch2
unzip -o commits/commit38.zip -d src
git add .
git commit -m "r38" --allow-empty

red
git checkout branch3
git merge --no-commit -Xours branch4
unzip -o commits/commit39.zip -d src
git add .
git commit -m "r39" --allow-empty

git checkout branch5
git merge --no-commit -Xours branch3
unzip -o commits/commit40.zip -d src
git add .
git commit -m "r40" --allow-empty

unzip -o commits/commit41.zip -d src
git add .
git commit -m "r41" --allow-empty

unzip -o commits/commit42.zip -d src
git add .
git commit -m "r42" --allow-empty

git checkout branch7
git merge --no-commit -Xours branch5
unzip -o commits/commit43.zip -d src
git add .
git commit -m "r43" --allow-empty

unzip -o commits/commit44.zip -d src
git add .
git commit -m "r44" --allow-empty

git checkout branch1
git merge --no-commit -Xours branch7
unzip -o commits/commit45.zip -d src
git add .
git commit -m "r45" --allow-empty

unzip -o commits/commit46.zip -d src
git add .
git commit -m "r46" --allow-empty


git log --graph --abbrev-commit --decorate --format=format:'%C(bold blue)%h%C(reset) - %C(bold green)(%ar)%C(reset) %C(white)%s%C(reset) %C(dim white)- %an%C(reset)%C(auto)%d%C(reset)' --all