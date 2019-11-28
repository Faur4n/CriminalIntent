package com.example.criminalintent

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_crime_list.*
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "CrimeListFragment"

private const val DATE_FORMAT_1 = "EEEE, MMM d, yyyy"


class CrimeListFragment : Fragment() {


    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())



    private val crimeListViewModel: CrimeListVIewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListVIewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        val addCrimeButton = view.findViewById<Button>(R.id.add_crime_button)

        addCrimeButton.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }

        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner, Observer { crimes  ->
                crimes?.let {
                    Log.i(TAG, "got crime ${crimes.size}")
                    updateUI(crimes)
                }

            }
        )
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.supportActionBar?.setTitle(R.string.app_name)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private val titleTextView = view.findViewById<TextView>(R.id.crime_title)
        private val dateTextView = view.findViewById<TextView>(R.id.crime_date)
        private val solvedImageView = view.findViewById<ImageView>(R.id.crime_solved)

        var dateFormat = SimpleDateFormat(DATE_FORMAT_1)


        private lateinit var crime: Crime

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = dateFormat.format(this.crime.date)
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {

            callbacks?.onCrimeSelected(crime.id)
        }
    }


    private inner class CrimePoliceHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private val titleTextView = view.findViewById<TextView>(R.id.crime_title)
        private val dateTextView = view.findViewById<TextView>(R.id.crime_date)
        private val callImageView = view.findViewById<ImageView>(R.id.crime_police_button)
        private val solvedImageView = view.findViewById<ImageView>(R.id.crime_solved)

        var dateFormat = SimpleDateFormat(DATE_FORMAT_1)


        val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+79776015423"))


        private lateinit var crime: Crime

        init {
            itemView.setOnClickListener(this)
            callImageView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = dateFormat.format(this.crime.date)
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }


        override fun onClick(v: View) {
            if (v.id == callImageView.id) {
                startActivity(phoneIntent)
            } else Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_REQUIRES = 1
        private val TYPE_NOT_REQUIRES = 2

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == TYPE_REQUIRES) {
                val crime = crimes[position]
                (holder as CrimePoliceHolder).bind(crime)
            } else {
                val crime = crimes[position]
                (holder as CrimeHolder).bind(crime)
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return if (viewType == TYPE_REQUIRES) {
                val view = layoutInflater.inflate(R.layout.list_item_crime_police, parent, false)
                CrimePoliceHolder(view)
            } else {
                val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                CrimeHolder(view)
            }

        }

        override fun getItemCount(): Int = crimes.size

//        override fun getItemViewType(position: Int): Int {
//            return if (crimes[position].requiresPolice) {
//                TYPE_REQUIRES
//
//            } else {
//                TYPE_NOT_REQUIRES
//            }
//        }

    }

    private fun updateUI(crimes: List<Crime>) {
            if(crimes.isEmpty()){
                add_crime_button.visibility = View.VISIBLE
                no_crime_text_view.visibility = View.VISIBLE
            }else{
                add_crime_button.visibility = View.GONE
                no_crime_text_view.visibility = View.GONE
            }
            adapter = CrimeAdapter(crimes)
            crimeRecyclerView.adapter = adapter

    }


    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}