package learnokhttp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

fun main() {
    val newList = getVKlines("SOLUSDT")
    println(newList)
    println(newList[0].open)
}

/**
 * symbol указываем заглавными буквами типа "SOLUSDT".
 * interval указываем для 1 минуты "1m".
 */
fun getVKlines(
    symbol: String = "BTCUSDT",
    interval: String = "1m",
    limit: Int = 1000
): List<VKlines> {
    val client = OkHttpClient()

    val outputVKlines = mutableListOf<VKlines>()

    val url = "https://api.binance.com/api/v3/klines?symbol=$symbol&interval=$interval&limit=$limit"
    val request = Request.Builder().url(url).build()
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
                // Преобразование каждого BinanceCandlestick в VKlines
                candlesticks.forEach { candlestick ->
                    val vkline = VKlines(
                        openTime = candlestick.openTime,
                        open = candlestick.open,
                        high = candlestick.high,
                        low = candlestick.low,
                        close = candlestick.close,
                        volume = candlestick.volume,
                        closeTime = candlestick.closeTime
                    )
                    outputVKlines.add(vkline)
                }
            } catch (e: Exception) {
                // Обработка ошибки при десериализации JSON
                println("Ошибка при обработке JSON: ${e.message}")
            }
        }
    } catch (e: IOException) {
        // Обработка ошибки запроса
        println("Ошибка выполнения запроса: ${e.message}")
    }

    return outputVKlines.reversed()
}

data class VKlines(
    val openTime: Long,//Время открытия свечи в миллисекундах с начала эпохи Unix (01.01.1970).
    val open: String,//Цена открытия свечи. Обычно представляется как строка, чтобы избежать потери точности при работе с десятичными числами.
    val high: String,//Наивысшая цена свечи за выбранный интервал времени.
    val low: String,//Наименьшая цена свечи за выбранный интервал времени.
    val close: String,//Цена закрытия свечи. Это цена, на которой завершился торговый период.
    val volume: String,//Количество базового актива, которое было куплено и продано. Например, для пары SOL/USDT объем будет представлен в SOL (базовый актив).
    val closeTime: Long,//Время закрытия свечи в миллисекундах с начала эпохи Unix.
)

@Serializable
data class BinanceCandlestick(
    val openTime: Long,//Время открытия свечи в миллисекундах с начала эпохи Unix (01.01.1970).
    val open: String,//Цена открытия свечи. Обычно представляется как строка, чтобы избежать потери точности при работе с десятичными числами.
    val high: String,//Наивысшая цена свечи за выбранный интервал времени.
    val low: String,//Наименьшая цена свечи за выбранный интервал времени.
    val close: String,//Цена закрытия свечи. Это цена, на которой завершился торговый период.
    val volume: String,//Количество базового актива, которое было куплено и продано. Например, для пары SOL/USDT объем будет представлен в SOL (базовый актив).
    val closeTime: Long,//Время закрытия свечи в миллисекундах с начала эпохи Unix.
    val quoteAssetVolume: String,/*
                                   Весь объём котируемых активов. Что такое котируемый актив?
                                   Котируемый актив — это актив, в котором измеряется
                                   стоимость базового актива. В паре SOL/USDT:
                                   Базовый актив: SOL (криптовалюта, которую вы хотите купить или продать).
                                   Котируемый актив: USDT (стабильная монета, которая используется для
                                   оценки стоимости SOL).
                                   */
    val numberOfTrades: Int,//Общее количество сделок, совершенных за период свечи.
    val takerBuyBaseAssetVolume: String,//Объём купленного базового актива.
    val takerBuyQuoteAssetVolume: String,//Объём купленного котируемого актива.
    val ignore: String/*
                        Параметр, который обычно игнорируется при анализе данных.
                        В контексте Binance API он может использоваться для передачи
                        дополнительных данных, которые не являются важными для пользователей.
                       */
)