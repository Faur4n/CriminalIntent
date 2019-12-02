package com.example.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.text.DateFormat
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"
private const val DIALOGE_DATE = "DialogDate"
private const val DIALOGE_TIME = "DialogTime"
private const val REQUEST_DATE = 0
private const val REQUEST_TIME = 1
private const val REQUEST_CONTACT = 3
private const val DATE_FORMAT = "EEE, MMM, dd"




class CrimeFragment : Fragment(),DatePickerFragment.Callbacks,TimePickerFragment.Callbacks {



    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var policeCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button




//    private val appCompatActivity = activity as AppCompatActivity
//    private val appBar = appCompatActivity.supportActionBar as Toolbar

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        policeCheckBox = view.findViewById(R.id.requires_police) as CheckBox
        reportButton = view.findViewById(R.id.send_crime_report) as Button
        suspectButton = view.findViewById(R.id.choose_suspect) as Button



//      Date Button disabled
//        dateButton.apply {
//            text = crime.date.toString()
//            isEnabled = false
//        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.supportActionBar?.setTitle(R.string.new_crime)
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun onTextChanged(
                sequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()

            }

            override fun beforeTextChanged(
                sequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun afterTextChanged(p0: Editable?) {
                //To change body of created functions use File | Settings | File Templates.
            }


        }



        titleField.addTextChangedListener(titleWatcher)

        timeButton.setOnClickListener{
            TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.requireFragmentManager(), DIALOGE_TIME)
            }
        }

        dateButton.setOnClickListener{
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment , REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOGE_DATE)
            }
        }

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }

        }

        policeCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.requiresPolice = isChecked
            }

        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type="text/plain"
                putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent,getString(R.string.send_crime_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
            //pickContactIntent.addCategory(Intent.CATEGORY_HOME)
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }



    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDateSelected(date: Date){
        crime.date = date
        updateUI()
    }

    override fun onTimeSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        policeCheckBox.apply {
            isChecked = crime.requiresPolice
            jumpDrawablesToCurrentState()
        }

        if(crime.suspect.isNotEmpty()){
            suspectButton.text = crime.suspect
        }
    }

    private  fun getCrimeReport() :String {
        val solvedString = if (crime.isSolved){
            getString(R.string.crime_report_solved)
        }else{
            getString(R.string.crime_report_unsolved)
        }

        val dateString = android.text.format.DateFormat.format(DATE_FORMAT,crime.date).toString()

        val suspect = if(crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        }else{
            getString(R.string.crime_report_suspect,crime.suspect)
        }

        return getString(R.string.crime_report,crime.title,dateString,solvedString,suspect)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null ->{
                val contactUri: Uri? = data.data

                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                var cursor = requireActivity().contentResolver.query(contactUri!!,queryFields,null,null,null)

                cursor?.use {
                    if(it.count == 0){
                        return
                    }

                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect  = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }
        }
    }

    companion object {
        fun newInstance(crimeID: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeID)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}