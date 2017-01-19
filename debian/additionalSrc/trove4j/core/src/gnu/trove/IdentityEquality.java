package gnu.trove;

class IdentityEquality<T> implements Equality<T> {
  public boolean equals(T o1, T o2) {
    return o1 == o2;
  }
}
