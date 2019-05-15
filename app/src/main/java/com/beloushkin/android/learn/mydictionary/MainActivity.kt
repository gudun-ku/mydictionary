package com.beloushkin.android.learn.mydictionary

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun findWord(view: View) {
        var stringUrl =
            "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" + edtWord.text.toString()

        MyAsyncTask().execute(stringUrl)
    }

    inner class MyAsyncTask : AsyncTask<String, Void, Data>() {
        override fun doInBackground(vararg params: String?): Data? {
            val url = createUrl(params[0])

            var jsonResponse: String?
            try {
                jsonResponse = getHttpResponse(url)
                val data = extractFeatureFromJson(jsonResponse)
                return data
            } catch (e: IOException) {
                Log.e("MainActivity", "Error while getting response")
            }
            return null
        }

        override fun onPostExecute(result: Data?) {
            super.onPostExecute(result)
        }

    }

    fun extractFeatureFromJson(definitionJson: String?):Data?{
        try {
            val baseJsonResponse = JSONObject(definitionJson)
            val definitions = baseJsonResponse
                .getJSONArray("results")!!
                .getJSONObject(0)!!
                .getJSONArray("lexicalEntries")!!
                .getJSONObject(0)!!
                .getJSONArray("entries")!!
                .getJSONObject(0)!!
                .getJSONArray("senses")!!
                .getJSONObject(0)!!
                .getJSONArray("definitions")

            Log.d("Definition", definitions[0].toString())

            return Data( definitions[0].toString())

        } catch (e: JSONException) {
            Log.e("MainActivity", "Error while parsing JSON")
            return null
        }
    }

    fun getHttpResponse(url: URL?):String {
        var jsonResponse = ""
        var urlConn: HttpURLConnection

        var inputStream: InputStream? = null

        try {
            urlConn = url?.openConnection() as HttpURLConnection
            urlConn.requestMethod = "GET"
            urlConn.setRequestProperty("Accept", "Application/json")
            urlConn.setRequestProperty("app_id", "5c7f0aad")
            urlConn.setRequestProperty("app_key", "17682fa2d223051c1d8b005eee42407b")
            urlConn.readTimeout = 10000
            urlConn.connectTimeout = 15000
            urlConn.connect()

            if (urlConn.responseCode == 200) {
                inputStream = urlConn.inputStream
                jsonResponse = readFromInputStream(inputStream)
            } else {
                Log.d("MainActivity","Error response code: " + urlConn.responseCode)
            }

            urlConn.disconnect()
            inputStream?.close()
        } catch (e: IOException) {
            Log.e("MainActivity","Connection error: " + e)
        }
        return jsonResponse
    }

    fun readFromInputStream(inputStream: InputStream): String {
        val output = StringBuilder()

        if (inputStream != null) {
            var inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()

            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }

        return output.toString()
    }


    fun createUrl(strUrl: String?): URL? {
        var url : URL?
        try {
            url = URL(strUrl)
        } catch (exception: MalformedURLException) {
            Log.d("MainActivity", "Error in creating URL")
            return null
        }
        return  url
    }
}
