#include "kernel.h"
#include "common.h"
#include "io.h"

extern char __bss[], __bss_end[], __stack_top[];

__attribute__((section(".text.boot")))
__attribute__((naked))
void boot(void)
{
  __asm__ __volatile__(
          "mv sp, %[stack_top]\n"
          "j kernel_main\n"
          :
          : [stack_top] "r" (__stack_top)
  );
}

struct sbiret sbi_call(long arg0, long arg1, long arg2, long arg3, long arg4, long arg5, long fid, long eid) {
    register long a0 __asm__("a0") = arg0;
    register long a1 __asm__("a1") = arg1;
    register long a2 __asm__("a2") = arg2;
    register long a3 __asm__("a3") = arg3;
    register long a4 __asm__("a4") = arg4;
    register long a5 __asm__("a5") = arg5;
    register long a6 __asm__("a6") = fid;
    register long a7 __asm__("a7") = eid;

    __asm__ __volatile__("ecall"
                    : "=r"(a0), "=r"(a1)
                    : "r"(a0), "r"(a1), "r"(a2), "r"(a3), "r"(a4), "r"(a5), "r"(a6), "r"(a7)
                    : "memory");
    return (struct sbiret) {.error = a0, .value = a1};
}

void putchar(char ch) {
    sbi_call(ch, 0, 0, 0, 0, 0, 0, SBI_ECALL_PUTCHAR);
}

int getchar(void) {
    struct sbiret ret;

    do {
        ret = sbi_call(0, 0, 0, 0, 0, 0, 0, SBI_ECALL_GETCHAR);
    } while (ret.error == SBI_ERROR);

    return (int) ret.error;
}

void readline(char* buf, int max_len) {
    int i = 0;
    char c;

    while (i < max_len - 1) {
        c = getchar();

        if (c == 8 || c == 127) {
            if (i > 0) {
                i--;
                buf[i] = '\0';

                putchar('\b');
                putchar(' ');
                putchar('\b');
            }
            continue;
        }

        if (c == '\n' || c == '\r') {
            putchar('\n');
            break;
        }

        if (c >= 32 && c < 127) {
            putchar(c);
            buf[i++] = c;
        }
    }

    buf[i] = '\0';
}

void get_sbi_impl_version() {
    struct sbiret ret = sbi_call(0, 0, 0, 0, 0, 0, SBI_EXT_IMPL, SBI_EXT_BASE);
    long version = ret.value;

    unsigned long major = version >> 16;
    unsigned long minor = version & 0xFFFF;

    printf("OpenSBI impl version: %d.%d\n", major, minor);
}

void hart_get_status() {
    printf("enter hart no: ");
    char input[32];
    readline(input, sizeof(input));
    long hart_num = atoi(input);

    struct sbiret ret = sbi_call(hart_num, 0, 0, 0, 0, 0, SBI_EXT_HRT_STATUS, SBI_EXT_HSM);

    printf("\nfor hart #%d, error: %d, value: %d, ", hart_num, ret.error, ret.value);

    switch (ret.value) {
        case 0:
            printf("STARTED\n");
            break;
        
        case 1:
            printf("STOPPED\n");
            break;

        case 2:
            printf("START_PENDING\n");
            break;

        case 3:
            printf("STOP_PENDING\n");
            break;

        case 4:
            printf("SUSPENDED\n");
            break;

        case 5:
            printf("SUSPEND_PENDING\n");
            break;

        case 6:
            printf("RESUME_PENDING\n");
            break;

        default:
            printf("UNKNOWN\n");
    }
}

void hart_stop() {
    printf("\nhart_stop\n");

    sbi_call(0, 0, 0, 0, 0, 0, SBI_EXT_HRT_STOP, SBI_EXT_HSM);
}

void system_shutdown() {
    sbi_call(0, 0, 0, 0, 0, 0, SBI_EXT_SHUTDOWN, SBI_EXT_SRST);
    while(1);
}

void menu() {
    while (1) {
        printf("\nMenu:\n");
        printf("%2s* 1. Get SBI implementation version\n", "");
        printf("%2s* 2. Hart get status\n", "");
        printf("%2s* 3. Hart stop\n", "");
        printf("%2s* 4. System shutdown\n", "");
        printf("Choose an option: ");

        char input[32];
        readline(input, sizeof(input));
        int choice = atoi(input);
        switch (choice) {
            case 1:
                get_sbi_impl_version();
                break;
            
            case 2:
                hart_get_status();
                break;

            case 3:
                hart_stop();
                break;

            case 4:
                system_shutdown();
                break;

            default:
                printf("\nThere is no such an option\n");
        }

        printf("\n");
    }
}

void kernel_main(void) {
    for (char* p = __bss; p < __bss_end; p++) {
        *p = 0;
    }

    menu();

    for (;;) {
        __asm__ __volatile__("wfi");
    }
}