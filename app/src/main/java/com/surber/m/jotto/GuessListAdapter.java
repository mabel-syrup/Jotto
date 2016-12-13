package com.surber.m.jotto;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wo1624bu on 12/6/16.
 */

public class GuessListAdapter extends ArrayAdapter {

    private ArrayList<Guess> guessList;

    private Context context;
    private int layoutResourceID;
    private static LayoutInflater inflater;

    public GuessListAdapter(Context context, int resource, ArrayList<Guess> guesses) {
        super(context, resource);
        this.layoutResourceID = resource;
        this.context = context;
        this.guessList = guesses;
        //This solution brought to you by: http://stackoverflow.com/questions/15832335/android-custom-row-item-for-listview
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        System.out.println("Adapter online.");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(vi == null){
            vi = inflater.inflate(R.layout.guess, null);
        }
        TextView guessText = (TextView) vi.findViewById(R.id.guess_text);
        TextView result = (TextView) vi.findViewById(R.id.guess_points);
        guessText.setText(guessList.get(position).mText);
        result.setText(String.valueOf(guessList.get(position).mMatches));
        return vi;

    }

}
