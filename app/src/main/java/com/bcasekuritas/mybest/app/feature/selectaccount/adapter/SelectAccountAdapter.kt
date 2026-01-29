package com.bcasekuritas.mybest.app.feature.selectaccount.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.source.AccountRes
import com.bcasekuritas.mybest.databinding.ItemSelectAccountBinding
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import java.util.TreeMap

class SelectAccountAdapter(private val onClickAny: OnClickAny) : RecyclerView.Adapter<BaseViewHolder>(){
    private val data: ArrayList<AccountRes> = arrayListOf()

    private var selectedItemPosition = -1
    private var selected = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemSelectAccountBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(data[position])

    }

    inner class ItemViewHolder(
        val binding: ItemSelectAccountBinding
    ): BaseViewHolder(binding.root){
        @SuppressLint("SuspiciousIndentation")
        override fun onBind(obj: Any) {
            val item = obj as AccountRes
            val accInt = item.accno?.substring(0,2)
            binding.apply {
                tvAccountName.text = "$accInt - ${item.accname}"

                rbtnAccount.isChecked = selectedItemPosition == position
                rbtnAccount.isEnabled = selectedItemPosition == position

                clAccount.setOnClickListener {
                    selected = item.accno.toString()
                    val tree = TreeMap<String, String>()
                    tree["accNo"] = item.accno.toString()
                    tree["cifCode"] = item.cifCode.toString()
                    tree["selected"] = selected

                    onClickAny.onClickAny(tree)
                    selectedItemPosition = position
                            notifyDataSetChanged()
                }

            }

        }
    }

    fun setData(list: List<AccountRes>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}