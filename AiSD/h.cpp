#define _CRT_SECURE_NO_WARNINGS

#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

int main() {
  freopen("shop.in", "r", stdin);
  freopen("shop.out", "w", stdout);

  int n, k;
  cin >> n >> k;

  vector<int> a(n);
  for (int i = 0; i < n; i++) {
    cin >> a[i];
  }

  sort(a.begin(), a.end(), greater<int>());

  int sum = 0;
  for (int i = 0; i < n; i += k) {
    for (int j = i; j < i + k - 1; j++) {
      if (j >= n)
        break;
      sum += a[j];
    }
  }

  cout << sum;
}