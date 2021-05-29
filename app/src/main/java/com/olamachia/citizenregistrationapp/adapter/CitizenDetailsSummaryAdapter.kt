package com.olamachia.citizenregistrationapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.olamachia.citizenregistrationapp.R
import com.olamachia.citizenregistrationapp.data.Citizen
import com.olamachia.citizenregistrationapp.data.CitizenDetailsSummary
import com.olamachia.citizenregistrationapp.databinding.ExpandableCitizenItemBinding

class CitizenDetailsSummaryAdapter(private var citizenDetailsSummary: List<Citizen>):
    RecyclerView.Adapter<CitizenDetailsSummaryAdapter.CitizenDetailsSummaryViewHolder>() {

    inner class CitizenDetailsSummaryViewHolder(private val binding: ExpandableCitizenItemBinding):
        RecyclerView.ViewHolder(binding.root) {

            fun bind(citizenDetailsSummary: Citizen) {
                binding.apply {
                    expandableListNameTv.text = citizenDetailsSummary.child?.firstname ?: ""
                    expandableListRegNoTv.text = citizenDetailsSummary.father?.nationalIDNumber ?: ""
                    expandableListGenderTv.text = citizenDetailsSummary.child?.gender ?: ""
                    expandableListStatusTv.text = citizenDetailsSummary.regStatus ?: ""
                    expandableListTimeTv.text = citizenDetailsSummary.dateAdded

                    when (citizenDetailsSummary.regStatus) {
                        "Accepted" -> {
                            expandableListStatusTv.setTextColor(
                                ResourcesCompat.getColor(
                                    root.context.resources,
                                    R.color.dark_green,
                                    null
                                )
                            )
                        }
                        "Pending" -> {
                            expandableListStatusTv.setTextColor(
                                ResourcesCompat.getColor(
                                    root.context.resources,
                                    R.color.yellow,
                                    null
                                )
                            )
                        }
                        else -> {
                            expandableListStatusTv.setTextColor(
                                ResourcesCompat.getColor(
                                    root.context.resources,
                                    R.color.red,
                                    null
                                )
                            )
                        }
                    }

                    expandableListTopCardCv.setOnClickListener {
                        if (expandableListToggleCv.visibility == View.VISIBLE) {
                            expandableListIconImg.setImageResource(R.drawable.minimize_add)
                            expandableListTopCardCv.strokeWidth = 3
                            expandableListToggleCv.visibility = View.GONE
                            expandableListTopCardCv.setBackgroundColor( ResourcesCompat.getColor(
                                root.context.resources,
                                R.color.white,
                                null
                            ))
                        } else {
                            expandableListTopCardCv.strokeWidth = 0
                            expandableListIconImg.setImageResource(R.drawable.minimize)
                            expandableListToggleCv.visibility = View.VISIBLE
                            expandableListTopCardCv.setBackgroundColor( ResourcesCompat.getColor(
                                root.context.resources,
                                R.color.primary_light_blue,
                                null
                            ))
                        }
                    }

                }


            }

        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitizenDetailsSummaryViewHolder {
        val binding = ExpandableCitizenItemBinding.
        inflate(LayoutInflater.from(parent.context), parent, false)

        return CitizenDetailsSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitizenDetailsSummaryViewHolder, position: Int) {
        holder.bind(citizenDetailsSummary[position])
    }

    override fun getItemCount(): Int = citizenDetailsSummary.size

    fun submitList(citizenDetailsSummary: List<Citizen>) {
        this.citizenDetailsSummary = citizenDetailsSummary
        notifyDataSetChanged()
    }
}