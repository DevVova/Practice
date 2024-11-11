package copyvlab

import copyvlab.feeds.binance.getBinanceCandlesticks
import copyvlab.feeds.vlab.getVKlinesFromBinance
import java.time.Instant

fun main() {
    val newList = getVKlinesFromBinance(getBinanceCandlesticks(limit = 6000))
    newList.forEach { println(it) }
    println(newList.size)
    println(newList[0].open)
    val diff = newList[0].openTime - newList[newList.size - 1].openTime
    println(diff)
    println()

    println(newList.firstOrNull()?.let { Instant.ofEpochMilli(it.openTime) })
}