#define _CRT_SECURE_NO_WARNINGS

#include <algorithm>
#include <iostream>
#include <unordered_map>

using namespace std;

int main() {
  freopen("aurora.in", "r", stdin);
  freopen("aurora.out", "w", stdout);

  string s;
  unsigned long long c[26];

  cin >> s;
  for (int i = 0; i < 26; i++) {
    cin >> c[i];
  }

  string start, mid, end;
  unordered_map<char, int> freq;
  for (char ch : s) {
    freq[ch]++;
  }

  for (auto& t : freq) {
    if (t.second > 1) {
      t.second -= 2;
      start = t.first + start;
    }
  }

  sort(start.begin(), start.end(), [&c](const char& a, const char& b) {
    return c[a - 'a'] > c[b - 'a'];
  });
  end = start;
  reverse(end.begin(), end.end());

  for (auto t : freq) {
    mid += string(t.second, t.first);
  }

  cout << start + mid + end;
}
