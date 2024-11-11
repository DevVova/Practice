package copyvlab.feeds.vlab

import copyvlab.feeds.binance.BinanceCandlestick

/**
 * Данная функция возвращает список данных в виде свечей, для окончательного
 * использования в самом приложении. На вход оно принимает список данных от
 * брокера binance и преобразует их, при этом что очень важно элемент с
 * индексом 0 всегда есть последняя свеча.
 */
fun getVKlinesFromBinance(inputListQuotes: List<BinanceCandlestick>): List<VKlines> {
    val outputVKlines = mutableListOf<VKlines>()//Это список для вывода окончательный.

    // Конвертируем BinanceCandlestick в VKlines и добавляем в общий список
    inputListQuotes.forEach { inputKline ->
        val vkline = VKlines(
            openTime = inputKline.openTime,
            open = inputKline.open,
            high = inputKline.high,
            low = inputKline.low,
            close = inputKline.close,
            volume = inputKline.volume,
            closeTime = inputKline.closeTime
        )
        outputVKlines.add(vkline)
    }

    return outputVKlines.reversed()
}