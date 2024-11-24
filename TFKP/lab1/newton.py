# import numpy as np
# import matplotlib.pyplot as plt

# def newton_method(z, max_iter=50, tol=1e-6):
#     for _ in range(max_iter):
#         z_prev = z
#         z = z - (z ** 3 - 1) / (3 * z ** 2)
#         if abs(z - z_prev) < tol:
#             break
#     return z

# def newton_fractal(xmin, xmax, ymin, ymax, width, height, max_iter):
#     x = np.linspace(xmin, xmax, width)
#     y = np.linspace(ymin, ymax, height)
#     fractal = np.zeros((height, width, 3))

#     for i in range(width):
#         for j in range(height):
#             z = x[i] + 1j * y[j]
#             z_final = newton_method(z, max_iter)
#             if np.isclose(z_final, 1):
#                 fractal[j, i] = [1, 0, 0]
#             elif np.isclose(z_final, np.exp(2j * np.pi / 3)):
#                 fractal[j, i] = [0, 1, 0]
#             elif np.isclose(z_final, np.exp(4j * np.pi / 3)):
#                 fractal[j, i] = [0, 0, 1]
#     return fractal

# plt.imshow(newton_fractal(-1.5, 1.5, -1.5, 1.5, 800, 800, 100))
# plt.title("Бассейны Ньютона для f(z) = z^3 - 1")
# plt.show()


import numpy as np
import matplotlib.pyplot as plt

def newton_polynomial_3(z):
    return z**3 - 1 

def newton_polynomial_5(z):
    return z**5 - 1

def newton_derivative_3(z):
    return 3*z**2

def newton_derivative_5(z):
    return 5*z**4

def newton_method(z, poly, dpoly, max_iter=100):
    for _ in range(max_iter):
        z_next = z - poly(z) / dpoly(z)
        if abs(z_next - z) < 1e-6:
            break
        z = z_next
    return z

def create_basin(poly, dpoly, xlim, ylim, resolution=1000):
    x = np.linspace(xlim[0], xlim[1], resolution)
    y = np.linspace(ylim[0], ylim[1], resolution)
    Z = np.array([[newton_method(complex(xi, yi), poly, dpoly) for xi in x] for yi in y])
    return Z

xlim = (-2, 2)
ylim = (-2, 2)

basin_3 = create_basin(newton_polynomial_3, newton_derivative_3, xlim, ylim)

basin_5 = create_basin(newton_polynomial_5, newton_derivative_5, xlim, ylim)

# plt.figure(figsize=(12, 6))

# plt.subplot(1, 2, 1)
# plt.imshow(np.angle(basin_3), extent=xlim + ylim, cmap='hsv', origin='lower')
# plt.title('Бассейн Ньютона для полинома третьей степени')
# plt.colorbar()

# plt.subplot(1, 2, 2)
plt.imshow(np.angle(basin_5), extent=xlim + ylim, cmap='hsv', origin='lower')
plt.title('Бассейн Ньютона для f(z) = z^5 - 1')
plt.colorbar()

# plt.tight_layout()
plt.show()