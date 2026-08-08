package com.andrew;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        HeapSort heapSort = new HeapSort();

        int[] arr = {5, 1, 4, 2, 8};

        System.out.println("Before: " + Arrays.toString(arr));
        heapSort.sort(arr);
        System.out.println("After: " + Arrays.toString(arr));
    }
}