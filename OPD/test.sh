for var in $(ls -p some | grep -v /)
do
    cat -n some/$var 2> err
done
