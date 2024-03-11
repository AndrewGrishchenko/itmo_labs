#!/bin/zsh
git add .
if [ -z "$1" ]
  then
    echo "no argument specified"
    exit
fi
git commit -m "$*"
git push "https://AndrewGrishchenko:${GIT_TOKEN}@github.com/AndrewGrishchenko/test.git"
