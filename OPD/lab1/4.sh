#0 дозаписывать или перезаписывать ошибки?

cd $HOME/lab0

#1 нет прав к директории и wc ломается
#echo "1."
#cd cacnea4
#wc -l $(ls -p | grep -v /$) | sort
#cd ..

#2 только файлы? если нет то можно ли grep studs? и можно ли сортировать по размеру файла в блоках
echo -e '\n2.'
ls -lRS 2> /tmp/error | grep ' l[^ ]' | grep -v ^d | head -n4

#3
echo -e '\n3.'
cd larvesta8
cat -n $(ls) 2> /tmp/error | grep t$
cd ..

#4 только файлы?
echo -e '\n4.'
ls -ltuRr 2> /dev/null | grep ^[-l] | head -n4

#5
echo -e '\n5.'
cat flareon0 2> /dev/null | grep -v t$

#6
echo -e '\n6.'
ls -lR | grep 4$ | sort -rk2 | head -n4
