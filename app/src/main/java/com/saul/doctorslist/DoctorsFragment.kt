package com.saul.doctorslist

import android.content.Intent
import android.os.Bundle
import com.saul.R
import com.saul.doctorslist.dummy.DummyContent
import com.saul.doctorslist.dummy.DummyContent.DummyItem
import com.saul.video.BaseActivity
import com.saul.video.CallScreenActivity
import com.saul.video.SinchService
import com.sinch.android.rtc.calling.Call
import kotlinx.android.synthetic.main.fragment_doctors_list.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DoctorsFragment.OnListFragmentInteractionListener] interface.
 */

interface OnListFragmentInteractionListener {
    // TODO: Update argument type and name
    fun onListFragmentInteraction(item: DummyItem?)
}

class DoctorsFragment : BaseActivity(), OnListFragmentInteractionListener {

    private lateinit var adapter: MyItemRecyclerViewAdapter
    // TODO: Customize parameters
    private var columnCount = 1

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_doctors_list)

        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val doctors = resources.getStringArray(R.array.doctors)
        val departments = resources.getStringArray(R.array.departments)
        val items = ArrayList<DummyItem>()
        for(i in doctors.indices) {
            items.add(DummyItem(doctors[i], departments[i], ""))
        }
        adapter = MyItemRecyclerViewAdapter(items, this)
        list.adapter = adapter
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_doctors_list, container, false)
//
//        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                adapter = MyItemRecyclerViewAdapter(DummyContent.ITEMS, listener)
//            }
//        }
//        return view
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
//    }

//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

//        // TODO: Customize parameter initialization
//        @JvmStatic
//        fun newInstance(columnCount: Int) =
//            DoctorsFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_COLUMN_COUNT, columnCount)
//                }
//            }
    }

    override fun onListFragmentInteraction(item: DummyItem?) {
        val call: Call = getSinchServiceInterface().callUserVideo("doctor")
        val mCallId = call.callId
        val mainActivity = Intent(this, CallScreenActivity::class.java)
        mainActivity.putExtra(SinchService.CALL_ID, mCallId)
        startActivity(mainActivity)
    }
}
