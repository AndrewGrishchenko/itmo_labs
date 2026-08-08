from methods import (
    dangerous_mobs,
    dangerous_mobs_dimension,
    useful_mobs,
    rare_blocks,
    tool_for_block,
    tameable_mobs,
    boss_mobs,
    dangerous_and_useful,
    useful_mobs_dimension,
    mobs_drop_dimension,
    tool_can_mine_block,
    tool_blocks_can_mine,
)


def print_examples():
    print("Примеры доступных запросов (формат строки фиксированный):")
    print("  1) Какие мобы считаются опасными?")
    print("  2) Какие мобы считаются полезными?")
    print("  3) Какие мобы считаются опасными в измерении nether?")
    print("  4) Какие блоки считаются редкими ресурсами?")
    print("  5) Какой инструмент нужен, чтобы добыть блок diamond_ore?")
    print("  6) Каких мобов можно приручить и чем?")
    print("  7) Какие мобы считаются боссами?")
    print("  8) Какие мобы одновременно опасные и полезные?")
    print("  9) Какие полезные мобы спаунятся в измерении overworld?")
    print(" 10) Какие мобы в измерении nether дают дроп bone?")
    print(" 11) Может ли игрок с инструментом iron_pickaxe добыть блок obsidian?")
    print(" 12) Какие блоки может добыть инструмент stone_pickaxe?")
    print()

def dispatch(query: str):
    query = query.strip()

    # 1
    if query == "Какие мобы считаются опасными?":
        dangerous_mobs.handle()
        return

    # 2
    if query == "Какие мобы считаются полезными?":
        useful_mobs.handle()
        return

    # 3
    prefix_dim_danger = "Какие мобы считаются опасными в измерении "
    if query.startswith(prefix_dim_danger) and query.endswith("?"):
        dim = query[len(prefix_dim_danger):-1].strip()
        dangerous_mobs_dimension.handle(dim)
        return

    # 4
    if query == "Какие блоки считаются редкими ресурсами?":
        rare_blocks.handle()
        return

    # 5
    prefix_block_tool = "Какой инструмент нужен, чтобы добыть блок "
    if query.startswith(prefix_block_tool) and query.endswith("?"):
        block = query[len(prefix_block_tool):-1].strip()
        tool_for_block.handle(block)
        return

    # 6
    if query == "Каких мобов можно приручить и чем?":
        tameable_mobs.handle()
        return

    # 7
    if query == "Какие мобы считаются боссами?":
        boss_mobs.handle()
        return

    # 8
    if query == "Какие мобы одновременно опасные и полезные?":
        dangerous_and_useful.handle()
        return

    # 9
    prefix_dim_useful = "Какие полезные мобы спаунятся в измерении "
    if query.startswith(prefix_dim_useful) and query.endswith("?"):
        dim = query[len(prefix_dim_useful):-1].strip()
        useful_mobs_dimension.handle(dim)
        return

    # 10
    prefix_dim_drop = "Какие мобы в измерении "
    middle_dim_drop = " дают дроп "
    if query.startswith(prefix_dim_drop) and query.endswith("?") and middle_dim_drop in query:
        rest = query[len(prefix_dim_drop):-1]  # без '?'
        dim, item_part = rest.split(middle_dim_drop, 1)
        dim = dim.strip()
        item = item_part.strip()
        mobs_drop_dimension.handle(dim, item)
        return

    # 11
    prefix_tool_can = "Может ли игрок с инструментом "
    middle_tool_can = " добыть блок "
    if query.startswith(prefix_tool_can) and query.endswith("?") and middle_tool_can in query:
        rest = query[len(prefix_tool_can):-1]
        tool, block_part = rest.split(middle_tool_can, 1)
        tool = tool.strip()
        block = block_part.strip()
        tool_can_mine_block.handle(tool, block)
        return

    # 12
    prefix_tool_blocks = "Какие блоки может добыть инструмент "
    if query.startswith(prefix_tool_blocks) and query.endswith("?"):
        tool = query[len(prefix_tool_blocks):-1].strip()
        tool_blocks_can_mine.handle(tool)
        return

    print("Неизвестный запрос")
    exit(1)

def main():
    print_examples()

    try:
        query = input("> ")
    except EOFError:
        return

    dispatch(query)

if __name__ == "__main__":
    main()