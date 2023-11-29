message = input()
nums = [int(x) for x in message]

if len(nums) != 7:
    print("Invalid message")
else:
    s1 = (nums[0] ^ nums[2] ^ nums[4] ^ nums[6])
    s2 = (nums[1] ^ nums[2] ^ nums[5] ^ nums[6])
    s3 = (nums[3] ^ nums[4] ^ nums[5] ^ nums[6])
    s = s1 + s2 * 2 + s3 * 4
    if s > 0:
        nums[s-1] = 1 - nums[s-1]
        print(f"Error in number {s}")
        print("Correct message:", ''.join(map(str, [nums[2], nums[4], nums[5], nums[6]])))
    else:
        print(f"Without errors")
        print("Message:", ''.join(map(str, [nums[2], nums[4], nums[5], nums[6]])))