package universecore.ui.reactive

import arc.func.Cons
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import universecore.ui.widgets.tables.ITable

fun Table.react(rs1: ReactiveState<*>, cons: Cons<ITable>): Cell<ITable> {
  return react(arrayOf(rs1), cons)
}

fun Table.react(rs1: ReactiveState<*>, rs2: ReactiveState<*>, cons: Cons<ITable>): Cell<ITable> {
  return react(arrayOf(rs1, rs2), cons)
}

fun Table.react(rs1: ReactiveState<*>, rs2: ReactiveState<*>, rs3: ReactiveState<*>, cons: Cons<ITable>): Cell<ITable> {
  return react(arrayOf(rs1, rs2, rs3), cons)
}

fun Table.react(reactiveState: Array<ReactiveState<*>>, cons: Cons<ITable>): Cell<ITable> {
  val table = ITable()

  for(state in reactiveState) {
    //理论上应该不需要取消订阅
    state.subscribe {
      cons.get(table)
    }
  }

  return add(table)
}