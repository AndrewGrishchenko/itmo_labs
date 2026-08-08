package com.andrew;

import java.util.ArrayList;
import java.util.List;

public class HeapSort {
    public List<String> trace = new ArrayList<>();

    public void sort(int[] arr) {
        trace.clear();

        buildHeap(arr);

        int n = arr.length;

        for (int i = n - 1; i > 0; i--) {
            trace.add("EXTRACT_MAX");

            swap(arr, 0, i);
            heapify(arr, i, 0);
        }

        trace.add("SORT_COMPLETE");
    }

    public void buildHeap(int[] arr) {
        trace.add("BUILD_HEAP");

        int n = arr.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }
    }

    private void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && arr[left] > arr[largest]) {
            trace.add("HEAPIFY_COMPARE_LEFT");
            largest = left;
            trace.add("NEW_LARGEST_LEFT");
        }

        if (right < n && arr[right] > arr[largest]) {
            trace.add("HEAPIFY_COMPARE_RIGHT");
            largest = right;
            trace.add("NEW_LARGEST_RIGHT");
        }

        if (largest != i) {
            trace.add("SWAP_BEFORE");
            swap(arr, i, largest);
            trace.add("SWAP_AFTER");

            trace.add("HEAPIFY_RECURSION");
            heapify(arr, n, largest);
        } else {
            trace.add("NO_SWAP");
        }
    }

    private void swap(int[] arr, int i, int j) {
        trace.add("SWAP");
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}