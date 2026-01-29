package com.bcasekuritas.mybest.ext.other

class PagingManager<T> {
    private var dataBuffer = mutableListOf<T>()

    fun updateData(newData: List<T>) {
        dataBuffer = newData.toMutableList()
    }

    // Add new data to the buffer
    fun addData(newData: List<T>) {
        dataBuffer.addAll(newData)
    }

    fun clearData(){
        dataBuffer.clear()
    }

    // Retrieve a paginated chunk of data
    fun getPage(page: Int, pageSize: Int): List<T> {
        val start = page * pageSize
        val end = minOf(start + pageSize, dataBuffer.size)
        return if (start < end) {
            dataBuffer.subList(start, end)
        } else {
            emptyList()
        }
    }

    // Check if more data is available
    fun hasMoreData(page: Int, pageSize: Int): Boolean {
        return page * pageSize < dataBuffer.size
    }
}