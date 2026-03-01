package universecore.util.funcs;

@FunctionalInterface
public interface VariableFunc<T, R>{
  @SuppressWarnings("unchecked")
  R apply(T... args);
}
