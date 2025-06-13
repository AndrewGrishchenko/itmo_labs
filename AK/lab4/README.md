# csa-lab4

Variant : alg | acc | neum | hw | tick | binary | trap | mem | cstr | prob1 | vector
- alg: java/javscript/lua + AST
- acc: аккумулятор
- neum: фон Неймановская архитектура
- hw: hardwired CU
- tick: с точностью до такта
- binary: бинарное представление, отладочный вывод
- trap: ввод-вывод через токены с системой прерываний
- mem: memory-mapped IO
- cstr: c-style string
- prob1: 
- vector: векторные регистры

Команды:
```
<program> ::= { <statement> }

<statement> ::= <var_decl> ";"
              | <assign_stmt> ";"
              | <load_stmt> ";"
              | <store_stmt> ";"
              | <arith_stmt> ";"
              | <if_stmt>
              | <while_stmt>
              | <function_decl>
              | <function_call> ";"
              | <return_stmt> ";"
              | <print_stmt> ";"

<var_decl> ::= "var" <identifier> "=" <expression>
<assign_stmt> ::= <identifier> "=" <expression>
<load_stmt> ::= "acc" "=" "load" "(" <identifier> ")"
<store_stmt> ::= "store" "(" <identifier> ")"
<arith_stmt> ::= "acc" "=" "acc" <arith_op> <expression>
<arith_op> ::= "+" | "-" | "*" | "/"

<if_stmt> ::= "if" "(" <condition> ")" <block> [ "else" <block> ]
<while_stmt> ::= "while" "(" <condition> ")" <block>
<function_decl> ::= "func" <identifier> "(" [ <param_list> ] ")" <block>
<return_stmt> ::= "return" [ <expression> ]
<print_stmt> ::= "print" "(" <expression> ")"

<function_call> ::= <identifier> "(" [ <arg_list> ] ")"

<block> ::= "{" { <statement> } "}"

<condition> ::= <bool_term> { "||" <bool_term> }
<bool_term> ::= <bool_factor> { "&&" <bool_factor> }
<bool_factor> ::= "!" <bool_factor>
                | "(" <condition> ")"
                | <comparison>
<comparison> ::= <expression> <comp_op> <expression>
<comp_op> ::= "==" | "!=" | "<" | ">" | "<=" | ">="

<expression> ::= <term> { <add_op> <term> }
<add_op> ::= "+" | "-"
<term> ::= <factor> { <mul_op> <factor> }
<mul_op> ::= "*" | "/"
<factor> ::= <number>
           | <identifier>
           | "acc"
           | "(" <expression> ")"
           | <function_call>

<param_list> ::= <identifier> { "," <identifier> }
<arg_list> ::= <expression> { "," <expression> }

<identifier> ::= <letter> { <letter> | <digit> | "_" }
<number> ::= <digit> { <digit> }

<letter> ::= "a" | ... | "z" | "A" | ... | "Z"
<digit> ::= "0" | ... | "9"
```

## Стратегия вычислений
жадная (eager) / строгая (strict)
- все аргументы выражений вычисляются сразу перед выполнением операции или передачи в функцию
- нет отложенного исполнения или ленивых выражений

Последовательность исполнения:
- программа исполняется сверху вниз, в порядке следования операндов
- управляющие конструкции регулируют порядок переходов

Порядок вычисления выражений:
- в выражениях операции выполняются слева направо, с учетом приоритета
- *, / выше +, -
- () имеют высший приоритет

Аккумулятор:
- acc - специальный регистр, хранящий промежуточное значение
- может быть явно загружен значением (`acc = load(x)`), или участвовать в арифметике (`acc = acc + 5`)

## Область видимости
Лексическая область видимости
- идентификаторы видимы в той области, где они были определены

Области:
- Глобальная область - переменные, определенные вне функций
- Локальная область - переменные, определенный внутри функций или блока `{...}`

Функции:
- Поддерживают собственную локальную область видимости
- Параметры функции считаются локальными переменными

Вложенные блоки:
- Поддерживают вложенные области видимости
- Переменные из внешней области могут быть доступны во внутренней, если не перекрыты

## Типизация
Динамическая, неявная
- Переменная создается по `var`, и тип определяется по присвоенному значению
- Повторное присваивание может менять тип переменной ???????????????????????????????????????????????????????
- Операции на несовместимых типах вызывают ошибку во время исполнения

## Литералы
Числовые литералы:
- Последоательности цифр, опицонально с префиксом `-`

Строковые литералы:
- В двойных кавычках `"..."`
- Поддерживаются escape-последовательности `\n`, `\t`, `\\`, `\"`, `\0` и т.д.

## Поведение при смешанных типах: ???????????????????
- Арифметика разрешена только между числами
- Конкатенация строк возможна с `+`, если оба операнда - строки
- Сравнение (`==`, `>=`, `<=`) допускается между значениями одного типа
- - Между строками по лексикографическому порядку


# хуй знает какой раздел
Набор машинных команд:
|      команда       |             описание              |
| ------------------ | --------------------------------- |
| add \<operand\>    | AC <- AC + \<operand\>            |
| sub \<operand\>    | AC <- AC - \<operand\>            |
| mul \<operand\>    | AC <- AC * \<operand\>            |
| div \<operand\>    | AC <- AC / \<operand\>            |
| rem \<operand\>    | AC <- AC % \<operand\>            |
| inc                | AC <- AC + 1                      |
| dec                | AC <- AC - 1                      |
| not                | AC <- -AC                         |
| cla                | AC <- 0                           |
| jmp \<operand\>    | IP <- MEM(\<operand\>)            |
| je \<operand\>     | IF ZF: IP <- MEM(\<operand\>)     |
| jne \<operand\>    | IF not ZF: IP <- MEM(\<operand\>) |
| jn \<operand\>     | IF NF: IP <- MEM(\<operand\>)     |
| jnn \<operand\>    | IF not NF: IP <- MEM(\<operand\>) |
| ld \<operand\>     | AC <- MEM(\<operand\>)            |
| st \<operand\>     | AC -> MEM(\<operand\>)            |
| halt               | stop machine                      |