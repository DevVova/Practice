package copyvlab.feeds.tslab

import copyvlab.feeds.binance.getBinanceCandlesticks
import java.io.File

fun saveDateInTxt() {
    val symbol = "SOLUSDT"
    val filePath = "C:\\C\\Kotlin\\MyPrograms\\Desktop\\Practice\\quotes$symbol.txt"

    // Заголовок для записи в файл
    val header = HEADER_TXT + "\n"

    // Данные из функции, которая возвращает список candlesticks
    val newList = getTSLabCandlesticksFromBinance(
        symbol = symbol,
        getBinanceCandlesticks(symbol = symbol, limit = 530000)
    )

    // Открываем BufferedWriter для записи
    File(filePath).bufferedWriter().use { writer ->
        writer.write(header)

        // Записываем каждую строку candlestick в файл
        newList.forEach { candlestick ->
            // Форматируем каждую запись в нужный формат строки
            val formattedLine = "${candlestick.ticker},${candlestick.period},${candlestick.date},${candlestick.time}," +
                    "${candlestick.open},${candlestick.high},${candlestick.low},${candlestick.close},${candlestick.volume}"
            writer.write(formattedLine + "\n")
        }
    }
}