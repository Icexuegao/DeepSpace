package ice.library.util

import arc.util.Log

fun <E> log(e: () -> E) = Log.info(e())
fun <E> E.log() = Log.info(this)