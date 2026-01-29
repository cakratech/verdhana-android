package com.bcasekuritas.mybest.app.feature.notification.transactionnotification.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.databinding.ItemNotificationHistoryBinding
import com.bcasekuritas.mybest.ext.common.convertMillisToTimeAgo
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistory

class TransactionNotificationAdapter (): RecyclerView.Adapter<TransactionNotificationAdapter.ItemViewHolder>() {

    private val listData: ArrayList<NotificationHistory> = arrayListOf()
    private val listMenuOption = arrayListOf("Edit", "Delete")

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<NotificationHistory>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: List<NotificationHistory>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemNotificationHistoryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class ItemViewHolder(
        val binding: ItemNotificationHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: NotificationHistory) {
            binding.apply {

                tvNotif.text = data.notifDescription
                tvTimeNotif.text = convertMillisToTimeAgo(data.time)

            }
        }

    }
}