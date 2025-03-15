#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <memory>
#include <unordered_map>
#include <vector>

using namespace std;

class Closure {
public:
  Closure() : declaredIn(make_shared<unordered_map<string, Closure*>>()) {
    variables = make_shared<unordered_map<string, int>>();
  }

  Closure(Closure* parent) : parent(parent), declaredIn(parent->declaredIn) {
    variables = parent->getVariablesPtr();
  }

  ~Closure() {
    if (parent)
      rollback(parent);
    localVariables.clear();
  }

  void parse(string s) {
    string var1 = s.substr(0, s.find("="));
    string var2 = s.substr(s.find("=") + 1);

    if (isNumber(var2)) {
      localVariables[var1] = stoi(var2);
    } else {
      localVariables[var1] = getVariable(var2);
      cout << localVariables[var1] << endl;
    }

    if (declaredIn->find(var1) == declaredIn->end()) {
      (*declaredIn)[var1] = this;
    }

    (*variables)[var1] = localVariables[var1];
  }

  unordered_map<string, int> getLocalVariables() {
    return localVariables;
  }

  void rollback(Closure* rollTo) {
    unordered_map<string, int> rollToLocalVariables = rollTo->getLocalVariables();
    for (const auto& [key, value] : localVariables) {
      if ((*declaredIn)[key] == this) {
        variables->erase(key);
        declaredIn->erase(key);
      }
      else
        (*variables)[key] = rollToLocalVariables[key];
    }
  }

  Closure* createChild() {
    Closure* newClosure = new Closure(this);
    children.push_back(newClosure);
    return newClosure;
  }

  Closure* getParent() {
    return parent;
  }

  int getVariable(string name) {
    if (localVariables.find(name) != localVariables.end())
      return localVariables[name];
    if (variables->find(name) != variables->end())
      return (*variables)[name];
    return 0;
  }

  shared_ptr<unordered_map<string, int>> getVariablesPtr() {
    return variables;
  }

private:
  Closure* parent = nullptr;
  shared_ptr<unordered_map<string, int>> variables;
  unordered_map<string, int> localVariables;
  shared_ptr<unordered_map<string, Closure*>> declaredIn;
  vector<Closure*> children;

  bool isNumber(const string& s) {
    if (s.empty())
      return false;
    size_t start = (s[0] == '-') ? 1 : 0;
    for (size_t i = start; i < s.size(); i++) {
      if (!isdigit(s[i]))
        return false;
    }
    return true;
  }
};

int main() {
  freopen("input.txt", "r", stdin);
  freopen("output.txt", "w", stdout);

  Closure* root = new Closure();
  Closure* current = root;

  string s;
  while (cin >> s) {
    if (s == "{") {
      current = current->createChild();
    } else if (s == "}") {
      Closure* oldScope = current;
      current = current->getParent();
      delete oldScope;
    } else {
      current->parse(s);
    }
  }

  delete root;
}
