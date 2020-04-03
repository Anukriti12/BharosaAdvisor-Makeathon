package com.ap.bharosaadvisor.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.helper.RequestHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static com.ap.bharosaadvisor.helper.Utils.CHECKLIST_STATUS;
import static com.ap.bharosaadvisor.helper.Utils.SERVICE_URL;
import static com.ap.bharosaadvisor.helper.Utils.USER_ID;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder>
{
    private ArrayList<String> data;
    private ArrayList<CheckBox> checkBoxes;
    private Button checkAllConditionsButton;
    private Context ctx;
    private int checkCount = 0;
    private Activity activity;

    public CheckListAdapter(Activity _activity, Context _ctx, ArrayList<String> _data)
    {
        data = _data;
        activity = _activity;
        checkBoxes = new ArrayList<>();
        ctx = _ctx;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_checklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CheckListAdapter.ViewHolder holder, int position)
    {
        holder.featuresText.setText(data.get(position));

        if (position == data.size() - 1)
        {
            if (!CHECKLIST_STATUS)
            {
                holder.checkAllConditions.setVisibility(View.VISIBLE);
                checkAllConditionsButton = holder.checkAllConditions;
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox featuresText;
        Button checkAllConditions;

        ViewHolder(View itemView)
        {
            super(itemView);
            featuresText = itemView.findViewById(R.id.checklist_feature);
            checkBoxes.add(featuresText);
            checkAllConditions = itemView.findViewById(R.id.checklist_accept);

            if (CHECKLIST_STATUS)
            {
                featuresText.setChecked(true);
                featuresText.setEnabled(false);
            }

            checkAllConditions.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Toast.makeText(ctx, R.string.checklist_accepted, Toast.LENGTH_SHORT).show();
                    view.setVisibility(View.GONE);
                    for (CheckBox c : checkBoxes)
                    {
                        c.setChecked(true);
                        c.setEnabled(false);
                    }
                }
            });

            featuresText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                {
                    if (isChecked)
                        checkCount++;
                    else
                        checkCount--;

                    if (checkCount == data.size())
                    {
                        Toast.makeText(ctx, R.string.checklist_accepted, Toast.LENGTH_SHORT).show();
                        checkAllConditionsButton.setVisibility(View.GONE);

                        for (CheckBox c : checkBoxes)
                            c.setEnabled(false);
                        checklistFlagRequest();
                    }
                }
            });
        }
    }

    private void checklistFlagRequest()
    {
        CHECKLIST_STATUS = true;
        try
        {
            RequestHelper request = new RequestHelper(activity,
                    SERVICE_URL + "apiwealthsimple/activeflag",
                    false,
                    false);
            request.execute(new JSONObject().put("userId", USER_ID));
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}

