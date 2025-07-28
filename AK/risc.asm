.data
    input_addr:  .word 0x80
    output_addr: .word 0x84
    mask: .word 0xFF000000

.text
.org 0x90
_start:
    lui sp, 1

    lui t0, %hi(input_addr)
    addi t0, t0, %lo(input_addr)
    lw t0, 0(t0)

    addi a0, a0, 4
    addi a1, a1, 1
    lw a2, 0(t0)

    jal ra, little_to_big_endian_rec

    lui t0, %hi(output_addr)
    addi t0, t0, %lo(output_addr)
    lw t0, 0(t0)

    sw a3, 0(t0)

    halt

little_to_big_endian_rec:
    addi sp, sp, -4
    sw ra, 0(sp)

    ; a0 - byte count
    ; a1 - current byte [1; a0]
    ; a2 - number
    ; a3 - result number

    ; t0 - current number
    ; t1 - mask
    ; t2 - shift
    ; t3 - 8

    lui t1, %hi(mask)
    addi t1, t1, %lo(mask)
    lw t1, 0(t1)

    mv t3, zero
    addi t3, t3, 8

    mv t2, a0
    sub t2, t2, a1
    mul t2, t2, t3

    srl t1, t1, t2
    and t0, a2, t1
    sll t0, t0, t2

    mv t2, a1
    addi t2, t2, -1
    mul t2, t2, t3

    srl t0, t0, t2
    or a3, a3, t0

    mv t0, a1
    sub t0, a0, a1
    beqz t0, little_to_big_endian_end
    
    addi a1, a1, 1
    jal ra, little_to_big_endian_rec

little_to_big_endian_end:
    lw ra, 0(sp)
    addi sp, sp, 4
    jr ra