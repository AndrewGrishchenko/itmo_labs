import numpy as np
import matplotlib.pyplot as plt

def newton_method(z, max_iter=50, tol=1e-6):
    for _ in range(max_iter):
        z_prev = z
        z = z - (z ** 3 - 1) / (3 * z ** 2)
        if abs(z - z_prev) < tol:
            break
    return z

def newton_fractal(xmin, xmax, ymin, ymax, width, height, max_iter):
    x = np.linspace(xmin, xmax, width)
    y = np.linspace(ymin, ymax, height)
    fractal = np.zeros((height, width, 3))

    for i in range(width):
        for j in range(height):
            z = x[i] + 1j * y[j]
            z_final = newton_method(z, max_iter)
            if np.isclose(z_final, 1):
                fractal[j, i] = [1, 0, 0]
            elif np.isclose(z_final, np.exp(2j * np.pi / 3)):
                fractal[j, i] = [0, 1, 0]
            elif np.isclose(z_final, np.exp(4j * np.pi / 3)):
                fractal[j, i] = [0, 0, 1]
    return fractal

plt.imshow(newton_fractal(-1.5, 1.5, -1.5, 1.5, 800, 800, 100))
plt.title("Бассейны Ньютона для f(z) = z^3 - 1")
plt.show()