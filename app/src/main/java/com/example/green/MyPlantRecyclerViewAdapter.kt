package com.example.green

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.example.green.PlantFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_plant.view.*

//Adapter class for plants list
class MyPlantRecyclerViewAdapter(
        private val mValues: List<PlantModel>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyPlantRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PlantModel
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_plant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.name.text = item.commonName
        holder.code.text = item.acceptedSymbol
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.name
        val code: TextView = mView.code
        override fun toString(): String {
            return super.toString() + " '" + "'"
        }
    }
}
