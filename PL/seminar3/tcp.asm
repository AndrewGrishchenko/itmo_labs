section .data
    socket_error_str: db 'socket error', 10
    bind_error_str: db 'bind error', 10
    recv_error_str: db 'recv error', 10

    buffer db 1024 dup(0)

    sockaddr_in:
        .sin_family dw 2
        .sin_port dw 0x3930
        .sin_addr dd 0
        .sin_zero db 8 dup(0)

section .bss
    client_fd resq 1

section .text
    global _start

_start:
    ; rax rdi rsi rdx

    ; socket (domain, type, protocol)
    mov rax, 41
    mov rdi, 2
    mov rsi, 1
    mov rdx, 0
    syscall
    
    test rax, rax
    js _socket_error

    mov rdi, rax

    ; bind
    lea rsi, [sockaddr_in]
    mov edx, 16
    mov rax, 49
    syscall

    test rax, rax
    js _bind_error

    ; listen
    mov rax, 42
    mov rsi, 5
    syscall ; <- вот тут rax становится -14

    ; accept (sockfd, addr, addrlen)
    mov rax, 43
    mov rsi, 0
    mov rdx, 0
    syscall
    mov [client_fd], rax

recv:
    ; recv  (sockfd, buf, len, flags)
    mov rax, 45
    mov rdi, [client_fd]
    mov rsi, buffer
    mov rdx, 1024
    mov r10, 0
    syscall

    test rax, rax
    js _recv_error

    ; print buffer
    mov rax, 1
    mov rdi, 1
    mov rsi, buffer
    mov rdx, rax
    syscall

    jmp recv

_socket_error:
    mov rax, 1
    mov rdi, 1
    mov rsi, socket_error_str
    mov rdx, 13
    syscall

    jmp _exit_err

_bind_error:
    mov rax, 1
    mov rdi, 1
    mov rsi, bind_error_str
    mov rdx, 11
    syscall

    jmp _exit_err

_recv_error:
    mov rax, 1
    mov rdi, 1
    mov rsi, recv_error_str
    mov rdx, 11
    syscall

    jmp _exit_err

_exit_err:
    mov rax, 60
    mov rdi, rax
    syscall

