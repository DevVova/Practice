package copyvlab.feeds.binance

import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.Instant
import kotlin.math.min

/**
 * Данная функция возвращает список данных в виде свечей, полученных от брокера binance.
 */
fun getBinanceCandlesticks(
    symbol: String = "BTCUSDT",//Это интересуемая монета.
    limit: Int = 1000,//Это количество свечей.
    interval: String = "1m"//Это интервал свечки.
): List<BinanceCandlestick> {
    val outputBinanceCandlestick = mutableListOf<BinanceCandlestick>()//Это список для вывода окончательный.

    val client = OkHttpClient()//Для работы с http создаём объект client.

    var remaining = limit//Оставшееся количество свечей.
    var lastOpenTime: Long? = null//Это время открытия последней свечи.
    var requestLimit: Int

    while (remaining > 0) {
        requestLimit = min(1000, remaining)  // Запрашиваем максимум 1000 записей за один раз.
        //Ниже это построитель строк, который нужен для запроса.
        val url = "https://api.binance.com/api/v3/klines?symbol=$symbol&interval=$interval&limit=$requestLimit"
        var request = Request.Builder().url(url).build()

        // Если есть lastOpenTime, добавляем параметр endTime, чтобы запрос начинался с последнего времени открытия.
        if (lastOpenTime != null) {
            val addUrl = "&endTime=${lastOpenTime - 60000}"
            val newUrl = url + addUrl
            request = Request.Builder().url(newUrl).build()
        }

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                // Обработка успешного ответа.
                val json = Json { ignoreUnknownKeys = true } // Игнорируем неизвестные ключи в ответе

                // Добавлена обработка ошибок при десериализации.
                try {
                    val rawCandlesticks = json.decodeFromString<JsonElement>(response.body?.string() ?: "").jsonArray
                    val candlesticks = rawCandlesticks.map {
                        val data = it.jsonArray
                        BinanceCandlestick(
                            openTime = data[0].jsonPrimitive.long,
                            open = data[1].jsonPrimitive.content,
                            high = data[2].jsonPrimitive.content,
                            low = data[3].jsonPrimitive.content,
                            close = data[4].jsonPrimitive.content,
                            volume = data[5].jsonPrimitive.content,
                            closeTime = data[6].jsonPrimitive.long,
                            quoteAssetVolume = data[7].jsonPrimitive.content,
                            numberOfTrades = data[8].jsonPrimitive.int,
                            takerBuyBaseAssetVolume = data[9].jsonPrimitive.content,
                            takerBuyQuoteAssetVolume = data[10].jsonPrimitive.content,
                            ignore = data[11].jsonPrimitive.content
                        )
                    }
                    outputBinanceCandlestick.addAll(0, candlesticks)
                } catch (e: Exception) {
                    // Обработка ошибки при десериализации JSON
                    println("Ошибка при обработке JSON: ${e.message}")
                }
            }
        } catch (e: IOException) {
            // Обработка ошибки запроса
            println("Ошибка выполнения запроса: ${e.message}")
        }

        // Добавьте паузу, чтобы не превысить лимит запросов Binance API (примерно 10 запросов в секунду)
        Thread.sleep(100)  // Пауза в 100 миллисекунд

        lastOpenTime = outputBinanceCandlestick.firstOrNull()?.openTime
        remaining -= requestLimit  // Уменьшаем оставшееся количество
    }

    return outputBinanceCandlestick
}

fun main() {
    val newList = getBinanceCandlesticks(limit = 4000)
    newList.forEach { println(it) }
    println(newList.size)
    val diff = newList[newList.size - 1].openTime - newList[0].openTime
    println(diff)
    println()

    println(newList.lastOrNull()?.let { Instant.ofEpochMilli(it.openTime) })
}