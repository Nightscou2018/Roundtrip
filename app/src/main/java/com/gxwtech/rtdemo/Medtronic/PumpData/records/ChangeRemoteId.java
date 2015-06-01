package com.gxwtech.rtdemo.Medtronic.PumpData.records;


import com.gxwtech.rtdemo.Medtronic.PumpModel;

public class ChangeRemoteId extends TimeStampedRecord {

    public ChangeRemoteId() {
        calcSize();
    }
    public boolean collectRawData(byte[] data, PumpModel model) {
        if (!super.collectRawData(data, model)) {
            return false;
        }
        return this.decode(data);
    }
}
