% minecraft

% Mobs
mob(zombie).
mob(skeleton).
mob(creeper).
mob(enderman).
mob(spider).
mob(villager).
mob(iron_golem).
mob(blaze).
mob(hoglin).
mob(wither_skeleton).
mob(pig).
mob(cow).
mob(chicken).
mob(end_dragon).
mob(wither_boss).

% Blocks
block(dirt).
block(stone).
block(wood).
block(diamond_ore).
block(iron_ore).
block(gold_ore).
block(obsidian).
block(netherit_block).
block(sand).
block(glowstone).

% Tameable mobs
tameable(wolf, meat).
tameable(cat, fish).
tameable(horse, apple).

% Mob drops
drops(zombie, rotten_flesh).
drops(skeleton, bone).
drops(creeper, gunpowder).
drops(pig, porkchop).
drops(cow, leather).
drops(end_dragon, dragon_egg).
drops(wither_boss, nether_star).

% Tools
tool(wooden_pickaxe).
tool(stone_pickaxe).
tool(iron_pickaxe).
tool(diamond_pickaxe).

% Tool requirements
tool_required(diamond_ore, iron_pickaxe).
tool_required(obsidian, diamond_pickaxe).
tool_required(netherit_block, diamond_pickaxe).

% Tool levels
tool_level(wooden_pickaxe, 1).
tool_level(stone_pickaxe, 2).
tool_level(iron_pickaxe, 3).
tool_level(diamond_pickaxe, 4).

% Required tool level for block
required_tool_level(Block, Level) :-
    tool_required(Block, Tool),
    tool_level(Tool, Level).

% Can be block mined with tool
can_mine(Block, Tool) :-
    tool_required(Block, Tool), !.
can_mine(Block, any) :-
    \+ tool_required(Block, _).

can_mine_with(Block, Tool) :-
    required_tool_level(Block, Required),
    tool_level(Tool, Level),
    Level >= Required.

can_mine_with(Block, Tool) :-
    \+ tool_required(Block, _),
    tool(Tool).

can_mine_with(Block, any) :-
    \+ tool_required(Block, _).

% Dimension mob spawns
spawns_in(zombie, overworld).
spawns_in(skeleton, overworld).
spawns_in(blaze, nether).
spawns_in(hoglin, nether).
spawns_in(end_dragon, end).
spawns_in(enderman, end).

% Hostility
hostile(zombie).
hostile(skeleton).
hostile(creeper).
hostile(enderman).
hostile(blaze).
hostile(wither_skeleton).
hostile(end_dragon).
hostile(wither_boss).

% Dangerous jmobs
dangerous(X) :-
    mob(X),
    hostile(X).

% Useful
useful(X) :-
    tameable(X, _).
useful(X) :-
    drops(X, Item),
    Item \= rotten_flesh.

% Rare
rare(X) :-
    tool_required(X, _).

% Bosses
boss(end_dragon).
boss(wither_boss).

% Existence
exists_in_world(X) :-
    mob(X),
    spawns_in(X, _).