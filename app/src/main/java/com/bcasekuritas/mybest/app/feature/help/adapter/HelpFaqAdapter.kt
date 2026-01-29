package com.bcasekuritas.mybest.app.feature.help.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.FaqHelpData
import com.bcasekuritas.mybest.databinding.ItemHelpQuestionsBinding
import com.bcasekuritas.mybest.widget.webview.CustomTabLinkMovementMethod

class HelpFaqAdapter(
    val context: Context
) : RecyclerView.Adapter<HelpFaqAdapter.ItemViewHolder>() {

    private val listData: ArrayList<FaqHelpData> = arrayListOf()

    fun setData(list: List<FaqHelpData>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun addData(list: List<FaqHelpData>){
        if (list.isNotEmpty()){
            listData.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemHelpQuestionsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemHelpQuestionsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: FaqHelpData) {
            binding.apply {

                tvTitleFaq.text = data.questionName
                val answer = data.answer
                tvAnswer.text = if (answer.contains("<") && answer.contains(">")) {
                    Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    answer
                }
                tvAnswer.movementMethod = CustomTabLinkMovementMethod(context)

                // state ui
                val isExpandDrawable = ContextCompat.getDrawable(context, R.drawable.ic_bot_row)
                val isNotExpandDrawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_right_black)

                lyFaq.setOnClickListener {
                    if (expandFaq.isExpanded) {
                        icExpandable.setImageDrawable(isNotExpandDrawable)
                        expandFaq.collapse()
                    } else {
                        icExpandable.setImageDrawable(isExpandDrawable)
                        expandFaq.expand()
                    }
                }


//                val selectedHelpfulDrawable = ContextCompat.getDrawable(context, R.drawable.ic_selected_thumbs_up)
//                val selectedUnHelpfulDrawable = ContextCompat.getDrawable(context, R.drawable.ic_selected_thumbs_down)
//                val unSelectedHelpfulDrawable = ContextCompat.getDrawable(context, R.drawable.ic_thumbs_up)
//                val unSelectedUnHelpfulDrawable = ContextCompat.getDrawable(context, R.drawable.ic_thumbs_down)

                // 0 not respond, 1 helpful, 2 not helpful
//                var isHelpful = false
//                var isUnHelpful = false
//                tvHelpfulYes.setOnClickListener {
//                    isHelpful = !isHelpful
//                    if (isHelpful) {
//                        tvHelpfulYes.setCompoundDrawablesRelativeWithIntrinsicBounds(selectedHelpfulDrawable, null, null,null)
//                        tvHelpfulNo.setCompoundDrawablesRelativeWithIntrinsicBounds(unSelectedUnHelpfulDrawable, null, null,null)
//                        isUnHelpful = false
//                    } else {
//                        tvHelpfulYes.setCompoundDrawablesRelativeWithIntrinsicBounds(unSelectedHelpfulDrawable, null, null,null)
//                    }
//                }
//
//                tvHelpfulNo.setOnClickListener {
//                    isUnHelpful = !isUnHelpful
//                    if (isUnHelpful) {
//                        tvHelpfulNo.setCompoundDrawablesRelativeWithIntrinsicBounds(selectedUnHelpfulDrawable, null, null,null)
//                        tvHelpfulYes.setCompoundDrawablesRelativeWithIntrinsicBounds(unSelectedHelpfulDrawable, null, null,null)
//                        isHelpful = false
//                    } else {
//                        tvHelpfulNo.setCompoundDrawablesRelativeWithIntrinsicBounds(unSelectedUnHelpfulDrawable, null, null,null)
//                    }
//                }
            }
        }

    }
}