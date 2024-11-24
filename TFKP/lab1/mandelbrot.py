import numpy as np
import matplotlib.pyplot as plt
from concurrent.futures import ProcessPoolExecutor

def mandelbrot(c, max_iter):
    z = 0
    for n in range(max_iter):
        if abs(z) > 2:
            return n
        z = z*z + c
    return max_iter

def mandelbrot_set(xmin, xmax, ymin, ymax, width, height, max_iter):
    r1 = np.linspace(xmin, xmax, width)
    r2 = np.linspace(ymin, ymax, height)
    m_set = np.empty((width, height))
    
    # with ProcessPoolExecutor() as executor:
    #     futures = []
    #     for i in range(width):
    #         futures.append(executor.submit(compute_column, i, r1, r2, m_set, max_iter))
        
    #     for future in futures:
    #         future.result()



    for i in range(width):
        for j in range(height):
            c = r1[i] + 1j*r2[j]
            m_set[j, i] = mandelbrot(c, max_iter)
    return m_set

def compute_column(i, r1, r2, m_set, max_iter):
    for j in range(len(r2)):
        c = r1[i] + 1j * r2[j]
        m_set[j, i] = mandelbrot(c, max_iter)

plt.imshow(mandelbrot_set(-2, 1, -1.5, 1.5, 4000, 4000, 100), cmap='inferno')
#plt.imshow(mandelbrot_set(-0.8, -0.7, 0.1, 0.2, 1000, 1000, 100), cmap='inferno')
plt.colorbar()
plt.show()
