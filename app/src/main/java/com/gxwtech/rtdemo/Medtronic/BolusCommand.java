package com.gxwtech.rtdemo.Medtronic;

/**
 * Created by geoff on 5/15/15.
 */
public class BolusCommand extends MedtronicCommand {
    public BolusCommand() {
        init(MedtronicCommandEnum.CMD_M_BOLUS);
    }
}
