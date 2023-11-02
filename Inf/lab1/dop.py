def to_fib(num):
    num = int(num)
    fib = [1, 1]  
    while fib[-1] < num:
        fib.append(fib[-1]+fib[-2])
    
    res = ['0']*(len(fib)-2)

    while num > 0:
        maxx = [x for x in fib if x <= num][-1]
        res[-fib.index(maxx, 1)] = '1'
        num -= maxx

    res = ''.join(res)
    while '11' in res:
        if res.index('11') == 0:
            res.replace('11', '100', 1)
        else:
            res.replace('011', '100', 1)

    return int(res)
    

print(to_fib(input()))