#!/bin/zsh
if [ -z "$1" ]
  then
    echo "no argument specified"
    exit
fi
git add .
git commit -m "$*"
git push "https://AndrewGrishchenko:${GIT_TOKEN}@github.com/AndrewGrishchenko/itmo_labs.git"
