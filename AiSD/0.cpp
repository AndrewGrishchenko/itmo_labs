#define _CRT_SECURE_NO_WARNINGS

#include <iostream>

using namespace std;

int main() {
  freopen("input.txt", "r", stdin);
  freopen("output.txt", "w", stdout);

  int t;
  string s;

  cin >> t;
  for (int i = 0; i < t; i++) {
    cin >> s;
    if (s.size() % 2 != 0) {
      cout << "NO" << endl;
      continue;
    }

    bool flag = true;
    for (int j = 0; j < (int)s.size() / 2; j++) {
      if (s[j] != s[j + s.size() / 2]) {
        cout << "NO" << endl;
        flag = false;
        break;
      }
    }
    if (flag) {
      cout << "YES" << endl;
    }
  }
}