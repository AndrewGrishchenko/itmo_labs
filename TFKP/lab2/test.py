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
    
    valid_indices = np.where((np.abs(z_points) < 6 - z_points) & (np.abs(z_points) <= 4))
    return z_points[valid_indices]

z_initial_points = generate_initial_region_points(num_points=10000000)
z_current_points = z_initial_points.copy()

plot_region_separate_window(z_current_points, "Изначальная область", [-10, 10], [-10, 10])

plt.show()