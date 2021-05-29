package com.olamachia.citizenregistrationapp.ui.dashboard

import android.os.Bundle
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.olamachia.citizenregistrationapp.R
import com.olamachia.citizenregistrationapp.adapter.CitizenDetailsSummaryAdapter
import com.olamachia.citizenregistrationapp.data.Hospital
import com.olamachia.citizenregistrationapp.databinding.FragmentDashboardBinding
import com.olamachia.citizenregistrationapp.ui.utils.SessionManager
import com.olamachia.citizenregistrationapp.ui.utils.USERNAME
import com.olamachia.citizenregistrationapp.ui.utils.USER_UID
import com.olamachia.citizenregistrationapp.ui.viewmodels.CitizenViewModel


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var currentBinding: FragmentDashboardBinding? = null
    private val binding get() = currentBinding!!
    private lateinit var hospitalName: String
    private val args: DashboardFragmentArgs by navArgs()
    private val viewModel: CitizenViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentDashboardBinding.bind(view)


        hospitalName = if (args.hospital != null) {
            val hospital = args.hospital as Hospital
            hospital.name.toString()
        } else {
            SessionManager.load(requireContext(), USERNAME)
        }

        val uuid = SessionManager.load(requireContext(), USER_UID)

        if (uuid.isNotEmpty()) {
            viewModel.getDetailsFromFirebase(requireContext())
        }

        binding.apply {
            dashboardGreetingsTv.text = hospitalName
            toolbar.mainToolBar.inflateMenu(R.menu.menu)

            val menuTitle = SpannableString("       Log out")
            menuTitle.setSpan(
                ImageSpan(requireContext(), R.drawable.sign_out),
                0,
                4,
                DynamicDrawableSpan.ALIGN_BASELINE
            )

            toolbar.mainToolBar.menu.findItem(R.id.menu_logout).title = menuTitle

            toolbar.mainToolBar.menu.findItem(R.id.menu_logout).setOnMenuItemClickListener {
                findNavController().navigate(R.id.landingFragment)
                true
            }

            val citizenDetailsSummaryAdapter = CitizenDetailsSummaryAdapter(
                mutableListOf())

            dashboardFragmentRv.apply {
                adapter = citizenDetailsSummaryAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            dashboardFab.setOnClickListener {
                findNavController().navigate(R.id.citizenRegistrationFragment)
            }

            viewModel.citizenDataLiveData.observe(viewLifecycleOwner, { data ->

                if (data != null) {
                    val filteredData = data.filter { it.child != null }
                    citizenDetailsSummaryAdapter.submitList(filteredData)
                } else {
                    Toast.makeText(
                        requireContext(), getString(R.string.citizen_data_failure),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })


        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }

}