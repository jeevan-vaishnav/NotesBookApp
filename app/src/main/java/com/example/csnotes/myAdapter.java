package com.example.csnotes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class myAdapter extends FirebaseRecyclerAdapter<model,myAdapter.myVHolder> {

    public myAdapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myVHolder myVHolder, int i, @NonNull model model) {

        myVHolder.headerID.setText(model.getFilename());
        myVHolder.textviewbook.setText(String.valueOf(model.getNov()));
        myVHolder.textlike.setText(String.valueOf(model.getNol()));
        myVHolder.textdislike.setText(String.valueOf(model.getNod()));


        myVHolder.imgBookID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myVHolder.imgBookID.getContext(),viewPDF.class);
                intent.putExtra("filename",model.getFilename());
                intent.putExtra("fileurl",model.getFileurl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myVHolder.imgBookID.getContext().startActivity(intent);

            }
        });
    }

    @NonNull
    @Override
    public myVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_design,parent,false);

        return new myVHolder(view);
    }




    public class myVHolder extends RecyclerView.ViewHolder{

        ImageView imgBookID;
        TextView headerID;
        ImageView readBookIMG,likebookIMG,dislikebookIMG;
        TextView textviewbook,textlike,textdislike;

        public myVHolder(@NonNull View itemView) {
            super(itemView);
            imgBookID = itemView.findViewById(R.id.imgBookID);
            headerID = itemView.findViewById(R.id.headerID);

            readBookIMG = itemView.findViewById(R.id.readBook);
            likebookIMG = itemView.findViewById(R.id.likebook);
            dislikebookIMG = itemView.findViewById(R.id.dislikebook);

            textviewbook = itemView.findViewById(R.id.textviewbook);
            textlike = itemView.findViewById(R.id.textlike);
            textdislike = itemView.findViewById(R.id.textdislike);



        }
    }

}
