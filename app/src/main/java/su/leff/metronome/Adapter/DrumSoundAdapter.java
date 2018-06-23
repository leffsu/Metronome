package su.leff.metronome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import su.leff.metronome.Activity.Metronome;
import su.leff.metronome.Class.DrumSound;
import su.leff.metronome.R;

public class DrumSoundAdapter extends RecyclerView.Adapter<DrumSoundAdapter.ViewHolder> {

    ArrayList<DrumSound> drumSounds;

    Context context;

    Metronome metronome;

    int activeId = 0;


    public DrumSoundAdapter(ArrayList<DrumSound> drumSounds, Context context, Metronome metronome) {
        this.drumSounds = drumSounds;
        this.context = context;
        this.metronome = metronome;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drum_sound_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(drumSounds.get(position).getName());
        holder.description.setText(drumSounds.get(position).getDescription());
        holder.hiddenSound.setText(String.valueOf(drumSounds.get(position).getSound()));
        holder.hiddenId.setText(String.valueOf(drumSounds.get(position).getId()));
        holder.checkSound.clearColorFilter();
        if (drumSounds.get(position).getSet()) {
            holder.checkSound.setColorFilter(context.getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        return drumSounds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView description;
        public ImageButton checkSound;
        public TextView hiddenSound;
        public TextView hiddenId;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txv_name);
            description = itemView.findViewById(R.id.txv_description);
            checkSound = itemView.findViewById(R.id.imageView2);
            hiddenSound = itemView.findViewById(R.id.hidden_sound);
            hiddenId = itemView.findViewById(R.id.txv_id);
//            checkSound.setOnClickListener(onSoundCheckListener);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            System.out.println(((TextView) v.findViewById(R.id.hidden_sound)).getText().toString());
            String stringToParse = ((TextView) v.findViewById(R.id.hidden_sound)).getText().toString();
            metronome.updateSoundResource(Integer.valueOf(stringToParse));
//            checkSound.setColorFilter(context.getResources().getColor(R.color.colorAccent));
//            for (DrumSound ds: drumSounds
//                 ) {
//                ds.setSet(false);
//            }
            notifyItemChanged(activeId);
            drumSounds.get(activeId).setSet(false);
            drumSounds.get(Integer.valueOf(hiddenId.getText().toString())).setSet(true);
            activeId = Integer.valueOf(hiddenId.getText().toString());
            notifyItemChanged(activeId);
            metronome.setTitle(context.getResources().getString(R.string.app_name) + ": " + drumSounds.get(activeId).getName());
        }

        public void resetColorFilter() {
            checkSound.clearColorFilter();
        }
    }
}
