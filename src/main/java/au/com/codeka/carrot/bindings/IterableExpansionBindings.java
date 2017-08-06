package au.com.codeka.carrot.bindings;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.expr.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * {@link Bindings} which expand an {@link Iterable} into multiple variables (given in an {@link Iterable} as well).
 * <p>
 * Each variable is assigned to the value at the same position in the values {@link Iterable}.
 * <p>
 * Note, this doesn't enforce any constraints. In particular the values {@link Iterable} can contain more values than the
 * variables {@link Iterable} without any error. In this case the remaining values will remain unbound.
 * If the variables contain more elements than the values, no error is thrown unless an "unbound" variable is accessed.
 * <p>
 * This behavior is different from Python where the variables must be equal to the number of values in the expanded iterable.
 *
 * @author Marten Gajda
 */
public final class IterableExpansionBindings implements Bindings {
  private final Iterable<Identifier> identifiers;
  private final Iterable<Object> values;

  public IterableExpansionBindings(Iterable<Identifier> identifiers, Iterable<Object> values) {
    this.identifiers = identifiers;
    this.values = values;
  }

  @Nullable
  @Override
  public Object resolve(@Nonnull String key) {
    Iterator<Object> values = this.values.iterator();
    for (Identifier identifier : identifiers) {
      Object nextValue = values.next();
      if (key.equals(identifier.evaluate())) {
        return nextValue;
      }
    }
    return null;
  }

  @Override
  public boolean isEmpty() {
    return !values.iterator().hasNext();
  }
}
