package com.example.vetappointment.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Interfaces.CancelBlockDateCallBack;
import com.example.vetappointment.Models.BlockAppointment;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class BlockDateAdapter extends RecyclerView.Adapter<BlockDateAdapter.BlockDateViewHolder> {

    private Context context;
    private ArrayList<BlockAppointment> blockDates;
    private CancelBlockDateCallBack cancelBlockDateCallBack;


    public BlockDateAdapter(Context context, ArrayList<BlockAppointment> blockDates) {
        this.context = context;
        this.blockDates = blockDates;
    }

    public BlockDateAdapter setCancelBlockDateCallBack(CancelBlockDateCallBack cancelBlockDateCallBack) {
        this.cancelBlockDateCallBack = cancelBlockDateCallBack;
        return this;
    }

    public ArrayList<BlockAppointment> getBlockDates() {
        return blockDates;
    }




    @NonNull
    @Override
    public BlockDateAdapter.BlockDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_block_date, parent, false);
        return new BlockDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockDateAdapter.BlockDateViewHolder holder, int position) {
        BlockAppointment blockDate = getItem(position);
        holder.date.setText(blockDate.getDate());
        holder.reason.setText(blockDate.getReason());



    }

    @Override
    public int getItemCount() {
        return blockDates.size();
    }

    public BlockAppointment getItem(int position) {
        return blockDates.get(position);
    }

    public void setBlockDates(ArrayList<BlockAppointment> blockDate) {
        this.blockDates=blockDate;
        notifyDataSetChanged();
    }

    public class BlockDateViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView date;
        private MaterialTextView reason;
        private FloatingActionButton cancelBlockDate;



        public BlockDateViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_block);
            reason = itemView.findViewById(R.id.reason_details);
            cancelBlockDate = itemView.findViewById(R.id.cancel_BTN);


            cancelBlockDate.setOnClickListener(v -> {
                if (cancelBlockDateCallBack != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Cancel Block Appointment")
                            .setMessage("Are you sure you want to cancel this Block appointment?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                if (cancelBlockDateCallBack != null) {
                                    cancelBlockDateCallBack.cancelBlockDate(getItem(getAdapterPosition()), getAdapterPosition());

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });



        }
    }
}
