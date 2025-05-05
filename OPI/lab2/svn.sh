#!/bin/bash

revert() {
    svn status | grep '^ *C' | awk '{print $2}' | while read path; do
        svn revert "$path"
        svn resolve --accept working "$path"
    done
}

rename() {
    svn status | grep '^ *R *\+' | awk '{print $3}' | while read path; do
        svn resolve --accept theirs-full "$path"
    done
}

deleted() {
    svn status | grep '^ *\!' | awk '{print $3}' | while read path; do
        svn revert "$path"
        rm $path
    done
}

svnadmin create repo
REPO_URL="file://$(pwd)/repo"

cd repo
svn mkdir -m "project structure" $REPO_URL/trunk $REPO_URL/branches
cd ..

svn checkout $REPO_URL/trunk/ wc
cd wc

unzip -o ../commits/commit0.zip -d .
svn add *
svn commit -m "r0" --username=red
svn update

svn copy $REPO_URL/trunk $REPO_URL/branches/branch5 -m "create branch5"
svn switch $REPO_URL/branches/branch5
svn rm *
unzip -o ../commits/commit1.zip -d .
svn add *
svn commit -m "r1" --username=red
svn update

svn copy $REPO_URL/branches/branch5 $REPO_URL/branches/branch4 -m "create branch4"
svn switch $REPO_URL/branches/branch4
svn rm *
unzip -o ../commits/commit2.zip -d .
svn add *
svn commit -m "r2" --username=blue
svn update

svn copy $REPO_URL/branches/branch4 $REPO_URL/branches/branch2 -m "create branch2"
svn switch $REPO_URL/branches/branch2
svn rm *
unzip -o ../commits/commit3.zip -d .
svn add *
svn commit -m "r3" --username=blue
svn update

svn copy $REPO_URL/branches/branch2 $REPO_URL/branches/branch6 -m "create branch6"
svn switch $REPO_URL/branches/branch6
svn rm *
unzip -o ../commits/commit4.zip -d .
svn add *
svn commit -m "r4" --username=red
svn update

svn switch $REPO_URL/branches/branch4
svn rm *
unzip -o ../commits/commit5.zip -d .
svn add *
svn commit -m "r5" --username=blue
svn update

svn copy $REPO_URL/branches/branch4 $REPO_URL/branches/branch7 -m "create branch7"
svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit6.zip -d .
svn add *
svn commit -m "r6" --username=red
svn update

svn switch $REPO_URL/branches/branch6
svn rm *
unzip -o ../commits/commit7.zip -d .
svn add *
svn commit -m "r7" --username=red
svn update

svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit8.zip -d .
svn add *
svn commit -m "r8" --username=red
svn update

svn switch $REPO_URL/branches/branch4
svn rm *
unzip -o ../commits/commit9.zip -d .
svn add *
svn commit -m "r9" --username=blue
svn update

svn copy $REPO_URL/branches/branch4 $REPO_URL/branches/branch3 -m "create branch3"
svn switch $REPO_URL/branches/branch3
svn rm *
unzip -o ../commits/commit10.zip -d .
svn add *
svn commit -m "r10" --username=red
svn update

svn switch $REPO_URL/branches/branch4
svn rm *
unzip -o ../commits/commit11.zip -d .
svn add *
svn commit -m "r11" --username=blue
svn update

svn switch $REPO_URL/branches/branch6
svn rm *
unzip -o ../commits/commit12.zip -d .
svn add *
svn commit -m "r12" --username=red
svn update

svn switch $REPO_URL/branches/branch2
svn rm *
unzip -o ../commits/commit13.zip -d .
svn add *
svn commit -m "r13" --username=blue
svn update

svn switch $REPO_URL/trunk
svn rm *
unzip -o ../commits/commit14.zip -d .
svn add *
svn commit -m "r14" --username=red
svn update

svn switch $REPO_URL/branches/branch2
svn rm *
unzip -o ../commits/commit15.zip -d .
svn add *
svn commit -m "r15" --username=blue
svn update

svn switch $REPO_URL/branches/branch5
svn rm *
unzip -o ../commits/commit16.zip -d .
svn add *
svn commit -m "r16" --username=red
svn update

svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit17.zip -d .
svn add *
svn commit -m "r17" --username=red
svn update

svn switch $REPO_URL/branches/branch4
svn rm *
unzip -o ../commits/commit18.zip -d .
svn add *
svn commit -m "r18" --username=blue
svn update

svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit19.zip -d .
svn add *
svn commit -m "r19" --username=red
svn update

svn switch $REPO_URL/branches/branch3
svn rm *
unzip -o ../commits/commit20.zip -d .
svn add *
svn commit -m "r20" --username=red
svn update

svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit21.zip -d .
svn add *
svn commit -m "r21" --username=red
svn update

svn rm *
unzip -o ../commits/commit22.zip -d .
svn add *
svn commit -m "r22" --username=red
svn update

svn rm *
unzip -o ../commits/commit23.zip -d .
svn add *
svn commit -m "r23" --username=red
svn update

svn rm *
unzip -o ../commits/commit24.zip -d .
svn add *
svn commit -m "r24" --username=red
svn update

svn switch $REPO_URL/branches/branch3
svn rm *
unzip -o ../commits/commit25.zip -d .
svn add *
svn commit -m "r25" --username=red
svn update

svn switch $REPO_URL/branches/branch5
svn rm *
unzip -o ../commits/commit26.zip -d .
svn add *
svn commit -m "r26" --username=red
svn update

svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit27.zip -d .
svn add *
svn commit -m "r27" --username=red
svn update

svn switch $REPO_URL/branches/branch4
svn rm *
unzip -o ../commits/commit28.zip -d .
svn add *
svn commit -m "r28" --username=blue
svn update

svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit29.zip -d .
svn add *
svn commit -m "r29" --username=red
svn update

svn switch $REPO_URL/trunk
svn rm *
unzip -o ../commits/commit30.zip -d .
svn add *
svn commit -m "r30" --username=red
svn update

svn rm *
unzip -o ../commits/commit31.zip -d .
svn add *
svn commit -m "r31" --username=red
svn update

svn switch $REPO_URL/branches/branch7
svn rm *
unzip -o ../commits/commit32.zip -d .
svn add *
svn commit -m "r32" --username=red
svn update

svn switch $REPO_URL/branches/branch5
svn rm *
unzip -o ../commits/commit33.zip -d .
svn add *
svn commit -m "r33" --username=red
svn update

svn switch $REPO_URL/branches/branch4
svn rm *
unzip -o ../commits/commit34.zip -d .
svn add *
svn commit -m "r34" --username=blue
svn update

svn switch $REPO_URL/branches/branch2
svn rm *
unzip -o ../commits/commit35.zip -d .
svn add *
svn commit -m "r35" --username=blue
svn update

svn switch $REPO_URL/branches/branch6
svn rm *
unzip -o ../commits/commit36.zip -d .
svn add *
svn commit -m "r36" --username=red
svn update

svn switch $REPO_URL/branches/branch2
svn merge --non-interactive $REPO_URL/branches/branch6
revert
rename
svn rm *
unzip -o ../commits/commit37.zip -d .
svn add *
svn commit -m "r37" --username=blue
svn update

svn switch $REPO_URL/branches/branch4
svn merge --non-interactive $REPO_URL/branches/branch2
revert
rename
svn rm *
unzip -o ../commits/commit38.zip -d .
svn add *
svn commit -m "r38" --username=blue
svn update

svn switch $REPO_URL/branches/branch3
svn merge --non-interactive $REPO_URL/branches/branch4
revert
rename
svn rm *
unzip -o ../commits/commit39.zip -d .
svn add *
svn commit -m "r39" --username=red
svn update

svn switch $REPO_URL/branches/branch5
svn merge --non-interactive $REPO_URL/branches/branch3
revert
rename
deleted
svn rm *
unzip -o ../commits/commit40.zip -d .
svn add *
svn commit -m "r40" --username=red
svn update

svn rm *
unzip -o ../commits/commit41.zip -d .
svn add *
svn commit -m "r41" --username=red
svn update

svn rm *
unzip -o ../commits/commit42.zip -d .
svn add *
svn commit -m "r42" --username=red
svn update

svn switch $REPO_URL/branches/branch7
svn merge --non-interactive $REPO_URL/branches/branch5
revert
rename
deleted
svn rm *
unzip -o ../commits/commit43.zip -d .
svn add *
svn commit -m "r43" --username=red
svn update

svn rm *
unzip -o ../commits/commit44.zip -d .
svn add *
svn commit -m "r44" --username=red
svn update

svn switch $REPO_URL/trunk
svn merge --non-interactive $REPO_URL/branches/branch7
revert
rename
svn rm *
unzip -o ../commits/commit45.zip -d .
svn add *
svn commit -m "r45" --username=red
svn update

svn rm *
unzip -o ../commits/commit46.zip -d .
svn add *
svn commit -m "r46" --username=red
svn update