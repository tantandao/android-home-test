package vn.tiki.android.androidhometest.adapter

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import vn.tiki.android.androidhometest.R
import vn.tiki.android.androidhometest.data.api.response.Deal
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DealAdapter(private val context: Context, private var dataSource: ArrayList<Deal>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var filterSource: ArrayList<Deal> = ArrayList()

    fun setDataSource(dataSource: ArrayList<Deal>) {
        this.dataSource = dataSource
        filterSource = this.dataSource.filter { System.currentTimeMillis() >= it.startedDate.time && System.currentTimeMillis() <= it.endDate.time }.toMutableList() as ArrayList<Deal>
    }

    override fun getCount(): Int = filterSource.size

    override fun getItem(position: Int): Any? = filterSource[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.layout_deal, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item = filterSource[position]

        Glide.with(context).load(item.productThumbnail).into(viewHolder.ivProductThumbnail)
        viewHolder.tvProductName.text = item.productName
        viewHolder.tvProductPrice.text = DecimalFormat("#,###,###")
                .format(item.productPrice * 1000)
                .toString()
                .replace(",", ".")
                .plus(" đ")

        object : CountDownTimer(item.endDate.time - item.startedDate.time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val stringBuilder = StringBuilder()
                stringBuilder.append("Kết thúc sau: ")
                stringBuilder.append(timeString(millisUntilFinished))

                viewHolder.tvDueDate.text = stringBuilder.toString()
            }

            override fun onFinish() {
                filterSource = dataSource.filter { System.currentTimeMillis() >= it.startedDate.time && System.currentTimeMillis() <= it.endDate.time }.toMutableList() as ArrayList<Deal>
                notifyDataSetChanged()
            }
        }.start()

        return view
    }

    private fun timeString(millisUntilFinished: Long): String {
        var milliseconds: Long = millisUntilFinished

        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return String.format(Locale.getDefault(), "%02d giờ %02d phút %02d giây", hours, minutes, seconds)
    }

    private class ViewHolder(view: View) {
        val ivProductThumbnail: ImageView = view.findViewById(R.id.ivProductThumbnail)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val tvDueDate: TextView = view.findViewById(R.id.tvDueDate)
    }
}