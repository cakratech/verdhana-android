package com.bcasekuritas.mybest.app.feature.rdn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.app.base.BaseViewHolder
import com.bcasekuritas.mybest.app.domain.dto.response.source.HowToTransferRes
import com.bcasekuritas.mybest.databinding.ItemHowToTransferBinding
import com.bcasekuritas.mybest.ext.common.copyToClipboardFromString
import com.bcasekuritas.mybest.ext.listener.OnClickStr

class TransferMBankingAdapter(val onClickCopy: OnClickStr) : RecyclerView.Adapter<BaseViewHolder>(){

    private val listData: ArrayList<HowToTransferRes> = arrayListOf()
    var i = 1
    var code = ""

    fun setRdnCode(rdn: String) {
        this.code = rdn
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater  = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemHowToTransferBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int = listData.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(listData[position])

    inner class ItemViewHolder(
        val binding: ItemHowToTransferBinding
    ) : BaseViewHolder(binding.root) {

        override fun onBind(obj: Any) {
            val data = obj as HowToTransferRes

            binding.tvNumber.text = i.toString()
            binding.tvDesc.text = data.description

            if (i==4){
                binding.ivClientCodeCopy.visibility = View.VISIBLE
            }

            binding.ivClientCodeCopy.setOnClickListener {
                onClickCopy.onClickStr(code)
            }

            i++
        }
    }

    fun setData(list: List<HowToTransferRes>?) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }
}