package com.bcasekuritas.mybest.app.feature.managedevice.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.TrustedDeviceItem
import com.bcasekuritas.mybest.databinding.ItemManageDeviceBinding
import com.bcasekuritas.mybest.ext.listener.OnClickInt
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDevice

class ManageDeviceAdapter(private val onRemoveClick: OnClickStr) : RecyclerView.Adapter<ManageDeviceAdapter.ItemViewHolder>() {

    private val listData: ArrayList<TrustedDeviceItem> = arrayListOf()

    fun setData(list: List<TrustedDeviceItem>) {
        if (list == null) return
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemManageDeviceBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(listData[position])

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemManageDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TrustedDeviceItem) {
            binding.apply {


                val iconDevice = if (item.platform.contains("Mobile")) R.drawable.ic_device_phone else R.drawable.ic_device_desktop
                ivDeviceType.setImageResource(iconDevice)

                tvDeviceName.text = item.deviceName
                tvLoginDate.text= "Login ${item.date}"
                tvLoginIp.text = "IP: ${item.ip}"
                tvDevicePlatform.text = item.platform

                btnRemove.setOnClickListener {
                    onRemoveClick.onClickStr(item.deviceId)
                }
            }

        }

    }
}