package com.gxwtech.rtdemo.medtronic.PumpData.records;


import com.gxwtech.rtdemo.medtronic.PumpModel;

public class BolusWizardChange extends TimeStampedRecord {

    public BolusWizardChange() {

    }

    public boolean collectRawData(byte[] data, PumpModel model) {
        if (!super.collectRawData(data, model)) {
            return false;
        }
        if (model == PumpModel.MM508 || model == PumpModel.MM515) {
            bodySize = 117;
        } else {
            bodySize = 143;
        }
        calcSize();
        return decode(data);
    }
}
