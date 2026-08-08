% queries

% Print all mobs
run_all_mobs :-
    mob(X),
    writeln(X),
    fail; true.

% Print all blocks
run_all_blocks :-
    block(B),
    writeln(B),
    fail; true.

% Which tool to use to obtain
what_tool(Block) :-
    can_mine(Block, Tool),
    format("Block ~w can be mined with: ~w~n", [Block, Tool]).

% Peaceful mobs
peaceful_mobs :-
    mob(X),
    \+ hostile(X),
    writeln(X),
    fail; true.

% Dangerous or useful mobs
dangerous_or_useful(X) :-
    (dangerous(X); useful(X)),
    write(X), nl.

% Only dangerous mobs
print_dangerous :-
    dangerous(X),
    format("Dangerous mob: ~w~n", [X]),
    fail; true.

% Rare blocks
print_rare_blocks :-
    rare(X),
    format("Rare block: ~w~n", [X]),
    fail; true.

% Find hostile nether mobs
dangerous_nether_mobs :-
    spawns_in(X, nether),
    dangerous(X),
    format("Dangerous Nether mob: ~w~n", [X]),
    fail; true.

% Drops of useful mobs
print_useful_drops :-
    useful(Mob),
    drops(Mob, Item),
    format("Useful mob ~w drops: ~w~n", [Mob, Item]),
    fail; true.