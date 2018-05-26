package com.as.airpush;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    List<MyMessage> mList;
    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textNum;
        TextView textName;
        TextView textTime;
        TextView textContent;

        public ViewHolder(View itemView) {
            super(itemView);

            textNum = (TextView) itemView.findViewById(R.id.text_num);
            textName = (TextView)itemView.findViewById(R.id.text_name);
            textTime = (TextView)itemView.findViewById(R.id.text_time);
            textContent = (TextView)itemView.findViewById(R.id.text_content);
        }
    }

    public MessageAdapter(List<MyMessage> mList) {
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent, false );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch(position%3){
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.textNum.setBackground(mContext.getDrawable(R.drawable.item_bg_3));
                }
                break;
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.textNum.setBackground(mContext.getDrawable(R.drawable.item_bg_1));
                }
                break;
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.textNum.setBackground(mContext.getDrawable(R.drawable.item_bg_2));
                }
                break;
        }

        MyMessage myMessage = mList.get(position);
        holder.textNum.setText( (position+1) + "/"+ mList.size());
        holder.textName.setText("" + myMessage.getName());
        holder.textTime.setText("时间/条件： " + myMessage.getTime());
        holder.textContent.setText("备注内容： " + myMessage.getContent());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateList(){
        notifyDataSetChanged();
    }
}
