import time
import keyboard

def run_timer():
    errors = []
    num_attempts = 50
    target_time = 5.0

    print("Нажмите пробел ровно на 5-й секунде!")
    
    for attempt in range(num_attempts):
        print(f"\nПопытка {attempt + 1}/{num_attempts}")
        start_time = time.time()
        while True:
            elapsed_time = time.time() - start_time
            print(f"\rПрошло {elapsed_time:.2f} секунд", end="")
            
            if keyboard.is_pressed('space'):
                error = elapsed_time - target_time
                errors.append(error)
                print(f"\nВы нажали пробел с погрешностью {error:.2f} секунд")
                time.sleep(0.5)  # Задержка для предотвращения многократных срабатываний
                break
        
        time.sleep(1)  # Секундная пауза перед новым запуском таймера
    
    print("\nВсе попытки завершены!")
    print(f"Средняя погрешность: {sum(errors) / len(errors):.2f} секунд")
    return errors

# Запуск программы
if __name__ == "__main__":
    run_timer()
