package learnokhttp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 *
 * Объект client объявлен глобально за пределами функции main, так его можно
 * легко передать в другие функции или классы без создания новых экземпляров.
 * Также в продвинутых приложениях клиент часто передается через
 * DI (dependency injection), чтобы гарантировать единый экземпляр во всем
 * приложении. Это полезно для тестирования и управления зависимостями. Ну
 * и в конце концов для улучшения читаемости.
 */
val client = OkHttpClient()

fun main() {
    val symbol = "SOLUSDT"//Здесь тип String.
    val interval = "1m"//Тип enum. Здесь очень важен регистр.
    val limit = 1000//Это количество свечей.

    // URL для запроса.
    /*
    Uniform Resource Locator, сокр. URL (произносится [ю-ар-эл], [ˌjuː ɑːr ˈel];
    с англ.«единообразный указатель местонахождения ресурса») — адрес
    ресурса в сети Интернет.
     */
    /**
     * https://api.binance.com - это одна из базовых конечных точек(адрес в интернете
     * куда можно отправить запрос, чтобы получить данные или выполнить какое-либо
     * действие). Далее идёт конечная точка для получения данных свечей - /api/v3/klines.
     *
     * Знак вопроса указывает на начало строки запроса в URL. Для запроса данных свечей
     * необходимо указать два обязательных параметра symbol и interval. Амперсанд
     * используется для разделения нескольких параметров в строке запроса.
     */
    val url = "https://api.binance.com/api/v3/klines?symbol=$symbol&interval=$interval&limit=$limit"

    // Создаем запрос.
    /**
     * Здесь мы создаём объект Request.Builder(). Builder - это вложенный класс класса Request.
     *
     * .url(url) — задаёт URL, к которому будет отправлен запрос.
     *
     * .build() — завершает настройку запроса и создаёт сам объект Request, который
     * уже можно использовать для отправки.
     */
    val request = Request.Builder().url(url).build()

    // Выполняем запрос и обрабатываем ответ.
    /*
     client.newCall(request): Создает новый HTTP-вызов (Call) на основе объекта request
     (это должен быть экземпляр Request, содержащий всю информацию о запросе: URL,
     метод (GET/POST и т.д.), заголовки и т.д.).

     execute(): Выполняет запрос синхронно, то есть ожидает завершения запроса и
     возвращает результат в виде объекта Response.

     use { ... }: Функция use автоматически закрывает ресурс после завершения блока.
     Это важно, так как Response реализует интерфейс Closeable, и его нужно закрывать,
     чтобы избежать утечек памяти.
     */
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
                    Candlestick(
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
                candlesticks.forEach { println(it) }
                println()
                //Выведем общее количество свечей в списке.
                println(candlesticks.size)
            } catch (e: Exception) {
                // Обработка ошибки при десериализации JSON
                println("Ошибка при обработке JSON: ${e.message}")
            }
        }
    } catch (e: IOException) {
        // Обработка ошибки запроса
        println("Ошибка выполнения запроса: ${e.message}")
    }
}

@Serializable
data class Candlestick(
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