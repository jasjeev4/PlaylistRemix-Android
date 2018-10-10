package science.logarithmic.recentsplaylist

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scrolling.*

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.multimoon.colorful.CAppCompatActivity
import kotlinx.android.synthetic.main.content_scrolling.*
import science.logarithmic.recentsplaylist.ResultModel;
import java.util.*

class ScrollingActivity : CAppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        viewManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)
        val message = intent.getStringExtra("result")
        handleResponse(message)
        fab.setOnClickListener { view ->
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun handleResponse(data: String) {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val modelAdapter = moshi.adapter(ResultModel::class.java)
        val json = modelAdapter.fromJson(data)
        scroller.text = json?.result?.playlist.toString();
        val tracks = json?.result?.playlist?.tracks
        val len = tracks!!.size
        var trackNames: Array<String> = arrayOf(String())
        var i = 0
        for(track in tracks!!) {
            trackNames[i++] = track.name!!
        }

        viewAdapter = MyAdapter(trackNames)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }


}
