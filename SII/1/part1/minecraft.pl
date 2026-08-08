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

% Drops
drops(zombie, rotten_flesh).
drops(skeleton, bone).
drops(creeper, gunpowder).
drops(pig, porkchop).
drops(cow, leather).
drops(end_dragon, dragon_egg).
drops(wither_boss, nether_star).

% Tools
tool_required(diamond_ore, iron_pickaxe).
tool_required(obsidian, diamond_pickaxe).
tool_required(netherit_block, diamond_pickaxe).

% Biomes of mob existance
spawns_in(zombie, overworld).
spawns_in(skeleton, overworld).
spawns_in(blaze, nether).
spawns_in(hoglin, nether).
spawns_in(end_dragon, end).
spawns_in(enderman, end).

% Hostile rule
hostile(zombie).
hostile(skeleton).
hostile(creeper).
hostile(enderman).
hostile(blaze).
hostile(wither_skeleton).
hostile(end_dragon).
hostile(wither_boss).

dangerous(X) :- mob(X), hostile(X).

% Useful rule
useful(X) :- tameable(X, _).
useful(X) :- drops(X, Item), Item \= rotten_flesh.

% Rare rule
rare(X) :- tool_required(X, _).

% Boss mob
boss(end_dragon).
boss(wither_boss).

% Obtaining block requires special tool
can_mine(Block, Tool) :- tool_required(Block, Tool).
can_mine(Block, Tool) :- \+ tool_required(Block, _), Tool = any.

% Mob existance
exists_in_world(X) :- mob(X), spawns_in(X,_).