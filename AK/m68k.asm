.data
input_addr: .word 0x80             
output_addr: .word 0x84 

.text
_start:
    movea.l 0x1000, A7
    movea.l input_addr, A0
    movea.l (A0), A0
    movea.l output_addr, A1
    movea.l (A1), A1

    xor.l D1, D1
    xor.l D2, D2

    move.b (A0), D0
    and.l 0xFF, D0

    jsr sum_word

    move.l D1, (A1)
    move.l D2, (A1)

    halt

sum_word:
    beq sum_word_ret
sum_word_do:
    move.l (A0), D3
    jsr perform_sum
    sub.b 1, D0
    bne sum_word_do
sum_word_ret:
    rts

perform_sum:
    cmp.l 0, D3
    bpl perform_sum_add
    sub.l 1, D1
perform_sum_add:
    add.l D3, D2
    bcc perform_sum_ret
    add.l 1, D1
perform_sum_ret:
    rts