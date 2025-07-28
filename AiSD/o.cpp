#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <queue>
#include <vector>

using namespace std;

const int MAXN = 105;
vector<int> g[MAXN];
vector<int> color(MAXN, -1);

bool bfs(int start) {
  queue<int> q;
  q.push(start);
  color[start] = 0;

  while (!q.empty()) {
    int v = q.front();
    q.pop();

    for (auto u : g[v]) {
      if (color[u] == -1) {
        color[u] = 1 - color[v];
        q.push(u);
      } else if (color[u] == color[v]) {
        return false;
      }
    }
  }
  return true;
}

int main() {
  freopen("input.txt", "r", stdin);
  freopen("output.txt", "w", stdout);

  int n, m;
  cin >> n >> m;
  for (int i = 0; i < m; i++) {
    int u, v;
    cin >> u >> v;
    u--;
    v--;
    g[u].push_back(v);
    g[v].push_back(u);
  }

  bool possible = true;
  for (int i = 0; i < n; i++) {
    if (color[i] == -1) {
      if (!bfs(i)) {
        possible = false;
        break;
      }
    }
  }

  cout << (possible ? "YES" : "NO");
}