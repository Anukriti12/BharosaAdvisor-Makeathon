package com.ap.bharosaadvisor.data;

public class Transaction
{
    public String id;
    public String type;
    public String typeLabel;
    public String data;
    public String dataFormatted;

    public Transaction(String _id, String _type, String _label, String _data, String _dataFormatted)
    {
        id = _id;
        type = _type;
        typeLabel = _label;
        data = _data;
        dataFormatted = _dataFormatted;
    }
}
