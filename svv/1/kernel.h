#pragma once

struct sbiret {
    long error;
    long value;
};

#define SBI_SUCCESS 0
#define SBI_ERROR -1

#define SBI_ECALL_PUTCHAR 0x01
#define SBI_ECALL_GETCHAR 0x02

#define SBI_EXT_BASE 0x10
#define SBI_EXT_IMPL 0x02

#define SBI_EXT_HSM 0x48534D
#define SBI_EXT_HRT_STATUS 0x02
#define SBI_EXT_HRT_STOP 0x01

#define SBI_EXT_SRST 0x53525354
#define SBI_EXT_SHUTDOWN 0x00