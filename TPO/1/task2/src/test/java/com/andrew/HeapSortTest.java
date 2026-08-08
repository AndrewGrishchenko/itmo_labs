package com.andrew;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class HeapSortTest {
    @Test
    public void testBasicSort() {
        HeapSort heapSort = new HeapSort();

        int[] input = {5, 1, 4, 2, 8};
        int[] expected = {1, 2, 4, 5, 8};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testEmpty() {
        HeapSort heapSort = new HeapSort();

        int[] input = {};
        heapSort.sort(input);

        assertArrayEquals(new int[]{}, input);
    }

    @Test
    public void testSingle() {
        HeapSort heapSort = new HeapSort();

        int[] input = {42};
        heapSort.sort(input);

        assertArrayEquals(new int[]{42}, input);
    }

    @Test
    public void testTwoElements() {
        HeapSort heapSort = new HeapSort();

        int[] input = {2, 1};
        heapSort.sort(input);

        assertArrayEquals(new int[]{1, 2}, input);
    }

    @Test
    public void testSorted() {
        HeapSort heapSort = new HeapSort();

        int[] input = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testReversed() {
        HeapSort heapSort = new HeapSort();

        int[] input = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testDuplicates() {
        HeapSort heapSort = new HeapSort();

        int[] input = {3, 1, 3, 2, 2};
        int[] expected = {1, 2, 2, 3, 3};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testAlmostHeap() {
        HeapSort heapSort = new HeapSort();

        int[] input = {9, 5, 8, 1, 2, 3};
        int[] expected = {1, 2, 3, 5, 8, 9};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testNearlySorted() {
        HeapSort heapSort = new HeapSort();

        int[] input = {1, 2, 3, 5, 4, 6};
        int[] expected = {1, 2, 3, 4, 5, 6};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testNegativeNumbers() {
        HeapSort heapSort = new HeapSort();

        int[] input = {-1, -3, -2, 5, 0};
        int[] expected = {-3, -2, -1, 0, 5};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testAllSame() {
        HeapSort heapSort = new HeapSort();

        int[] input = {2, 2, 2, 2, 2};
        int[] expected = {2, 2, 2, 2, 2};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testLarge() {
        HeapSort heapSort = new HeapSort();

        int[] input = {10, 3, 7, 1, 9, 2, 8, 6, 5, 4};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testAlreadyHeap() {
        HeapSort heapSort = new HeapSort();

        int[] input = {10, 5, 9, 1, 3, 8, 7};
        int[] expected = {1, 3, 5, 7, 8, 9, 10};

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testRandom() {
        HeapSort heapSort = new HeapSort();

        int[] input = new int[20];
        
        Random random = new Random(42);

        for (int i = 0; i < input.length; i++) {
            input[i] = random.nextInt(100);
        }

        int[] expected = input.clone();
        Arrays.sort(expected);

        heapSort.sort(input);

        assertArrayEquals(expected, input);
    }

    @Test
    public void testBuildHeapProperly() {
        HeapSort heapSort = new HeapSort();

        int[] input = {5, 1, 4, 2, 8};

        heapSort.buildHeap(input);

        assertTrue(isMaxHeap(input));
    }

    @Test
    public void testTrace() {
        HeapSort heapSort = new HeapSort();

        int[] input = {5, 1, 4};

        heapSort.sort(input);

        System.out.println(heapSort.trace);
    }

    private boolean isMaxHeap(int[] arr) {
        int n = arr.length;

        for (int i = 0; i < n / 2; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < n && arr[i] < arr[left]) return false;
            if (right < n && arr[i] < arr[right]) return false;
        }

        return true;
    }
}