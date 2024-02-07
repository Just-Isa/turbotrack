
import android.annotation.SuppressLint
import android.os.AsyncTask
import com.google.gson.Gson
import com.kidrich.turbotrack.ApiTaskCallback
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
@SuppressLint("NewApi")
class OpenFoodFactsApiTask(
    private val callback: ApiTaskCallback
) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String): String? {
        val barcode = params[0]
        val apiUrl = "https://world.openfoodfacts.org/api/v2/product/$barcode?fields=nutriments,product_name"

        return try {
            val url = URL(apiUrl)
            val urlConnection = url.openConnection() as HttpURLConnection

            try {
                val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val stringBuilder = StringBuilder()

                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }

                stringBuilder.toString()
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: IOException) {
            null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String?) {
        if (result != null) {
            val gson: Gson = Gson()
            val data = gson.fromJson(
                result,
                ProductResponse::class.java)
            callback.onApiTaskComplete(data)
        } else {
            callback.onApiTaskError()
        }
    }
}
