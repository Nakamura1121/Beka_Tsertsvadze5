import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beka_tsertsvadze5.Item
import com.example.beka_tsertsvadze5.ItemAdapter
import com.example.beka_tsertsvadze5.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = ItemAdapter()
        recyclerView.adapter = itemAdapter

        fetchData()
    }

    private fun fetchData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://reqres.in/api/unknown")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val items = parseItems(responseBody)
                    runOnUiThread {
                        itemAdapter.setItems(items)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun parseItems(jsonData: String?): MutableList<Item> {
        val items = mutableListOf<Item>()
        jsonData?.let {
            val jsonObject = JSONObject(it)
            val jsonArray = jsonObject.getJSONArray("data")

            for (i in 0 until jsonArray.length()) {
                val itemObject = jsonArray.getJSONObject(i)
                val id = itemObject.getInt("id")
                val name = itemObject.getString("name")
                val year = itemObject.getInt("year")
                val color = itemObject.getString("color")
                val pantoneValue = itemObject.getString("pantone_value")

                val item = Item(id, name, year, color, pantoneValue)
                items.add(item)
            }
        }
        return items
    }
}
