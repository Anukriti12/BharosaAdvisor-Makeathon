package com.ap.bharosaadvisor.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.bharosaadvisor.R;
import com.ap.bharosaadvisor.data.Transaction;
import com.ap.bharosaadvisor.helper.RequestHelper;
import com.ap.bharosaadvisor.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static com.ap.bharosaadvisor.helper.Utils.SERVICE_URL;
import static com.ap.bharosaadvisor.helper.Utils.USER_ID;

public class WithdrawTransactionsAdapter
        extends RecyclerView.Adapter<WithdrawTransactionsAdapter.ViewHolder>
{
    private Context ctx;
    private ArrayList<Transaction> data;
    private Activity activity;
    private MaterialDialog withdrawDialog;
    private MaterialDialog updateDialog;
    private EditText updateAmount;
    private int curPos = 0;

    public WithdrawTransactionsAdapter(Context _ctx,
                                       MaterialDialog _dialog,
                                       Activity _activity,
                                       ArrayList<Transaction> _data)
    {
        data = _data;
        ctx = _ctx;
        withdrawDialog = _dialog;
        activity = _activity;
        updateDialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.dialog_update_transaction, false)
                .build();

        assert updateDialog.getCustomView() != null;
        updateAmount = updateDialog.getCustomView().findViewById(R.id.update_amount);
        Button updateButton = updateDialog.getCustomView().findViewById(R.id.update_button);
        Button updateCancel = updateDialog.getCustomView().findViewById(R.id.update_cancel);
        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Transaction transaction = data.get(curPos);
                int transactionAmount = 0;
                try
                {
                    transactionAmount = Integer.parseInt(updateAmount.getText().toString().trim());
                } catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }

                if (transactionAmount % 100 != 0)
                {
                    Toast.makeText(activity,
                            R.string.message_amount_multiple,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (transactionAmount == 0)
                {
                    Toast.makeText(activity,
                            R.string.message_amount_invalid,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (Utils.NEW_TRANS)
                {
                    if (transactionAmount < 500)
                    {
                        Toast.makeText(activity,
                                R.string.message_amount_more,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                int totalHoldings = transactionAmount;
                for (Transaction t : Utils.WITHDRAW_TRANSACTIONS)
                {
                    if (transaction != t)
                        totalHoldings += Integer.parseInt(t.data);
                }

                if (totalHoldings > Utils.ALL_UNITS_AMOUNT)
                {
                    Toast.makeText(activity,
                            R.string.withdraw_update_overflow,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                try
                {
                    RequestHelper request = new RequestHelper(activity,
                            SERVICE_URL + "apiwealthsimple/withdrawupdatetransactiondata",
                            true,
                            true);

                    request.execute(new JSONObject()
                            .put("Id", transaction.id)
                            .put("userId", USER_ID)
                            .put("type", transaction.type)
                            .put("amount", transactionAmount)
                    );

                    withdrawDialog.dismiss();
                    updateDialog.dismiss();
                    Toast.makeText(activity,
                            R.string.amount_updated,
                            Toast.LENGTH_LONG).show();
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        updateCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateDialog.dismiss();
            }
        });
    }

    @NonNull
    @Override
    public WithdrawTransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                     int viewType)
    {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_dialog_transaction, parent, false);
        return new WithdrawTransactionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WithdrawTransactionsAdapter.ViewHolder holder,
                                 int position)
    {
        String val = (data.get(position).typeLabel + " (" + data.get(position).dataFormatted + " ₹)");
        holder.infoType.setText(val);

        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new MaterialDialog.Builder(activity)
                        .title(R.string.confirm)
                        .content(R.string.sure_delete)
                        .autoDismiss(false)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                            {
                                Transaction transaction = data.get(holder.getAdapterPosition());
                                try
                                {
                                    RequestHelper request = new RequestHelper(activity,
                                            SERVICE_URL + "apiwealthsimple/deletetransactiondata",
                                            true,
                                            true);

                                    request.execute(new JSONObject().put("Id", transaction.id));
                                    withdrawDialog.dismiss();
                                    dialog.dismiss();
                                } catch (MalformedURLException e)
                                {
                                    e.printStackTrace();
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .show();
            }
        });

        holder.update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                curPos = holder.getAdapterPosition();
                updateAmount.setText(data.get(curPos).data);
                updateDialog.show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView infoType;
        ImageView update;
        ImageView delete;

        ViewHolder(View itemView)
        {
            super(itemView);
            infoType = itemView.findViewById(R.id.transaction_type);
            update = itemView.findViewById(R.id.transaction_update);
            delete = itemView.findViewById(R.id.transaction_delete);
        }
    }
}



