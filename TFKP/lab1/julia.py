import numpy as np
from matplotlib import pyplot as plt

def julia(c, z, max_iter):
    for n in range(max_iter):
        if abs(z) > 2:
            return n
        z = z*z + c
    return max_iter

def julia_set(xmin, xmax, ymin, ymax, width, height, c, max_iter):
    r1 = np.linspace(xmin, xmax, width)
    r2 = np.linspace(ymin, ymax, height)
    j_set = np.empty((width, height))
    for i in range(width):
        for j in range(height):
            z = r1[i] + 1j*r2[j]
            j_set[i, j] = julia(c, z, max_iter)
    return j_set

plt.imshow(julia_set(-1.5, 1.5, -1.5, 1.5, 1500, 1500, -0.5251993 + 0.5251993j, 150), cmap='inferno')
#plt.imshow(julia_set(-1.5, 1.5, -1.5, 1.5, 1500, 1500, 0.28 + 0.0113j, 400), cmap='inferno')
plt.colorbar()
plt.show()
