section .data
    pi dq 3.141592653589793
    err_message db "invalid input", 0
    ; input db 20
    result dq 0.0
    divisor_two dq 2.0

    num_minus_nine dq -9.0
    num_minus_five dq -5.0
    num_minus_four dq -4.0
    num_zero dq 0.0
    num_two dq 2.0
    num_four dq 4.0
    num_five dq 5.0
    num_seven dq 7.0

    ; int_part db 10 dup(0)
    ; frac_part db 10 dup(0)

    int_part dq 0.0
    frac_part dq 0.0
    is_neg dw 0
    ten_power dq 0.0
    precision dq 1000.0

section .bss
    input resb 32
    num resq 1
    buf resb 32
    out_buffer resb 64

section .text
    global _start

_start:
    ; rax rdi rsi rdx
    mov rax, 0
    mov rdi, 0
    mov rsi, input
    mov rdx, 32
    syscall

    dec rax

    mov rcx, 0
    mov rdx, 0
    mov r10, 0
    mov rdi, 0
    mov [int_part], rdi
    mov [frac_part], rdi
    mov [result], rdi
    mov [ten_power], rdi
    finit
    
    cmp byte [rsi], '-'
    jne it_int

    inc rcx
    mov byte [is_neg], 1
it_int:
    cmp rcx, rax
    jge end_read

    cmp byte [rsi+rcx], '.'
    je it_frac

    mov dil, byte [rsi+rcx]
    sub dil, '0'

    mov rdx, [int_part]
    imul rdx, 10
    add rdx, rdi
    mov [int_part], rdx

    inc rcx
    jmp it_int
it_frac:
    inc rcx

    cmp rcx, rax
    jge end_read

    mov dil, byte [rsi+rcx]
    sub dil, '0'

    mov rdx, [frac_part]
    imul rdx, 10
    add rdx, rdi
    mov [frac_part], rdx

    inc r10
    jmp it_frac
end_read:
    mov rax, 1
    mov rdx, 0

    mov rbx, 10
.calc_power:
    cmp r10, 0
    je .done_perm
    imul rax, rbx
    dec r10
    jmp .calc_power
.done_perm:
    mov qword [ten_power], rax
    fild qword [ten_power]
    fstp qword [ten_power]

    fild qword [int_part]
    fstp qword [int_part]

    fild qword [frac_part]
    fstp qword [frac_part]

    fld qword [frac_part]
    fdiv qword [ten_power]
    fadd qword [int_part]
    fstp qword [result]

    mov rdi, [result]

    mov al, byte [is_neg]
    cmp rax, 0
    je is_pos

    fld qword [result]
    fchs
    fstp qword [result]
is_pos:
    mov rdi, [int_part]
    mov rdi, [frac_part]
    mov rdi, [result]
; calculate
calc:
    fld qword [result]

    fld qword [num_minus_nine]
    fcomip st0, st1
    ja exit_err

    ; cmp rax, -5
    fld qword [num_minus_five]
    fcomip st0, st1
    ja .first_part

    ; cmp rax, -4
    fld qword [num_minus_four]
    fcomip st0, st1
    ja .second_part

    ; cmp rax, 0
    fld qword [num_zero]
    fcomip st0, st1
    ja .third_part

    ; cmp rax, pi
    fld qword [result]
    fld qword [pi]
    fcomip st0, st1
    ja .fourth_part

    ; cmp rax, 5
    fld qword [result]
    fld qword [num_five]
    fcomip st0, st1
    ja .fifth_part

    jmp exit_err
.first_part:
    fadd qword [num_seven]
    fmul st0, st0
    fchs
    fadd qword [num_four]
    fsqrt
    fchs
    fadd qword [num_two]
    fstp qword [result]
    jmp done_calc
.second_part:
    fld qword [num_two]
    fstp qword [result]
    jmp done_calc
.third_part:
    fchs
    fdiv qword [num_two]
    fstp qword [result]
    jmp done_calc
.fourth_part:
    fsin
    fstp qword [result]
    jmp done_calc
.fifth_part:
    fsub qword [pi]
    fstp qword [result]
    jmp done_calc
done_calc:
    mov rdi, [result]
    fld qword [result]
    frndint
    fstp qword [int_part]

    mov rdi, [int_part]

    fld qword [result]
    fsub qword [int_part]
    fmul qword [precision]
    frndint
    fistp qword [frac_part]

    fld qword [result]
    frndint
    fistp qword [int_part]

    mov rdi, [int_part]
    call print_int

    mov rdi, '.'
    call print_char

    mov rdi, [frac_part]
    call print_uint

    mov rdi, 10
    call print_char

    jmp _start
exit_err:
    mov rax, 1
    mov rdi, 1
    mov rsi, err_message
    mov rdx, 14
    syscall

    jmp _start
exit:
    mov rax, 60
    mov rdi, 0
    syscall

string_length:
    mov rcx, 0
    .loop:
        cmp byte[rdi + rcx], 0
        je .ret
        inc rcx
        jmp .loop
    .ret:
        mov rax, rcx
        ret


; Принимает указатель на нуль-терминированную строку, выводит её в stdout
print_string:
    push rdi
    call string_length
    mov rdx, rax
    pop rsi
    mov rax, 1
    mov rdi, 1
    syscall
    ret

print_char:
    push rdi
    mov rsi, rsp
    mov rax, 1
    mov rdi, 1
    mov rdx, 1
    syscall
    pop rdi
    ret

print_uint:
    ; rdi
    sub rsp, 24
    mov rcx, 23
    mov byte[rsp+rcx], 0

    mov rax, rdi
    mov rbx, 10

    .loop:
        mov rdx, 0
        div rbx

        add dl, '0'
        dec rcx
        mov [rsp+rcx], dl

        test rax, rax
        jnz .loop
    
    mov rbx, 0
    lea rdi, [rsp+rcx]
    call print_string
    add rsp, 24
    ret

print_int:
    ; rdi
    test rdi, rdi
    jns print_uint

    push rdi
    mov rdi, '-'
    call print_char
    pop rdi
    neg rdi
    jmp print_uint

