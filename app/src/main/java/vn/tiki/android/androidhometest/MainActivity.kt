package vn.tiki.android.androidhometest

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.GridView
import vn.tiki.android.androidhometest.adapter.DealAdapter
import vn.tiki.android.androidhometest.data.api.ApiServices
import vn.tiki.android.androidhometest.data.api.response.Deal
import vn.tiki.android.androidhometest.di.initDependencies
import vn.tiki.android.androidhometest.di.inject
import vn.tiki.android.androidhometest.di.releaseDependencies

class MainActivity : AppCompatActivity() {

    private val apiServices by inject<ApiServices>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDependencies(this)

        setContentView(R.layout.activity_main)

        val gridView: GridView = findViewById(R.id.gridview)
        val dataSource = ArrayList<Deal>()
        val adapter = DealAdapter(this, dataSource)

        gridView.adapter = adapter

        adapter.setDataSource(GetDealTask(apiServices).execute().get().toMutableList() as ArrayList<Deal>)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseDependencies()
    }
}

class GetDealTask(private val apiServices: ApiServices) : AsyncTask<Unit, Unit, List<Deal>>() {

    override fun doInBackground(vararg params: Unit?): List<Deal> {
        return apiServices.getDeals()
    }

    override fun onPostExecute(result: List<Deal>?) {
        super.onPostExecute(result)
        result.orEmpty()
                .forEach { deal ->
                    println(deal)
                }
    }
}
