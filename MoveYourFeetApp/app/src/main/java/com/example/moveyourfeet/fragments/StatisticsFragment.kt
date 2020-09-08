package com.example.moveyourfeet.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.moveyourfeet.R
import com.example.moveyourfeet.database.SpeedDataEntity
import com.example.moveyourfeet.database.SpeedDatabase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.statistics_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

////Alexander Schröder 4schoa24///
class StatisticsFragment : Fragment() {

    //database variable
    lateinit var speedDatabase: SpeedDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var statisticsFragmentView =
            inflater.inflate(R.layout.statistics_fragment, container, false)


        speedDatabase = SpeedDatabase.getDatabase((requireActivity().application))
        handleGraphData(statisticsFragmentView)
        fillTimeTexView(statisticsFragmentView)

        statisticsFragmentView.btn_statisticsFragment_deleteData.setOnClickListener {

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    speedDatabase.speedDataDao().deleteAllStatistics()
                }
                Toast.makeText(activity, "All data was deleted", Toast.LENGTH_LONG).show()
            }
        }


        return statisticsFragmentView

    }

    private fun fillTimeTexView(statisticsFragmentView: View) {
        lifecycleScope.launch {
            var minSpeedId: SpeedDataEntity? = null
            var maxSpeedId: SpeedDataEntity? = null

            withContext(Dispatchers.IO) {
                minSpeedId = speedDatabase.speedDataDao().getMinId()
                maxSpeedId = speedDatabase.speedDataDao().getMaxId()
            }
            statisticsFragmentView.tv_statistic_timeInfo.text =
                "Data from " + maxSpeedId?.startHour?.toInt() + ":" + minSpeedId?.startMinute?.toInt() + " to " + maxSpeedId?.startHour?.toInt() + ":" + maxSpeedId?.startMinute?.toInt() + "."
        }
    }

    //function to fill the graph with the data collected from gps in home fragment through an sqlite database
    private fun handleGraphData(statisticsFragmentView: View) {
        lifecycleScope.launch {
            var allSpeeds = listOf<SpeedDataEntity>()
            var entries = ArrayList<Entry>()
            withContext(Dispatchers.IO) {
                allSpeeds = speedDatabase.speedDataDao().getAllSpeeds()
            }
            for (speed in allSpeeds) {
                entries.add(Entry(speed.id.toFloat(), speed.speed.toFloat()))
            }
            var dataset = LineDataSet(entries, "Speed over Time")


            var linedata = LineData(dataset)


            statisticsFragmentView.lcv_statisticsFragment_lineChart.data = linedata
            var labelDescription = Description()
            labelDescription.text = "X axis is Time. Y axis is the speed."
            labelDescription.textSize = 10f
            statisticsFragmentView.lcv_statisticsFragment_lineChart.description = labelDescription
            statisticsFragmentView.lcv_statisticsFragment_lineChart.setNoDataText("No data found please start a route!")
            statisticsFragmentView.lcv_statisticsFragment_lineChart.invalidate()
        }
    }


}