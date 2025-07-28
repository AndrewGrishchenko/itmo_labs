#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <vector>

using namespace std;

const int MAXN = 1005;

long long c[MAXN][MAXN];
vector<int> g[MAXN];
vector<bool> used;

void dfs_forward(int v) {
  used[v] = true;
  for (auto u : g[v]) {
    if (!used[u]) {
      dfs_forward(u);
    }
  }
}

void dfs_backward(int v, const vector<vector<int>>& gt) {
  used[v] = true;
  for (auto u : gt[v]) {
    if (!used[u]) {
      dfs_backward(u, gt);
    }
  }
}

bool check(long long W, int n) {
  for (int i = 0; i < n; i++) {
    g[i].clear();
    for (int j = 0; j < n; j++) {
      if (c[i][j] <= W && i != j) {
        g[i].push_back(j);
      }
    }
  }

  fill(used.begin(), used.end(), false);
  dfs_forward(0);
  for (int i = 0; i < n; i++) {
    if (!used[i]) {
      return false;
    }
  }

  vector<vector<int>> gt(n);
  for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
      if (c[i][j] <= W && i != j) {
        gt[j].push_back(i);
      }
    }
  }

  fill(used.begin(), used.end(), false);
  dfs_backward(0, gt);
  for (int i = 0; i < n; i++) {
    if (!used[i]) {
      return false;
    }
  }

  return true;
}

int main() {
  freopen("avia.in", "r", stdin);
  freopen("avia.out", "w", stdout);

  int n;
  cin >> n;
  for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
      cin >> c[i][j];
    }
  }

  used.resize(n);

  long long left = 0, right = 1e9 + 5, ans = 1e9 + 5;
  while (left <= right) {
    long long mid = left + (right - left) / 2;
    if (check(mid, n)) {
      ans = mid;
      right = mid - 1;
    } else {
      left = mid + 1;
    }
  }

  cout << ans;
}