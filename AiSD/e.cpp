#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <vector>

using namespace std;

int main() {
  freopen("input.txt", "r", stdin);
  freopen("output.txt", "w", stdout);

  int n, k;
  cin >> n >> k;

  vector<int> a(n);
  for (int i = 0; i < n; i++) {
    cin >> a[i];
  }

  int maxx = 0;
  int l = 1, r = a[n - 1] - a[0], m = (l + r) / 2;
  while (r - l >= 0) {
    m = (l + r) / 2;

    bool flag = false;
    int cnt = 1, pos = a[0];
    for (int i = 1; i < n; i++) {
      if (a[i] - pos >= m) {
        cnt++;
        pos = a[i];
        if (cnt == k) {
          flag = true;
          break;
        }
      }
    }

    if (flag) {
      maxx = m;
      l = m + 1;
    } else {
      r = m - 1;
    }
  }

  cout << maxx;
}