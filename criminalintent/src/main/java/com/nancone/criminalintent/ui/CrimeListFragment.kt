package com.nancone.criminalintent.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nancone.criminalintent.R
import com.nancone.criminalintent.model.Crime
import com.nancone.criminalintent.model.CrimeType
import com.nancone.criminalintent.viewmodel.CrimeListViewModel
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    /**
     * 호스팅 액티비티에서 구현할 인터페이스
     */
    interface Callbacks {
        fun onCrimesSelected(crimeId: UUID)
    }
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList()) // initialize empty list

    // CrimeListViewModel 참조
    // ViewModel이 Fragment와 같이 사용되면 ViewModel의 생명주기는 Fragment의 생명주기를 따라감
    private val crimeListViewModel: CrimeListViewModel by lazy {
        // ViewModelProvider(this): 현재의 CrimeListFragment 인스턴스와 연관된 ViewModelProvider 인스턴스를 생성하고 반환
        // get(CrimeListViewModel::class.java): CrimeListViewModel 인스턴스 반환
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        // recyclerview 설정
        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crimes ->
                crimes?.let {
                    Log.d(TAG, "onViewCreated: Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            }
        )
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    // ViewHolder
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val crimeSolved: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            val dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.US)
            dateTextView.text = dateFormat.format(crime.date).toString()
//            dateTextView.text = crime.date.toString()
            crimeSolved.visibility = if(crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class CrimeRequirePoliceHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val callPoliceBtn: Button = itemView.findViewById(R.id.call_police_btn)
        private val crimeSolved: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            callPoliceBtn.setOnClickListener(this)
            titleTextView.setOnClickListener(this)
//            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toString()
            crimeSolved.visibility = if(crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View) {
           when(v.id) {
               R.id.crime_title -> Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
               R.id.call_police_btn -> Toast.makeText(context, "Call Police", Toast.LENGTH_SHORT).show()

           }
        }
    }


    private inner class CrimeAdapter(var crimes: List<Crime>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
//            super.getItemViewType(position)
            return crimes[position].requiresPolice
        }

        // onCreateViewHolder 에서 item 틀에 맞춰 ViewHolder 만들고 해당 ViewHolder 가 화면에 보여질만큼 충분히
        // 만들어지면 이제 그 View 는 재사용함
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view: View? = null
            return when(viewType) {
                CrimeType.requiresPoliceCrime -> {
                    view = layoutInflater.inflate(R.layout.list_item_crime_requires_police, parent, false)
                    CrimeRequirePoliceHolder(view)
                }
                else -> {
                    view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                    CrimeHolder(view)
                }
            }
//            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
//            // CrimeHolder Instance 생성: 아직 CrimeHolder 에 데이터는 반영되지 않음
//            return CrimeHolder(view)
        }

        // onBindViewHolder 에서 CrimeHolder 와 데이터셋 내부 위치를 전달
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val crime= crimes[position]
            when(holder) {
                is CrimeHolder -> holder.bind(crime)
                is CrimeRequirePoliceHolder -> holder.bind(crime)
            }
//            holder.bind(crime)
//            holder.apply { // CrimeHolder(=View) 에 Data 세팅
//                titleTextView.text = crime.title
//                dateTextView.text = crime.date.toString()
//            }
        }

        override fun getItemCount(): Int = crimes.size

    }
}