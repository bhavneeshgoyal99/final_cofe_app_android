package com.cofe.solution.ui.device.config.powersavingmode.listener;

import java.util.HashMap;

public class PowerSavingModeContract {
    public interface IPowerSavingModeView {
        void onGetConfigResult(HashMap<Object,Object> result,int errorId);
    }

    public interface IPowerSavingModePresenter {

    }
}
