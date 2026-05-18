package universecore.util.funcs;

@FunctionalInterface
public interface VariableCons<T>{
  @SuppressWarnings("unchecked")
  void apply(T... args);
}
