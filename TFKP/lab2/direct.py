import numpy as np
import matplotlib.pyplot as plt

def plot_region_separate_window(z_points, title, xlim=[-5, 5], ylim=[-5, 5]):
    plt.figure()
    x = z_points.real
    y = z_points.imag
    plt.plot(x, y, '.', markersize=2)
    plt.title(title)
    plt.xlabel("Re")
    plt.ylabel("Im")
    plt.gca().set_aspect('equal', adjustable='box')
    plt.grid(True)
    plt.axhline(y=0, color='k', linestyle='--')
    plt.axvline(x=0, color='k', linestyle='--')

    plt.xlim(xlim)
    plt.ylim(ylim)

    plt.show(block=False)

def generate_initial_region_points(x_range=(-5, 5), y_range=(-5, 5), num_points=5000):
    x = np.random.uniform(x_range[0], x_range[1], num_points)
    y = np.random.uniform(y_range[0], y_range[1], num_points)
    z_points = x + 1j * y
    
    valid_indices = np.where(z_points.real + z_points.imag + 1 < 0)
    return z_points[valid_indices]

def g1(z):
    return z * np.exp(-3j * np.pi / 4)

def g2(z):
    return z - 1j / np.sqrt(2)

def g3(z):
    return np.arccosh(z)

def g4(z):
    return 2 * (1 + 1j * z / np.pi)

def g5(z):
    return 2 / (1 - z)

z_initial_points = generate_initial_region_points(num_points=10000000)
z_current_points = z_initial_points.copy()

plot_region_separate_window(z_current_points, "Изначальная область", [-6, 6], [-6, 6])

z_current_points = g1(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 1: g1(z) = z * e^(-3i * pi/4)", [-7, 7], [-1, 8])

z_current_points = g2(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 2: g2(z) = z - i/sqrt(2)", [-7, 7], [-1, 7])

z_current_points = g3(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 3: g3(z) = arccosh(z)", [-1, 3], [-1, 4])

z_current_points = g4(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 4: g4(z) = 2 * (1 + zi / pi)", [-1, 3], [-0.5, 2])

z_current_points = g5(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 5: g5(z) = 2 / (1 - z)", [-2.5, 2.5], [-0.5, 1.5])

plt.show()