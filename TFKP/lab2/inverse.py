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
    
    valid_indices = np.where((z_points.imag > 0) & (np.abs(z_points + 1) >= 1) & (np.abs(z_points - 1) >= 1))
    return z_points[valid_indices]

def g1(z):
    return (z - 2) / z

def g2(z):
    return (np.pi / 1j) * (z / 2 - 1)

def g3(z):
    return np.cosh(z)

def g4(z):
    return z + 1j / np.sqrt(2)

def g5(z):
    return z * np.exp(3j * np.pi / 4)

z_initial_points = generate_initial_region_points(num_points=10000000)
z_current_points = z_initial_points.copy()

plot_region_separate_window(z_current_points, "Изначальная область", [-2.5, 2.5], [-0.5, 1.5])

z_current_points = g1(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 1: g1(z) = (z - 2) / z", [-0.5, 2.5], [-0.5, 3])

z_current_points = g2(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 2: g2(z) = pi/i * (z/2 - 1)", [-1, 5], [-1, 4])

z_current_points = g3(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 3: g3(z) = cosh(z)", [-7, 7], [-1, 7])

z_current_points = g4(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 4: g4(z) = z + i/sqrt(2)", [-7, 7], [-1, 8])

z_current_points = g5(z_current_points)
plot_region_separate_window(z_current_points, "Шаг 5: g5(z) = z * e^(3i * pi/4)")

plt.show()