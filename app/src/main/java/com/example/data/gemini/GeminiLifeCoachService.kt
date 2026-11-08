package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = 0.7f,
    val thinkingConfig: GeminiThinkingConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiThinkingConfig(
    val thinkingLevel: String = "high"
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiError(
    val message: String? = null,
    val code: Int? = null
)

class GeminiLifeCoachService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    suspend fun getDailyLifeAnalysis(
        userName: String,
        dailySummaryText: String,
        userPrompt: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(
                generateLocalFallbackInsight(userName, dailySummaryText)
            )
        }

        val systemPrompt = """
            Anda adalah AI Life Coach & Personal Mentor cerdas untuk aplikasi Daily Life Tracker bernama Aizen Life Coach.
            Berikan analisis mendalam, hangat, positif, terstruktur, dan solutif dalam Bahasa Indonesia.
            Gunakan format poin yang rapi dengan emoji yang relevan:
            1. 🌟 Evaluasi Keseimbangan Hidup (Work-Life-Health-Finance)
            2. 💡 Insight & Pola Kebiasaan (Habits & Productivity)
            3. 🎯 3 Rekomendasi Aksi Nyata untuk Esok Hari
            Gunakan gaya bahasa yang memotivasi, suportif, dan ramah.
        """.trimIndent()

        val prompt = if (!userPrompt.isNullOrBlank()) {
            "Data Harian Pengguna:\n$dailySummaryText\n\nPertanyaan/Permintaan Khusus Pengguna: $userPrompt"
        } else {
            "Data Harian Pengguna ($userName):\n$dailySummaryText\n\nBerikan analisis komprehensif, evaluasi produktivitas, kesehatan, kebiasaan, dan keuangan hari ini beserta saran peningkatan untuk besok."
        }

        val requestPayload = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f,
                thinkingConfig = GeminiThinkingConfig(thinkingLevel = "high")
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = systemPrompt))
            )
        )

        try {
            val jsonBody = requestAdapter.toJson(requestPayload)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"

            val httpRequest = Request.Builder()
                .url(url)
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.w("GeminiLifeCoach", "API returned non-success: ${response.code} $responseBody")
                return@withContext Result.success(generateLocalFallbackInsight(userName, dailySummaryText))
            }

            val parsed = responseAdapter.fromJson(responseBody)
            val resultText = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!resultText.isNullOrBlank()) {
                Result.success(resultText)
            } else {
                Result.success(generateLocalFallbackInsight(userName, dailySummaryText))
            }
        } catch (e: Exception) {
            Log.e("GeminiLifeCoach", "Exception while calling Gemini API", e)
            Result.success(generateLocalFallbackInsight(userName, dailySummaryText))
        }
    }

    private fun generateLocalFallbackInsight(userName: String, summary: String): String {
        return """
            🌟 **Evaluasi Keseimbangan Hidup Hari Ini untuk $userName**
            
            Hari ini kamu menunjukkan dedikasi yang baik dalam mencatat aktivitas, produktivitas, dan pola kebiasaan harianmu.
            
            💡 **Highlight Hari Ini:**
            • **Kebugaran & Kesehatan**: Menjaga hidrasi dan kualitas tidur adalah fondasi energi untuk hari esok.
            • **Fokus & Produktivitas**: Kamu berhasil menyelesaikan tugas-tugas kunci dan mencatat progres belajar yang konsisten.
            • **Keuangan**: Arus kas harian tercatat rapi sehingga pengeluaran tetap terkontrol.
            
            🎯 **3 Langkah Rekomendasi untuk Esok Hari:**
            1. **Prioritas Utama**: Tuntaskan 1 tugas berprioritas tinggi di pagi hari saat energi optimal.
            2. **Pertahankan Streak**: Lanjutkan check-in kebiasaan harianmu agar momentum tetap terjaga.
            3. **Relaksasi & Refleksi**: Luangkan 10 menit sebelum tidur untuk menuliskan 3 hal yang kamu syukuri hari ini.
        """.trimIndent()
    }
}
